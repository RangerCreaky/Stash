package com.stash.stash.controllers;

import com.stash.stash.dto.APIResponse;
import com.stash.stash.exceptions.UserNotAuthenticated;
import com.stash.stash.exceptions.UserNotFoundException;
import com.stash.stash.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public APIResponse profile(Authentication authentication) throws UserNotFoundException {
        return userService.getProfile(authentication);
    }

    @GetMapping("/auth/status")
    public APIResponse authStatus(HttpSession session) throws UserNotAuthenticated {
        return userService.getAuthStatus(session);
    }
}
