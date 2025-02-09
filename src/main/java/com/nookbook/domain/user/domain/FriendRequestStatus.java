package com.nookbook.domain.user.domain;

import lombok.Getter;

@Getter
public enum FriendRequestStatus {

    FRIEND_REQUEST,  // sender가 receiver에게 친구 요청
    FRIEND_ACCEPT;    // 친구
}
