package denysserdiuk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import denysserdiuk.model.Users;
import denysserdiuk.repository.UserRepository;

@Service
public class UserCreationServiceImp implements UserCreationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserCreationServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createUser(Users users) {

        String encodedPassword = passwordEncoder.encode(users.getPassword());
        Users users1 = new Users();
        users1.setUsername(users.getUsername());
        users1.setEmail(users.getEmail());
        users1.setName(users.getName());
        users1.setPassword(encodedPassword);

        userRepository.save(users1);
        return "Users +" + users.getUsername() + " created";
    }
}

