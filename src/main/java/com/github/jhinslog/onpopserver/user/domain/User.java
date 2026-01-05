package com.github.jhinslog.onpopserver.user.domain;

import com.github.jhinslog.onpopserver.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "USERS")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "social_id", nullable = false, length = 100)
    private String socialId;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_mode", nullable = false, length = 1)
    private UserMode userMode;

    @Column(name = "role", length = 20)
    private String role;

    public void changeUserMode(UserMode userMode) {
        if (this.userMode == userMode) return;
        this.userMode = userMode;
    }
}