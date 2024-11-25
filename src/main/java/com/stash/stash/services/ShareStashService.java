package com.stash.stash.services;

import com.stash.stash.constants.ResponseStatusEnum;
import com.stash.stash.dto.APIDataResponse;
import com.stash.stash.entities.ShareStash;
import com.stash.stash.entities.Stash;
import com.stash.stash.entities.User;
import com.stash.stash.exceptions.*;
import com.stash.stash.repositories.ShareStashRepository;
import com.stash.stash.utils.GenerateToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
public class ShareStashService {
    @Autowired
    private StashService stashService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShareStashRepository shareStashRepository;

    @Transactional
    public APIDataResponse getShareLink(Authentication authentication, Long stashId) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        User user = userService.getUser(authentication);
        Optional<Stash> optionalStash = stashService.findStash(stashId);
        if(optionalStash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        Stash stash = optionalStash.get();

        boolean hasStashAssigned = stashService.hasStashAssigned(stashId, user.getId());

        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        String token = GenerateToken.generateToken();

        ShareStash shareStash = ShareStash.builder()
                .token(token)
                .createdAt(LocalDate.now())
                .stash(stash)
                .build();

        ShareStash savedShareStash = shareStashRepository.save(shareStash);

        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.CREATED)
                .data(savedShareStash)
                .build();

    }

    @Transactional
    public APIDataResponse deleteShareLink(Authentication authentication, Long stashId) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException, TokenNotFoundException {
        User user = userService.getUser(authentication);
        Optional<Stash> stash = stashService.findStash(stashId);

        if(stash.isEmpty()){
            throw new StashNotFoundException("Stash not found");
        }

        boolean hasStashAssigned = stashService.hasStashAssigned(stashId, user.getId());
        if(!hasStashAssigned){
            throw new ForbiddenActionException("Permission denied");
        }

        Optional<ShareStash> optionalShareStash = shareStashRepository.findByStashId(stashId);
        if(optionalShareStash.isEmpty()){
            throw new TokenNotFoundException("Token not found");
        }

        ShareStash shareStash = optionalShareStash.get();
        shareStash.setToken(null);

        return APIDataResponse.builder()
                .message("Token removed")
                .responseStatus(ResponseStatusEnum.DELETED)
                .build();
    }

    public Stash findByToken(String token) throws InvalidShareTokenException {
        Optional<Stash> stash = shareStashRepository.findByToken(token);
        if(stash.isEmpty()){
            throw new InvalidShareTokenException("Token invalid");
        }

        return stash.get();
    }


}
