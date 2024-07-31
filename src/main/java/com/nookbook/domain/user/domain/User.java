package com.nookbook.domain.user.domain;

import com.nookbook.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="User")
@NoArgsConstructor
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", updatable = false, nullable = false, unique = true)
    private Long userId;

    @Column(name="nickname_id")
    private String nicknameId;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    // 이미지는 기본 고정 필요
    private String imageUrl = "https://";

    private String imageName = "default.png";


    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public User(String nicknameId, String email, String password, String nickname, Provider provider, String providerId, Role role) {
        this.nicknameId = nicknameId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public void saveUserInfo(String nicknameId, String nickname) {
        this.nicknameId = nicknameId;
        this.nickname = nickname;
    }

    public void updateNicknameId(String nicknameId) { this.nicknameId = nicknameId; }
    public void updateNickname(String nickname) { this.nickname = nickname; }

}
