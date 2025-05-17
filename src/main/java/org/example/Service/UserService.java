package org.example.Service;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import org.example.DTO.UserDTO;
import org.example.DTO.UserTypeDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.UserTypeMapper;
import org.example.Model.User;
import org.example.Model.UserType;
import org.example.Repository.NotificationRepository;
import org.example.Repository.UserRepository;
import org.example.Repository.UserTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private UserTypeMapper userTypeMapper;
    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public void registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new RuntimeException("Пользователь с таким email уже существует: " + userDTO.getEmail());

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        User registeredUser = userMapper.toEntity(userDTO);
        Set<UserType> userTypes = userDTO.getUserTypes();
        if (userTypes == null || userTypes.isEmpty())
            throw new RuntimeException("Тип пользователя не может быть пустым.");

        Set<UserType> savedUserTypes = new HashSet<>();
        for (UserType userType : userTypes) {
            UserType existingUserType = userTypeRepository.findByType(userType.getType());
            if (existingUserType != null) {
                savedUserTypes.add(existingUserType);
            } else {
                existingUserType = new UserType(userType.getType());
                userTypeRepository.save(existingUserType);
                savedUserTypes.add(existingUserType);
            }
        }

        registeredUser.setUserTypes(savedUserTypes);
        userRepository.save(registeredUser);
        logger.info("Пользователь успешно зарегистрирован: {}", registeredUser.getEmail());
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + email));
        return userMapper.toDTO(user);
    }

    public Long findIdByEmail(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Работодатель не найден по email: " + email));
        return employer.getId();
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, String[] userTypes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserType> roles = userTypeRepository.findByTypeIn(Arrays.asList(userTypes));
        user.setUserTypes(new HashSet<>(roles));
        userRepository.save(user);
    }

    public UserDTO findUserById(Long userId) {
        User user = userRepository.getById(userId);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void updateUser(String email, UserDTO updatedUser) throws SystemException {
        userRepository.findByEmail(email)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setLastname(updatedUser.getLastname());
                    user.setEmail(updatedUser.getEmail());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional
    public void deleteUserById(Long userId) {
        Optional<UserDTO> optionalUser = userRepository.findById(userId).stream().map(userMapper::toDTO).findFirst();
        if (optionalUser.isPresent()) {
            UserDTO user = optionalUser.get();
            notificationRepository.deleteByUserId(userId);
            userRepository.delete(userMapper.toEntity(user));
            logger.info("Пользователь {} успешно удален.", user.getEmail());
        } else
            logger.error("Пользователь с ID {} не найден, удаление невозможно.", userId);
    }
}