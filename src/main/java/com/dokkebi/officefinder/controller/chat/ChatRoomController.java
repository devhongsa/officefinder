package com.dokkebi.officefinder.controller.chat;

import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatMessageResponse;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatRoomStatus;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.CreateRoomResponse;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.service.chat.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

  private final ChatService chatService;

  // 모든 채팅방 목록 반환
  @GetMapping("/rooms")
  @ResponseBody
  public List<ChatRoomStatus> room(@RequestHeader("Authorization") String jwt) {
    return chatService.findAllRoom(jwt);
  }

  // 채팅방 생성
  @PostMapping("/room/{officeId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public CreateRoomResponse createRoom(@PathVariable Long officeId,
      @RequestHeader("Authorization") String jwt) {
    return chatService.createRoom(officeId, jwt);
  }

  // 특정 채팅방 조회
  @GetMapping("/room/{roomUid}")
  @ResponseBody
  public List<ChatMessageResponse> roomInfo(@PathVariable String roomUid,
      @RequestHeader("Authorization") String jwt) {
    return chatService.roomInfo(roomUid, jwt);
  }

  // 채팅 메세지 읽음 표시
  @PostMapping("/room/read-message/{roomUid}")
  public ResponseDto<String> readMessage(@PathVariable String roomUid,
      @RequestHeader("Authorization") String jwt) {
    chatService.readMessage(roomUid, jwt);
    return new ResponseDto<>("success", "read message success");
  }

  // 새로운 메세지가 있는지 체크
  @GetMapping("/room/new-message")
  public ResponseDto<Boolean> isNewMessage(@RequestHeader("Authorization") String jwt) {
    return new ResponseDto<>("success", chatService.isNewMessage(jwt));
  }
}
