package com.nookbook.domain.user.domain;

import com.nookbook.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Friend")
@NoArgsConstructor
@Getter
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="friend_id", updatable = false, nullable = false, unique = true)
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus friendRequestStatus = FriendRequestStatus.FRIEND_REQUEST;

    @Builder
    public Friend(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public void updateFriendRequestStatus(FriendRequestStatus friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    // 사용자 본인이 아닌 친구의 user id를 가져오는 유틸 메서드
    public Long getFriendUserId(User currentUser) {
        return sender.equals(currentUser) ? receiver.getUserId() : sender.getUserId();
    }
}
