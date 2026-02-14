package com.neartalk.api.domain.user.repository;

import com.neartalk.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(User.Provider provider, String providerId);

    boolean existsByNickname(String nickname);
}
