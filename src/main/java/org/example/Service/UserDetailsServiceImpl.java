package org.example.Service;
import org.example.Config.MyUserDetails;
import org.example.DTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDTO userDTO = userService.findByEmail(email);
        if (userDTO == null) {
            logger.error("Пользователь не найден для email: {}", email);
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        logger.info("Пользователь найден: {}", userDTO.getEmail());
        return new MyUserDetails(userDTO);
    }
}