package com.stash.stash.controllers;

import com.stash.stash.dto.APIResponse;
import com.stash.stash.dto.request.AddLinkRequestDTO;
import com.stash.stash.dto.request.CloneStashDTO;
import com.stash.stash.dto.request.UpdateLinkRequestDTO;
import com.stash.stash.exceptions.*;
import com.stash.stash.services.LinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping("/add/{id}")
    public APIResponse add(Authentication authentication, @PathVariable Long id,  @RequestBody @Valid AddLinkRequestDTO linkRequestDTO) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        return linkService.add(authentication, id, linkRequestDTO);
    }

    @PatchMapping("/update/{stashId}")
    public APIResponse update(Authentication authentication, @PathVariable Long stashId, @RequestBody @Valid UpdateLinkRequestDTO updateLinkRequestDTO) throws UserNotFoundException, LinkNotFoundException, ForbiddenActionException, StashNotFoundException {
        return linkService.update(authentication, stashId, updateLinkRequestDTO);
    }

    @GetMapping("/{stashId}")
    public APIResponse getAll(Authentication authentication, @PathVariable Long stashId) throws UserNotFoundException, StashNotFoundException, ForbiddenActionException {
        return linkService.getAll(authentication, stashId);
    }

    @GetMapping("/paginated/{stashId}")
    public APIResponse getPaginated(Authentication authentication, @PathVariable Long stashId, @RequestParam Integer page, @RequestParam Integer size) throws UserNotFoundException, ForbiddenActionException, StashNotFoundException {
        return linkService.getAllPaginated(authentication, stashId, page, size);
    }

    @PostMapping(value="/clone-stash")
    public APIResponse cloneStash(Authentication authentication, @RequestBody @Valid CloneStashDTO cloneStashDTO) throws UserNotFoundException, InvalidShareTokenException, StashNotFoundException {
        return linkService.cloneStash(authentication, cloneStashDTO);
    }

    @GetMapping(value="/")
    public APIResponse getLinkById(Authentication authentication, @RequestParam Long stashId, @RequestParam Long linkId) throws UserNotFoundException, ForbiddenActionException, LinkNotFoundException, StashNotFoundException {
        return linkService.getLinkById(authentication, stashId, linkId);
    }
}
