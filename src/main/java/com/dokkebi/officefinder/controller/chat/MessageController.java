package com.dokkebi.officefinder.controller.chat;

import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.SendMessage;
import com.dokkebi.officefinder.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final SimpMessageSendingOperations sendingOperations;
  private final ChatService chatService;

  @MessageMapping("/chat/message")
  public void send(SendMessage message) {
    sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomUid(),message);
    chatService.send(message);
  }
}

