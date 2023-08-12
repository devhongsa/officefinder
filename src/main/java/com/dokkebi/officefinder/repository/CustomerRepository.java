package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
