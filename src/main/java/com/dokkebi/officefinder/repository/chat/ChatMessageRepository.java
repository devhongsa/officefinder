package com.dokkebi.officefinder.repository.chat;

import com.dokkebi.officefinder.entity.chat.ChatMessage;
import com.dokkebi.officefinder.entity.chat.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findTopByChatRoomInOrderByCreatedAtDesc(List<ChatRoom> chatRooms);

  List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

}
