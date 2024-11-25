package com.stash.stash.controllers;

import com.stash.stash.dto.APIResponse;
import com.stash.stash.entities.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/")
    public String greet(){
        return "hello world!";
    }

    @GetMapping("/secure")
    public String secure(){
        return "secure";
    }
}
