package org.example.mongodbauthserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mongodbauthserver.model.MongoDBAuthority;
import org.example.mongodbauthserver.model.MongoDBRole;
import org.example.mongodbauthserver.model.MongoDBUser;
import org.example.mongodbauthserver.repository.MongoDBUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoDBUserDetailsService implements UserDetailsService {

    private final MongoDBUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MongoDBUser> userOptional = userRepository.findByUsername(username);

        if(!userOptional.isPresent()) {
            log.debug("[LOGIN] - Cannot find any user record with username: {}", username);
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        MongoDBUser user = userOptional.get();
        if(!user.getEnabled()) {
            log.debug("[LOGIN] - User with username: {} is not enabled.", username);
            throw new UsernameNotFoundException("User with username: " + username + " is not enabled.");
        }

        Set<String> dbAuthsSet = new HashSet<>();
        for(MongoDBRole role : user.getRoleList()) {
            String roleName = "ROLE_" + role.getRole();
            dbAuthsSet.add(roleName);

            for(MongoDBAuthority authority : role.getAuthorityList()) {
                dbAuthsSet.add(authority.getAuthority());
            }
        }

        List<GrantedAuthority> authorityList =
                dbAuthsSet.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableList());
        if(authorityList.isEmpty()) {
            log.debug("[LOGIN] - User with username: {} has no role/authority.", username);
            throw new UsernameNotFoundException("User with username: " + username + " has no role/authority.");
        }

        //todo should include user expiry in db
        return new User(
                user.getUsername(),
                user.getPassword(),
                authorityList);
    }
}
