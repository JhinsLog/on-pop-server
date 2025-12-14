package com.github.jhinslog.onpopserver.user.repository;

import com.github.jhinslog.onpopserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    //소셜 로그인시, 기존 회원 찾기
    Optional<User> findBySocialIdAndProvider(String socialId, String provider);

    boolean existsByNickname(String nickname);

}
