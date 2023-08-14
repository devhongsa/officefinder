package com.dokkebi.officefinder.entity.chat;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
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
public class ChatRoom extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_room_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private OfficeOwner officeOwner;

  @Column(nullable = false)
  private String roomName;

  @Builder
  private ChatRoom(Long id, Customer customer, OfficeOwner officeOwner, String roomName) {
    this.id = id;
    this.customer = customer;
    this.officeOwner = officeOwner;
    this.roomName = roomName;
  }
}
