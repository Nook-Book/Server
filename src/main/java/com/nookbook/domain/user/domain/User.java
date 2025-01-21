package com.nookbook.domain.user.domain;

import com.nookbook.domain.challenge.domain.Invitation;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.friend.domain.Friend;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    // Participant와 Invitation 연관관계 추가
    @OneToMany(mappedBy = "user")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

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

    public void updateImage(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

}
