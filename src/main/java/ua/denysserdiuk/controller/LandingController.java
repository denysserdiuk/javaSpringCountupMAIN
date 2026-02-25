package denysserdiuk.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import denysserdiuk.utils.EmailService;
import denysserdiuk.model.ContactForm;

import java.io.IOException;


@Controller
public class LandingController {


    @Autowired
    private EmailService emailService;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "index";
    }

    @GetMapping("/")
    public String showIndexPage(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/contact")
    public String submitContactForm(@Valid @ModelAttribute("contactForm") ContactForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "index";
        }

        emailService.sendContactEmail(form.getName(), form.getEmail(), form.getPhone(), form.getMessage());
        model.addAttribute("successMessage", "Your message has been sent successfully!");
        model.addAttribute("contactForm", new ContactForm()); // Reset the form

        return "index";
    }
}


