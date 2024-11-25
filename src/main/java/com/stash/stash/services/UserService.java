package com.stash.stash.services;

import com.stash.stash.constants.ResponseStatusEnum;
import com.stash.stash.dto.APIDataResponse;
import com.stash.stash.dto.response.UserProfileResponseDTO;
import com.stash.stash.entities.User;
import com.stash.stash.exceptions.UserNotAuthenticated;
import com.stash.stash.exceptions.UserNotFoundException;
import com.stash.stash.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUser(Authentication authentication) throws UserNotFoundException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oauth2User.getAttributes().get("email");

        return this.getUserByEmail(email);
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        return user.get();
    }

    public User saveUser(User user){
        userRepository.save(user);
        return user;
    }
    
    public APIDataResponse getAuthStatus(HttpSession session) throws UserNotAuthenticated {
        OAuth2User user = (OAuth2User) session.getAttribute("user");
        if(user == null){
            throw new UserNotAuthenticated("user not authenticated");
        }
        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(Map.of("isAuthenticated", true, "user", user))
                .build();

    }

    public APIDataResponse getProfile(Authentication authentication) throws UserNotFoundException{
        User user = getUser(authentication);
        UserProfileResponseDTO userProfileResponseDTO = UserProfileResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .bio(user.getBio())
                .email(user.getEmail())
                .build();
        return APIDataResponse.builder()
                .responseStatus(ResponseStatusEnum.SUCCESS)
                .data(userProfileResponseDTO)
                .build();
    }

}
