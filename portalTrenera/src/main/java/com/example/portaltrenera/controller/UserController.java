package com.example.portaltrenera.controller;

import com.example.portaltrenera.dto.UserDto;
import com.example.portaltrenera.dto.UserDtoMapper;
import com.example.portaltrenera.dto.UserPostDto;
import com.example.portaltrenera.dto.UserPostDtoMapper;
import com.example.portaltrenera.model.User;
import com.example.portaltrenera.payload.response.UserInfoResponse;
import com.example.portaltrenera.service.PostService;
import com.example.portaltrenera.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin(origins ="*", maxAge = 3600)
@RestController
@RequestMapping("/api")
//@CrossOrigin("http://localhost:3000/") // -- for react
public class UserController {

    public final UserService userService;
    public final PostService postService;
    public static final Logger LOGGER = LogManager.getLogger(UserController.class);

    public UserController(UserService userService,
                          PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getUsers() {
        LOGGER.info("Admin accessed method: getUsers()");
        return UserDtoMapper.MapToUserDtos(userService.getUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public UserPostDto getSingleUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(id)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                        + getAccessUserId(auth)
                        + " tried to access user's(id): "
                        + id
                        + " account!");
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User(id) " + id + " accessed his account data.");
            return UserPostDtoMapper.MapToUserPostDto(userService.getSingleUser(id));
        }
        LOGGER.info("Admin accessed user's(id) " + id + " account.");
        return UserPostDtoMapper.MapToUserPostDto(userService.getSingleUser(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(id)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                        + getAccessUserId(auth)
                        + " tried to edit user's(id): "
                        + id
                        + " account!");
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User(id) " + id + " edited his account.\nfirst name: " + user.getFirstName()
                    + "\nlast name: " + user.getLastName());
            UserDto responseBody = UserDtoMapper.MapToUserDto(userService.editUser(id, user));
            return ResponseEntity.ok().body( responseBody );
        }
        LOGGER.info("Admin edited user's(id): " + id + " account.\nfirst name: " + user.getFirstName()
                + "\nlast name: " + user.getLastName());
        UserDto responseBody = UserDtoMapper.MapToUserDto(userService.editUser(id, user));
        return ResponseEntity.ok().body( responseBody );
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public void deleteUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            if (!getAccessUserId(auth).equals(id)) {
                LOGGER.warn(
                        "Unauthorized access attempt - user with id: "
                        + getAccessUserId(auth)
                        + " tried to delete user's(id): "
                        + id
                        + " account!");
                throw new AccessDeniedException("Unauthorized");
            }
            LOGGER.info("User(id) " + id + " deleted his account!)");
            userService.deleteUser(id);
        }
        LOGGER.info("Admin deleted account - user(id): " + id);
        userService.deleteUser(id);
    }

    private Long getAccessUserId(Authentication auth) {
        User user = (User) Objects.requireNonNull(auth).getPrincipal();
        return user.getId();
    }
}
