package com.dokkebi.officefinder.repository.chat;

import com.dokkebi.officefinder.entity.chat.ChatMessage;
import com.dokkebi.officefinder.entity.chat.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  @Query(value = "SELECT * FROM chat_message as cm WHERE (cm.chat_room_id, cm.created_at) IN" +
      "(SELECT c.chat_room_id, MAX(c.created_at) AS createdAt " +
      "FROM chat_message c " +
      "WHERE c.chat_room_id IN (:chatRoomIds)" +
      "GROUP BY c.chat_room_id) " +
      "ORDER BY cm.created_at DESC", nativeQuery = true)
  List<ChatMessage> findTopByChatRoomInOrderByCreatedAtDesc(@Param("chatRoomIds")List<Long> chatRoomIds);

  List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

}
