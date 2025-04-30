package org.example.Service;

import org.example.Config.MyUserDetails;
import org.example.DTO.UserDTO;
import org.example.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDTO userDTO = userService.findByEmail(email);
        logger.info("Поиск пользователя с email: {}", userDTO.getEmail());
        if (userDTO == null) {
            logger.error("Пользователь не найден: {}", email);
            throw new UsernameNotFoundException("Пользователь не найден: " + email);
        }
        logger.info("Пользователь найден: {}", userDTO.getEmail());
        return new MyUserDetails(userDTO);
    }
}