package com.dokkebi.officefinder.entity.chat;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import java.time.LocalDateTime;
import java.util.UUID;
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

  private Long officeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private OfficeOwner officeOwner;

  @Column(nullable = false)
  private String roomUid;

  private LocalDateTime lastSeenCustomer;

  private LocalDateTime lastSeenOfficeOwner;

  @Builder
  private ChatRoom(Long id, Long officeId, Customer customer, OfficeOwner officeOwner, String roomUid,
      LocalDateTime lastSeenCustomer, LocalDateTime lastSeenOfficeOwner) {
    this.id = id;
    this.officeId = officeId;
    this.customer = customer;
    this.officeOwner = officeOwner;
    this.roomUid = roomUid;
    this.lastSeenCustomer = lastSeenCustomer;
    this.lastSeenOfficeOwner = lastSeenOfficeOwner;
  }

  public static ChatRoom create(Customer customer, OfficeOwner officeOwner, Long officeId) {
    return ChatRoom.builder()
        .officeId(officeId)
        .customer(customer)
        .officeOwner(officeOwner)
        .lastSeenCustomer(LocalDateTime.now())
        .lastSeenOfficeOwner(LocalDateTime.now())
        .roomUid(UUID.randomUUID().toString())
        .build();
  }

  public void readMessage(String userType) {
    if (userType.equals("customer")){
      this.lastSeenCustomer = LocalDateTime.now();
    } else{
      this.lastSeenOfficeOwner = LocalDateTime.now();
    }

  }

}
