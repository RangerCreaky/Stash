package com.stash.stash.services;

import com.stash.stash.constants.ResponseStatusEnum;
import com.stash.stash.constants.RoleEnum;
import com.stash.stash.dto.APIDataResponse;
import com.stash.stash.dto.request.AddLinkRequestDTO;
import com.stash.stash.dto.request.CloneStashDTO;
import com.stash.stash.dto.request.UpdateLinkRequestDTO;
import com.stash.stash.entities.Link;
import com.stash.stash.entities.Stash;
import com.stash.stash.entities.User;
import com.stash.stash.entities.UserStashMapping;
import com.stash.stash.exceptions.*;
import com.stash.stash.repositories.LinkRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LinkService {
    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StashService stashService;

    @Autowired
    private ShareStashService shareStashService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public APIDataResponse add(Authentication authentication, Long id, AddLinkRequestDTO linkRequestDTO) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        User user = userService.getUser(authentication);

        Optional<Stash> optionalStash = stashService.findStash(id);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }
        Stash stash = optionalStash.get();
        boolean hasStashAssigned = stashService.hasStashAssigned(id, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("permission denied");
        }

        RoleEnum role = stashService.getRole(stash.getId(), user.getId());

        if(!( role.equals(RoleEnum.COOWNER) || role.equals(RoleEnum.OWNER))){
            throw new ForbiddenActionException("permission denied to add the link");
        }

        Link link = modelMapper.map(linkRequestDTO, Link.class);

        link.setStash(stash);

        Link savedLink = linkRepository.save(link);
        return APIDataResponse.builder()
                .message("Link added")
                .data(savedLink)
                .responseStatus(ResponseStatusEnum.CREATED)
                .build();
    }

    @Transactional
    public APIDataResponse update(Authentication authentication, Long id, UpdateLinkRequestDTO linkRequestDTO) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException, LinkNotFoundException {
        User user = userService.getUser(authentication);

        Optional<Stash> optionalStash = stashService.findStash(id);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }
        Stash stash = optionalStash.get();
        boolean hasStashAssigned = stashService.hasStashAssigned(id, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        RoleEnum role = stashService.getRole(stash.getId(), user.getId());

        if(!( role.equals(RoleEnum.COOWNER) || role.equals(RoleEnum.OWNER))){
            throw new ForbiddenActionException("permission denied to add the link");
        }

        Optional<Link> optionalLink = linkRepository.findById(linkRequestDTO.getId());
        if (optionalLink.isEmpty()){
            throw new LinkNotFoundException("Link not found");
        }

        Link link = optionalLink.get();

        if(!Objects.equals(stash.getId(), link.getId())){
            throw new ForbiddenActionException("Permission denied");
        }

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(linkRequestDTO, link);

        Link savedLink = linkRepository.save(link);

        return APIDataResponse.builder()
                .data(savedLink)
                .message("Link updated successfully")
                .responseStatus(ResponseStatusEnum.UPDATED)
                .build();
    }

    @Transactional
    public APIDataResponse getAll(Authentication authentication, Long id) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        User user = userService.getUser(authentication);

        Optional<Stash> optionalStash = stashService.findStash(id);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        Stash stash = optionalStash.get();

        boolean hasStashAssigned = stashService.hasStashAssigned(id, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(stash.getLinks())
                .build();
    }

    @Transactional
    public APIDataResponse getAllPaginated(Authentication authentication, Long stashId, Integer page, Integer size) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        User user = userService.getUser(authentication);

        Optional<Stash> optionalStash = stashService.findStash(stashId);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        boolean hasStashAssigned = stashService.hasStashAssigned(stashId, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        Pageable pageable = PageRequest.of(page, size);

        Slice<Link> links = linkRepository.findAllByStashId(stashId, pageable);

        return APIDataResponse.builder()
                .data(links)
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .build();
    }

    @Transactional
    public APIDataResponse cloneStash(Authentication authentication, CloneStashDTO cloneStashDTO) throws UserNotFoundException, InvalidShareTokenException, StashNotFoundException {
        // TODO<IMPLEMENT>: Check if you have links are being saved. If yes change this to Stash Service. Else deep copy the links as well.

        // TODO<IMPLEMENT>: Make Stash as the parent in the many to many of User stash. and also update teh add stash method
        User user = userService.getUser(authentication);
        String token = cloneStashDTO.getToken();
        Stash stash = shareStashService.findByToken(token);
        boolean stashExists = stashService.stashExists(stash.getId());

        if(!stashExists){
            throw new StashNotFoundException("stash not found");
        }

        Stash newStash = Stash.builder()
                .name(stash.getName())
                .category(stash.getCategory())
                .description(stash.getDescription())
                .users(new HashSet<User>())
                .createdBy(user.getId())
                .links(Collections.emptySet())
                .build();
        newStash.getUsers().add(user);

        Stash savedStash = stashService.saveStash(newStash);

        user.getStashes().add(savedStash);
        userService.saveUser(user);

        UserStashMapping mapping = stashService.getUserStashMapping(savedStash.getId(), user.getId());
        mapping.setRole(RoleEnum.OWNER);
        stashService.saveUserStashMapping(mapping);

        Set<Link> newLinks = stash.getLinks().stream()
        .map(ogLink -> Link.builder()
                .note(ogLink.getNote())
                .name(ogLink.getName())
                .link(ogLink.getLink())
                .description(ogLink.getDescription())
                .stash(savedStash)
                .build())
        .collect(Collectors.toSet());

        linkRepository.saveAll(newLinks);

        System.out.println(savedStash.getLinks());

        return APIDataResponse.builder()
                .data(savedStash)
                .responseStatus(ResponseStatusEnum.CREATED)
                .message("Cloned Stash")
                .build();
    }

    @Transactional
    public APIDataResponse getLinkById(Authentication authentication, Long stashId, Long linkId) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException, LinkNotFoundException {
        User user = userService.getUser(authentication);

        Optional<Stash> optionalStash = stashService.findStash(stashId);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        boolean hasStashAssigned = stashService.hasStashAssigned(stashId, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        Optional<Link> optionalLink = linkRepository.findByLinkIdAndStashId(stashId, linkId);

        if(optionalLink.isEmpty()){
            throw new LinkNotFoundException("Link not found");
        }

        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(optionalLink.get())
                .build();
    }
}
