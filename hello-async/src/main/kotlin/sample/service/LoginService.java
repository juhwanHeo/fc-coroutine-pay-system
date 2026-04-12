package sample.service;

import sample.UserRepository;

public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean loginv2(String username, String password) {
        String pw =  userRepository.findById(2L);
        return password.equals(pw);
    }
}
