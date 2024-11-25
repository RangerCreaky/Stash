package com.stash.stash.services;

import com.stash.stash.constants.ResponseStatusEnum;
import com.stash.stash.constants.RoleEnum;
import com.stash.stash.dto.APIDataResponse;
import com.stash.stash.dto.request.AddStashRequestDTO;
import com.stash.stash.dto.request.AddUserToStashRequestDTO;
import com.stash.stash.dto.request.CloneStashDTO;
import com.stash.stash.dto.request.UpdateStashRequestDTO;
import com.stash.stash.entities.Stash;
import com.stash.stash.entities.User;
import com.stash.stash.entities.UserStashMapping;
import com.stash.stash.exceptions.ForbiddenActionException;
import com.stash.stash.exceptions.StashNotFoundException;
import com.stash.stash.exceptions.UserNotFoundException;
import com.stash.stash.repositories.StashRepository;
import com.stash.stash.repositories.StashUserMappingRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class StashService {
    @Autowired
    private StashRepository stashRepository;

    @Autowired
    private StashUserMappingRepository userStashMappingRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public APIDataResponse addStash(Authentication authentication, AddStashRequestDTO stashRequestDTO) throws UserNotFoundException {
        User user = userService.getUser(authentication);

        Stash stash = Stash.builder()
                .name(stashRequestDTO.getName())
                .category(stashRequestDTO.getCategory())
                .description(stashRequestDTO.getDescription())
                .createdBy(user.getId())
                .isPrivate(true)
                .users(new HashSet<>())
                .build();

        stash.getUsers().add(user);
        // save stash
        Stash savedStash = stashRepository.save(stash);

        // save user
        user.getStashes().add(stash);
        userService.saveUser(user);

        // update role of the user
        UserStashMapping mapping = userStashMappingRepository.findByStashIdAndUserId(savedStash.getId(), user.getId());
        mapping.setRole(RoleEnum.OWNER);

        userStashMappingRepository.save(mapping);

        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(savedStash)
                .message("New stash added")
                .build();
    }

    public boolean hasStashAssigned(Long id, Long userId){
        return userStashMappingRepository.existsByStashIdAndUserId(id, userId);
    }

    @Transactional
    public APIDataResponse updateStash(Authentication authentication, UpdateStashRequestDTO stashRequestDTO, Long id) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        User user = userService.getUser(authentication);

        // check if stash exists
        Optional<Stash> optionalStash = this.findStash(id);

        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        Stash stash = optionalStash.get();

        // Check if the user has the stash assigned to him
        boolean hasStashAssigned = this.hasStashAssigned(id, user.getId());
        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        RoleEnum role = userStashMappingRepository.findRoleByUserIdAndStashId(stash.getId(), user.getId());
        if(!(role.equals(RoleEnum.OWNER) || role.equals(RoleEnum.COOWNER))){
            throw new ForbiddenActionException("Permission denied to update the stash");
        }

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(stashRequestDTO, stash);
        this.saveStash(stash);

        return APIDataResponse.builder()
                .message("Stash updated successfully")
                .responseStatus(ResponseStatusEnum.UPDATED)
                .data(stash)
                .build();
    }

    @Transactional
    public APIDataResponse addUserToStash(Authentication authentication, AddUserToStashRequestDTO addUserToStashRequestDTO) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        if(addUserToStashRequestDTO.getRole().equals(RoleEnum.OWNER)){
            throw new ForbiddenActionException("Cannot set user as OWNER");
        }

        User user = userService.getUser(authentication);

        Long stashId = addUserToStashRequestDTO.getStashId();
        Optional<Stash> stash = this.findStash(stashId);

        // Checks
        if(stash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }
        boolean hasStashAssigned = this.hasStashAssigned(stash.get().getId(), user.getId());
        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        User newUser = userService.getUserByEmail(addUserToStashRequestDTO.getEmail());
        newUser.getStashes().add(stash.get());
        User savedNewUser = userService.saveUser(newUser);

        UserStashMapping mapping = userStashMappingRepository.findByStashIdAndUserId(stashId, savedNewUser.getId());
        mapping.setRole(addUserToStashRequestDTO.getRole());

        userStashMappingRepository.save(mapping);

        return APIDataResponse.builder()
                .message("Stash added to user")
                .responseStatus(ResponseStatusEnum.UPDATED)
                .build();
    }


    @Transactional
    public APIDataResponse getAllStashes(Authentication authentication) throws UserNotFoundException {
        User user = userService.getUser(authentication);
        System.out.println("================================================================");
        System.out.println(user);
        // return all the stashes of the user
        Set<Stash> stashes = user.getStashes();
        System.out.println(stashes);

        return APIDataResponse.builder()
                .data(stashes)
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .build();
    }

    @Transactional
    public APIDataResponse getPaginatedStashes(Authentication authentication, Integer page, Integer size) throws UserNotFoundException {
        User user = userService.getUser(authentication);

        Pageable pageable = PageRequest.of(page, size);
        Slice<Stash> stashes = stashRepository.findByUserId(user.getId(), pageable);

        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(stashes)
                .build();
    }

    @Transactional
    public APIDataResponse getStashById(Authentication authentication, Long stashId) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        User user = userService.getUser(authentication);

        Optional<Stash> stash = this.findStash(stashId);

        if(stash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        boolean hasStashAssigned = this.hasStashAssigned(stashId, user.getId());
        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }
        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(stash.get())
                .build();
    }

    public Optional<Stash> findStash(Long id) {
        return stashRepository.findById(id);
    }

    public Stash saveStash(Stash stash){
        return stashRepository.save(stash);
    }

    public boolean stashExists(Long id){
        return stashRepository.existsById(id);
    }

    public RoleEnum getRole(Long stashId , Long userId){
        return userStashMappingRepository.findRoleByUserIdAndStashId(stashId, userId);
    }

    public UserStashMapping getUserStashMapping(Long stashId, Long userId){
        return userStashMappingRepository.findByStashIdAndUserId(stashId, userId);
    }

    public void saveUserStashMapping(UserStashMapping mapping){
        userStashMappingRepository.save(mapping);
    }
}
