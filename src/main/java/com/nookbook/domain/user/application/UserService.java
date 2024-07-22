package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
