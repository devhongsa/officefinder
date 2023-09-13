package com.dokkebi.officefinder.controller.chat.dto;

import com.dokkebi.officefinder.entity.chat.ChatRoom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateRoomResponse {
    private String roomUid;
    private String roomName;

    public static CreateRoomResponse from(ChatRoom chatRoom) {
      return new CreateRoomResponse(chatRoom.getRoomUid(), chatRoom.getOfficeOwner().getName());
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ChatRoomStatus{
    private String roomUid;
    private String roomName;
    private String userName;
    private String profileImageUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean newMessage;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ChatMessageResponse{
    private boolean isMyMessage;
    private String sender;
    private String message;
    private LocalDateTime createdAt;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SendMessage{
    //채팅방 ID
    private String roomUid;
    //보내는 사람
    private String sender;
    //내용
    private String message;
  }


}
