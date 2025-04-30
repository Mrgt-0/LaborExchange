package org.example.Service;

import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import org.example.DTO.UserDTO;
import org.example.Enum.UserType;
import org.example.Mapper.UserMapper;
import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new RuntimeException("Пользователь с таким email уже существует: " + userDTO.getEmail());

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);

        User registeredUser = userMapper.toEntity(userDTO);

        String userTypeString = userDTO.getUsertype();
        if (userTypeString == null || userTypeString.isEmpty()) {
            throw new RuntimeException("Тип пользователя не может быть пустым.");
        }

        try {
            registeredUser.setUsertype(String.valueOf(UserType.valueOf(userTypeString.trim().toUpperCase())));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Некорректный тип пользователя: " + userTypeString);
        }
        registeredUser = userRepository.save(registeredUser);
        logger.info("Пользователь успешно зарегистрирован: {}", registeredUser.getEmail());
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + email));
        return userMapper.toDTO(user);
    }

    public UserDTO findUserById(Long userId) {
        User user = userRepository.getById(userId);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void updateUser(String email, User updatedUser) throws SystemException {
        userRepository.findByEmail(email)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setLastname(updatedUser.getLastname());
                    user.setEmail(updatedUser.getEmail());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}