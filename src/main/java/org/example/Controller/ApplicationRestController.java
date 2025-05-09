package org.example.Controller;

import org.example.DTO.ApplicationDTO;
import org.example.DTO.UserDTO;
import org.example.Mapper.UserMapper;
import org.example.Model.Application;
import org.example.Model.User;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationRestController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/my")
    public List<Application> getMyApplications(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return applicationService.getApplicationsByUser(userId);
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null || principal.getName() == null)
            throw new IllegalStateException("Пользователь не аутентифицирован");

        String email = principal.getName();
        UserDTO user = userService.findByEmail(email);
        if (user == null)
            throw new IllegalStateException("Пользователь по email " + email + " не найден");

        return user.getId();
    }
}