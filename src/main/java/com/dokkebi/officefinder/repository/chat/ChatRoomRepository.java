package com.dokkebi.officefinder.repository.chat;


import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.chat.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  List<ChatRoom> findByCustomer(Customer customer);
  List<ChatRoom> findByOfficeOwner(OfficeOwner officeOwner);

  Optional<ChatRoom> findByRoomUid(String roomUid);

  boolean existsByCustomerAndOfficeOwner(Customer customer, OfficeOwner officeOwner);

}
