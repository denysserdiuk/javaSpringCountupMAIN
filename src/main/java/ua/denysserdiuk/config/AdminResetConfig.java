package denysserdiuk.config;

import denysserdiuk.model.Users;
import denysserdiuk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminResetConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner resetAdminPassword() {
        return args -> {
            Users admin = userRepository.findByUsername(adminUsername);

            // The password you want to use to login
            String freshPassword = adminPassword;

            if (admin != null) {
                // If admin exists, UPDATE the password (so you know what it is)
                admin.setPassword(passwordEncoder.encode(freshPassword));
                userRepository.save(admin);
                System.out.println("✅ ADMIN FOUND. Password reset to: " + freshPassword);
            } else {
                // If admin does not exist, CREATE it
                Users newAdmin = new Users();
                newAdmin.setUsername(adminUsername);
                newAdmin.setPassword(passwordEncoder.encode(freshPassword));
                newAdmin.setEmail("admin@localhost.com"); // Dummy email
                newAdmin.setName("System Admin");
                userRepository.save(newAdmin);
                System.out.println("✅ ADMIN CREATED. Username: " + adminUsername + " | Password: " + freshPassword);
            }
        };
    }
}