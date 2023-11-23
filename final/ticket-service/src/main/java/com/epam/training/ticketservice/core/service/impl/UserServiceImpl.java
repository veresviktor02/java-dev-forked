package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.dto.UserDto;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserDto loggedInUser = null;

    @Override
    public Optional<UserDto> login(String name, String password) {
        Optional<User> userOptional =userRepository.findByUsername(name);

        if(userOptional.isPresent() && isValidCredentials(userOptional.get(), password)) {
            loggedInUser = new UserDto(userOptional.get().getUsername(), userOptional.get().getRole());

            return describeAccount();
        }

        return Optional.empty();
    }

    @Override
    public boolean isValidCredentials(User user, String password) {
        return user.getPassword().equals(password) &&
                user.getRole().equals(User.Role.ADMIN);
    }

    @Override
    public Optional<UserDto> logout() {
        Optional<UserDto> userDtoOptional = describeAccount();

        loggedInUser = null;

        return userDtoOptional;
    }

    @Override
    public Optional<UserDto> describeAccount() {
        return Optional.ofNullable(loggedInUser);
    }
}
