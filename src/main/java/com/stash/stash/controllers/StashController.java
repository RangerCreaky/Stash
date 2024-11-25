package com.stash.stash.controllers;

import com.stash.stash.dto.APIResponse;
import com.stash.stash.dto.request.AddStashRequestDTO;
import com.stash.stash.dto.request.AddUserToStashRequestDTO;
import com.stash.stash.dto.request.CloneStashDTO;
import com.stash.stash.dto.request.UpdateStashRequestDTO;
import com.stash.stash.exceptions.ForbiddenActionException;
import com.stash.stash.exceptions.StashNotFoundException;
import com.stash.stash.exceptions.UserNotFoundException;
import com.stash.stash.services.StashService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stash")
public class StashController {

    @Autowired
    private StashService stashService;

    @PostMapping("/add")
    public APIResponse add(Authentication authentication, @RequestBody @Valid AddStashRequestDTO stashRequestDTO) throws UserNotFoundException {
        // add
        return stashService.addStash(authentication, stashRequestDTO);
    }

    @PatchMapping("/update/{id}")
    public APIResponse update(Authentication authentication, @PathVariable Long id, @RequestBody UpdateStashRequestDTO stashRequestDTO) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        // update
        return stashService.updateStash(authentication, stashRequestDTO, id);
    }

    @PostMapping("/add-user")
    public APIResponse addUserToStash(Authentication authentication, @RequestBody @Valid AddUserToStashRequestDTO addUserToStashRequestDTO) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        // add user
        return stashService.addUserToStash(authentication, addUserToStashRequestDTO);
    }

    @GetMapping("/")
    public APIResponse getAllStashes(Authentication authentication) throws UserNotFoundException {
        return stashService.getAllStashes(authentication);
    }

    @GetMapping("/paginated")
    public APIResponse getPaginatedStashes(Authentication authentication, @RequestParam Integer page, @RequestParam Integer size) throws UserNotFoundException {
        return stashService.getPaginatedStashes(authentication, page, size);
    }

    @GetMapping("/{stashId}")
    public APIResponse getStashById(Authentication authentication, @PathVariable Long stashId) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        return stashService.getStashById(authentication, stashId);
    }
}
