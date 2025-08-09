package com.github.fecrol.yasm.user.services;

import com.github.fecrol.yasm.comon.exceptions.BadRequestException;
import com.github.fecrol.yasm.comon.exceptions.NotFoundException;
import com.github.fecrol.yasm.user.entities.User;
import com.github.fecrol.yasm.user.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User with id of " + id + " not found"));
    }

    public User createNewUser(User newUser) {
        List<String> illegalFieldsList = new ArrayList<>();
        if(newUser.getId() != null) illegalFieldsList.add("id");
        if(illegalFieldsList.isEmpty()) return userRepository.save(newUser);
        else throw new BadRequestException("Illegal fields provided: " + illegalFieldsList);
    }

    public User updateExistingUser(UUID id, User updatedUser) {
        User user = getUser(id);
        user.updateUsing(updatedUser);
        return userRepository.save(user);
    }

    public User deleteExistingUser(UUID id) {
        User user = getUser(id);
        userRepository.delete(user);
        return user;
    }
}
