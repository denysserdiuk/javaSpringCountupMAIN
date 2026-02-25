package denysserdiuk.controller;

import denysserdiuk.dto.UserRegistrationDto; // Import the DTO
import denysserdiuk.model.Users;
import denysserdiuk.service.UserCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserCreationService userService;

    @Autowired
    public UserRestController(UserCreationService userService) {
        this.userService = userService;
    }

    @PostMapping("/CreateUser")
    public ResponseEntity<String> createUser(@RequestBody UserRegistrationDto userDto) {

        // 1. Validation: Basic check (You can add more advanced validation later)
        if (userDto.getPassword() == null || !userDto.getPassword().equals(userDto.getRepeatPassword())) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        Users newUser = new Users();
        newUser.setUsername(userDto.getUsername());
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setPassword(userDto.getPassword());


        try {
            String responseMessage = userService.createUser(newUser);
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error registering user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}