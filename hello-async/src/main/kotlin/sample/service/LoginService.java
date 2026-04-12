package sample.service;

import sample.UserRepository;

public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        String pw =  userRepository.findById(1L);
        return password.equals(pw);
    }
}
