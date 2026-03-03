package com.bilbo.store.service;

import com.bilbo.store.client.Auth0Client;
import com.bilbo.store.client.dto.Auth0User;
import com.bilbo.store.entites.User;
import com.bilbo.store.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Auth0Client auth0Client;

    @Transactional
    public User processUser(String sub) {
        log.info("Processing user with sub: {}", sub);

        Auth0User auth0User = auth0Client.getUser(sub).block();

        if (auth0User == null) {
            log.error("Could not retrieve user details from Auth0 for sub: {}", sub);
            throw new IllegalStateException("Unable to fetch user profile from Auth0.");
        }

        User user = userRepository.findBySub(sub)
            .map(existingUser -> updateUser(existingUser, auth0User))
            .orElseGet(() -> createUser(auth0User));

        return userRepository.save(user);
    }

    private User updateUser(User existingUser, Auth0User auth0User) {
        if (isUserUnchanged(existingUser, auth0User)) {
            log.info("User {} is unchanged. No update needed.", existingUser.getSub());
            return existingUser;
        }

        log.info("Updating existing user with sub: {}", existingUser.getSub());
        existingUser.setUsername(auth0User.getUserId());
        existingUser.setEmail(auth0User.getEmail());
        existingUser.setFirstName(auth0User.getName());
        existingUser.setLastName(auth0User.getNickname());
        existingUser.setLastUpdatedTimestamp(OffsetDateTime.now());
        existingUser.setLastUpdatedBy(existingUser.getSub());
        return existingUser;
    }

    private User createUser(Auth0User auth0User) {
        log.info("Creating new user with sub: {}", auth0User.getUserId());
        User newUser = new User();
        newUser.setSub(auth0User.getUserId());
        newUser.setUsername(auth0User.getUserId());
        newUser.setEmail(auth0User.getEmail());
        newUser.setFirstName(auth0User.getName());
        newUser.setLastName(auth0User.getNickname());
        newUser.setCreatedTimestamp(OffsetDateTime.now());
        newUser.setLastUpdatedTimestamp(OffsetDateTime.now());
        newUser.setCreatedBy(newUser.getSub());
        newUser.setLastUpdatedBy(newUser.getSub());
        return newUser;
    }

    private boolean isUserUnchanged(User user, Auth0User auth0User) {
        return Objects.equals(user.getUsername(), auth0User.getUserId()) &&
            Objects.equals(user.getEmail(), auth0User.getEmail()) &&
            Objects.equals(user.getFirstName(), auth0User.getName()) &&
            Objects.equals(user.getLastName(), auth0User.getNickname());
    }
}
