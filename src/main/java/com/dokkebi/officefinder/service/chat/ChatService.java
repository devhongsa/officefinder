package com.dokkebi.officefinder.service.chat;

import static com.dokkebi.officefinder.exception.CustomErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.dokkebi.officefinder.exception.CustomErrorCode.INVALID_OFFICE_ID;
import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;

import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatMessageResponse;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.ChatRoomStatus;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.CreateRoomResponse;
import com.dokkebi.officefinder.controller.chat.dto.ChatRoomDto.SendMessage;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.chat.ChatMessage;
import com.dokkebi.officefinder.entity.chat.ChatRoom;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.chat.ChatMessageRepository;
import com.dokkebi.officefinder.repository.chat.ChatRoomRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final CustomerRepository customerRepository;
  private final OfficeOwnerRepository officeOwnerRepository;
  private final OfficeRepository officeRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final TokenProvider tokenProvider;

  private final String CUSTOMER = "customer";

  // 특정 회원의 채팅방 목록 불러오기
  @Transactional(readOnly = true)
  public List<ChatRoomStatus> findAllRoom(String jwt) {
    Long userId = tokenProvider.getUserIdFromHeader(jwt);

    List<ChatRoomStatus> result = new ArrayList<>();
    if (tokenProvider.getUserType(tokenProvider.resolveTokenFromHeader(jwt)).equals(CUSTOMER)) {
      Customer customer = customerRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
      List<ChatRoom> chatRooms = chatRoomRepository.findByCustomer(
          customer);
      HashMap<ChatRoom, ChatMessage> chatMap = getChatMessageHashMap(
          chatRooms);

      for (ChatRoom chatRoom : chatRooms) {
        if (chatMap.containsKey(chatRoom)) {
          result.add(ChatRoomStatus.builder()
              .roomUid(chatRoom.getRoomUid())
              .roomName(chatRoom.getOfficeOwner().getName())
              .userName(chatRoom.getCustomer().getName())
              .profileImageUrl(chatRoom.getOfficeOwner().getOfficeOwnerProfileImage())
              .lastMessage(chatMap.get(chatRoom).getMessage())
              .lastMessageTime(chatMap.get(chatRoom).getCreatedAt())
              .newMessage(
                  chatMap.get(chatRoom).getCreatedAt().isAfter(chatRoom.getLastSeenCustomer()))
              .build());
        } else {
          result.add(ChatRoomStatus.builder()
              .roomUid(chatRoom.getRoomUid())
              .roomName(chatRoom.getOfficeOwner().getName())
              .userName(chatRoom.getCustomer().getName())
              .profileImageUrl(chatRoom.getOfficeOwner().getOfficeOwnerProfileImage())
              .lastMessage("")
              .lastMessageTime(chatRoom.getLastSeenCustomer())
              .newMessage(false)
              .build());
        }
      }
    } else {
      OfficeOwner officeOwner = officeOwnerRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
      List<ChatRoom> chatRooms = chatRoomRepository.findByOfficeOwner(officeOwner);
      HashMap<ChatRoom, ChatMessage> chatMap = getChatMessageHashMap(
          chatRooms);

      for (ChatRoom chatRoom : chatRooms) {
        if (chatMap.containsKey(chatRoom)) {
          result.add(ChatRoomStatus.builder()
              .roomUid(chatRoom.getRoomUid())
              .roomName(chatRoom.getCustomer().getName())
              .userName(chatRoom.getOfficeOwner().getName())
              .profileImageUrl(chatRoom.getCustomer().getProfileImage())
              .lastMessage(chatMap.get(chatRoom).getMessage())
              .lastMessageTime(chatMap.get(chatRoom).getCreatedAt())
              .newMessage(
                  chatMap.get(chatRoom).getCreatedAt().isAfter(chatRoom.getLastSeenOfficeOwner()))
              .build());
        } else {
          result.add(ChatRoomStatus.builder()
              .roomUid(chatRoom.getRoomUid())
              .roomName(chatRoom.getCustomer().getName())
              .userName(chatRoom.getOfficeOwner().getName())
              .profileImageUrl(chatRoom.getCustomer().getProfileImage())
              .lastMessage("")
              .lastMessageTime(chatRoom.getLastSeenCustomer())
              .newMessage(false)
              .build());
        }
      }
    }
    return result;
  }


  // 특정 회원에게 새로운 채팅 메세지가 왔는지
  @Transactional(readOnly = true)
  public boolean isNewMessage(String jwt) {
    Long userId = tokenProvider.getUserIdFromHeader(jwt);

    if (tokenProvider.getUserType(tokenProvider.resolveTokenFromHeader(jwt)).equals(CUSTOMER)) {
      Customer customer = customerRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
      List<ChatRoom> chatRooms = chatRoomRepository.findByCustomer(
          customer);
      HashMap<ChatRoom, ChatMessage> chatMap = getChatMessageHashMap(
          chatRooms);

      for (ChatRoom chatRoom : chatMap.keySet()) {
        if (chatMap.get(chatRoom).getCreatedAt().isAfter(chatRoom.getLastSeenCustomer())) {
          return true;
        }

      }
    } else {
      OfficeOwner officeOwner = officeOwnerRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
      List<ChatRoom> chatRooms = chatRoomRepository.findByOfficeOwner(officeOwner);
      HashMap<ChatRoom, ChatMessage> chatMap = getChatMessageHashMap(
          chatRooms);

      for (ChatRoom chatRoom : chatMap.keySet()) {
        if (chatMap.get(chatRoom).getCreatedAt().isAfter(chatRoom.getLastSeenCustomer())) {
          return true;
        }
      }
    }
    return false;
  }


  //특정 채팅방 내용 불러오기
  @Transactional(readOnly = true)
  public List<ChatMessageResponse> roomInfo(String roomUid, String jwt) {
    String userType = tokenProvider.getUserType(tokenProvider.resolveTokenFromHeader(jwt));

    ChatRoom chatRoom = chatRoomRepository.findByRoomUid(roomUid)
        .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
    List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoom(chatRoom);

    String customerName = chatRoom.getCustomer().getName();
    String officeOwnerName = chatRoom.getOfficeOwner().getName();

    List<ChatMessageResponse> result = new ArrayList<>();

    if (userType.equals(CUSTOMER)) {
      for (ChatMessage chatMessage : chatMessages) {
        String sender;
        boolean isMyMsg = false;
        if (chatMessage.getCustomerId() != null) {
          sender = customerName;
          isMyMsg = true;
        } else {
          sender = officeOwnerName;
        }
        result.add(ChatMessageResponse.builder()
            .message(chatMessage.getMessage())
            .isMyMessage(isMyMsg)
            .sender(sender)
            .createdAt(chatMessage.getCreatedAt())
            .build());
      }
    } else {
      for (ChatMessage chatMessage : chatMessages) {
        String sender;
        boolean isMyMsg = false;
        if (chatMessage.getOfficeOwnerId() != null) {
          sender = officeOwnerName;
          isMyMsg = true;
        } else {
          sender = customerName;
        }
        result.add(ChatMessageResponse.builder()
            .message(chatMessage.getMessage())
            .isMyMessage(isMyMsg)
            .sender(sender)
            .createdAt(chatMessage.getCreatedAt())
            .build());
      }

    }
    return result;
  }

  //채팅방 생성
  @Transactional
  public CreateRoomResponse createRoom(Long officeId, String jwt) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwt);
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Office office = officeRepository.findById(officeId)
        .orElseThrow(() -> new CustomException(INVALID_OFFICE_ID));
    OfficeOwner officeOwner = office.getOwner();

    if (chatRoomRepository.existsByCustomerAndOfficeId(customer, officeId)) {
      throw new CustomException(CHAT_ROOM_ALREADY_EXISTS);
    }

    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(customer, officeOwner, officeId));
    return CreateRoomResponse.from(chatRoom);
  }

  @Transactional
  public void send(SendMessage message) {
    ChatRoom chatRoom = chatRoomRepository.findByRoomUid(message.getRoomUid())
        .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

    if (message.getSender().equals(chatRoom.getCustomer().getName())) {
      chatMessageRepository.save(
          ChatMessage.builder()
              .chatRoom(chatRoom)
              .customerId(chatRoom.getCustomer().getId())
              .message(message.getMessage())
              .build()
      );
    } else {
      chatMessageRepository.save(
          ChatMessage.builder()
              .chatRoom(chatRoom)
              .officeOwnerId(chatRoom.getOfficeOwner().getId())
              .message(message.getMessage())
              .build()
      );
    }

  }

  // 메세지 읽음
  @Transactional
  public void readMessage(String roomUid, String jwt) {
    Long userId = tokenProvider.getUserIdFromHeader(jwt);
    String userType = tokenProvider.getUserType(tokenProvider.resolveTokenFromHeader(jwt));

    ChatRoom chatRoom = chatRoomRepository.findByRoomUid(roomUid)
        .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

    chatRoom.readMessage(userType.equals(CUSTOMER) ? CUSTOMER : "agent");
  }


  private HashMap<ChatRoom, ChatMessage> getChatMessageHashMap(List<ChatRoom> chatRooms) {
    List<ChatMessage> chatMessages = chatMessageRepository.findTopByChatRoomInOrderByCreatedAtDesc(
        chatRooms);

    HashMap<ChatRoom, ChatMessage> chatMap = new HashMap<>();

    for (ChatMessage chatMessage : chatMessages) {
      chatMap.put(chatMessage.getChatRoom(), chatMessage);
    }
    return chatMap;
  }

}

