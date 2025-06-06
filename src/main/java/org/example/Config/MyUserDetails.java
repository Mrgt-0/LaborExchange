package org.example.Config;

import org.example.DTO.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {
    private UserDTO user;

    public MyUserDetails(UserDTO user){ this.user=user; }

    public Long getId() { return user.getId(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public UserDTO getUser() { return user; }
}
