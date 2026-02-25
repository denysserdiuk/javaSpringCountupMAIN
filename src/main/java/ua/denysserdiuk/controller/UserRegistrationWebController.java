package denysserdiuk.controller;

import denysserdiuk.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import denysserdiuk.model.Users;
import denysserdiuk.model.VerificationToken;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.UserCreationService;
import denysserdiuk.utils.EmailService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class UserRegistrationWebController {

    private final UserCreationService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public UserRegistrationWebController(UserCreationService userService, UserRepository userRepository, EmailService emailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/CreateUser")
    public String createUser(@ModelAttribute UserRegistrationDto userDto, Model model, HttpSession session) throws IOException {

        // 2. Validate Password Match inside the DTO context
        if (!userDto.getPassword().equals(userDto.getRepeatPassword())) {
            model.addAttribute("passwordError", "Passwords do not match");
            return "register";
        }

        // 3. Check uniqueness using values from DTO
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            model.addAttribute("usernameError", "Username is not unique");
            return "register";
        }

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            model.addAttribute("emailError", "Email is not unique");
            return "register";
        }

        // 4. SAFER: Map DTO to Entity manually
        // We only copy the fields we trust. If a hacker sent "balance", we ignore it.
        Users newUser = new Users();
        newUser.setUsername(userDto.getUsername());
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setPassword(userDto.getPassword());
        // Note: Balance and Role are NOT set here, so they default to 0/User. Safe!

        int verificationCode = new Random().nextInt(900000) + 100000;
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);

        // 5. Pass the safe 'newUser' entity to the logic
        VerificationToken token = new VerificationToken(newUser, verificationCode, expiryDate);
        session.setAttribute("verificationToken", token);

        emailService.sendVerificationEmail(newUser.getEmail(), verificationCode);

        model.addAttribute("verificationPending", true);

        return "register";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam("verification") int verificationCodeInput, HttpSession session, Model model) {
        VerificationToken token = (VerificationToken) session.getAttribute("verificationToken");

        if (token != null) {
            if (LocalDateTime.now().isAfter(token.getExpiryDate())) {
                model.addAttribute("verificationError", "Verification code has expired. Please request a new one.");
                model.addAttribute("verificationPending", true);
                return "register";
            }

            if (token.getCode() == verificationCodeInput) {
                try {
                    userService.createUser(token.getUser());
                    session.removeAttribute("verificationToken");
                    return "redirect:/login";
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("registrationError", "An error occurred during registration.");
                    return "register";
                }
            } else {
                model.addAttribute("verificationError", "Invalid verification code");
                model.addAttribute("verificationPending", true);
                return "register";
            }
        } else {
            model.addAttribute("verificationError", "No verification code found. Please register again.");
            return "register";
        }
    }

    @PostMapping("/resendVerificationCode")
    @ResponseBody
    public ResponseEntity<String> resendVerificationCode(HttpSession session) throws IOException {
        VerificationToken token = (VerificationToken) session.getAttribute("verificationToken");
        System.out.println("Second code sent");

        if (token != null) {
            int newCode = new Random().nextInt(900000) + 100000;
            token.setCode(newCode);
            token.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // Reset expiry
            session.setAttribute("verificationToken", token);

            emailService.sendVerificationEmail(token.getUser().getEmail(), newCode);
            return ResponseEntity.ok("Verification code resent");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No registration in progress");
        }
    }


}
