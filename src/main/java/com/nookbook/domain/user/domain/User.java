package com.nookbook.domain.user.domain;

import com.nookbook.domain.challenge.domain.Invitation;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionStatus;
import com.nookbook.domain.common.BaseEntity;
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

    private String imageUrl = "https://nookbook-image-bucket.s3.amazonaws.com/default.png";

    private String imageName = "default.png";

    // Participant와 Invitation 연관관계 추가
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Collection> collections = new ArrayList<>();

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
        this.role = Role.USER;
        this.collections = new ArrayList<>();
        initializeDefaultCollections();
    }

    // 읽고 싶은, 읽는 중, 읽음 기본 컬렉션 생성
    private void initializeDefaultCollections() {
        Collection reading = Collection.builder()
                .title("읽는 중")
                .orderIndex(1L)
                .user(this)
                .collectionStatus(CollectionStatus.MAIN)
                .build();
        Collection read = Collection.builder()
                .title("읽음")
                .orderIndex(2L)
                .user(this)
                .collectionStatus(CollectionStatus.MAIN)
                .build();
        Collection wish = Collection.builder()
                .title("읽고 싶은")
                .orderIndex(3L)
                .user(this)
                .collectionStatus(CollectionStatus.MAIN)
                .build();

        this.collections.add(reading);
        this.collections.add(read);
        this.collections.add(wish);
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
