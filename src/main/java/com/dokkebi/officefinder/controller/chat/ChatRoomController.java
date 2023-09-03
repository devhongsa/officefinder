package com.dokkebi.officefinder.controller.chat;

import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatMessageResponse;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatRoomStatus;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.CreateRoomResponse;
import com.dokkebi.officefinder.service.chat.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {
  private final ChatService chatService;

//  // 채팅 리스트 화면 (테스트용 api)
//  @GetMapping("/room")
//  public ModelAndView rooms() {
//    ModelAndView model = new ModelAndView("room");
//    return model;
//  }

  // 모든 채팅방 목록 반환
  @GetMapping("/rooms")
  @ResponseBody
  public List<ChatRoomStatus> room(@CookieValue("Authorization") String jwt) {
    return chatService.findAllRoom(jwt);
  }

  // 채팅방 생성
  @PostMapping("/room/{officeId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public CreateRoomResponse createRoom(@PathVariable Long officeId, @CookieValue("Authorization") String jwt) {
    return chatService.createRoom(officeId,jwt);
  }

//  // 채팅방 입장 화면 (테스트용 api)
//  @GetMapping("/room/enter/{roomUid}")
//  public ModelAndView roomDetail(@PathVariable String roomUid) {
//    ModelAndView model = new ModelAndView("roomdetail");
//    model.addObject("roomUid", roomUid);
//    return model;
//  }

  // 특정 채팅방 조회
  @GetMapping("/room/{roomUid}")
  @ResponseBody
  public List<ChatMessageResponse> roomInfo(@PathVariable String roomUid, @CookieValue("Authorization") String jwt) {
    return chatService.roomInfo(roomUid, jwt);
  }
}
