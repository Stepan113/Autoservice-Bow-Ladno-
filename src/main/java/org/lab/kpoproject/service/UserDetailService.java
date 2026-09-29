package org.lab.kpoproject.service;

import org.lab.kpoproject.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository rep;

    public UserDetailService(final UserRepository rep) {
        this.rep = rep;
    }

    @Override
    public UserDetails loadUserByUsername(final String email)
            throws UsernameNotFoundException {
        return rep.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(email));
    }
}
