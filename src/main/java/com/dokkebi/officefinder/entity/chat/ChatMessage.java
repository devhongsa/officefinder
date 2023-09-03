package com.dokkebi.officefinder.entity.chat;

import com.dokkebi.officefinder.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;

  private Long customerId;

  private Long officeOwnerId;

  @Column(nullable = false)
  private String message;

  @Builder
  private ChatMessage(Long id, ChatRoom chatRoom, Long customerId, Long officeOwnerId,
      String message) {
    this.id = id;
    this.chatRoom = chatRoom;
    this.customerId = customerId;
    this.officeOwnerId = officeOwnerId;
    this.message = message;
  }
}
