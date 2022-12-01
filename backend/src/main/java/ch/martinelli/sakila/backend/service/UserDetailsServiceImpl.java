package ch.martinelli.sakila.backend.service;

import ch.martinelli.sakila.backend.entity.SakilaUser;
import ch.martinelli.sakila.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SakilaUser applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new User(applicationUser.getUsername(), applicationUser.getHashedPassword(), getAuthorities(applicationUser));
        }
    }

    private List<GrantedAuthority> getAuthorities(SakilaUser applicationUser) {
        return userRepository.getRoles(applicationUser.getId())
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoles()))
                .collect(Collectors.toList());
    }

}
