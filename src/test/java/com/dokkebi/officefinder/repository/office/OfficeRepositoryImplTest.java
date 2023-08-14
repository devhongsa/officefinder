package com.dokkebi.officefinder.repository.office;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OfficeRepositoryImplTest {

  @Autowired
  private OfficeRepository officeRepository;
}