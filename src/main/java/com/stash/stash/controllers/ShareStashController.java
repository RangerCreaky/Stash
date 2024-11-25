package com.stash.stash.controllers;

import com.stash.stash.dto.APIResponse;
import com.stash.stash.dto.request.CloneStashDTO;
import com.stash.stash.exceptions.ForbiddenActionException;
import com.stash.stash.exceptions.StashNotFoundException;
import com.stash.stash.exceptions.TokenNotFoundException;
import com.stash.stash.exceptions.UserNotFoundException;
import com.stash.stash.services.ShareStashService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/share")
public class ShareStashController {
    @Autowired
    private ShareStashService shareStashService;
    @GetMapping(value="/{stashId}")
    public APIResponse getShareLink(Authentication authentication, @PathVariable Long stashId) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        return shareStashService.getShareLink(authentication, stashId);
    }

    @GetMapping(value = "/delete/{stashId}")
    public APIResponse deleteShareLink(Authentication authentication, @PathVariable Long stashId) throws UserNotFoundException, ForbiddenActionException, TokenNotFoundException, StashNotFoundException {
        return shareStashService.deleteShareLink(authentication, stashId);
    }
}
