package com.dokkebi.officefinder.repository.office.picture;

import static org.junit.jupiter.api.Assertions.*;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.Office.OfficeBuilder;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OfficePictureRepositoryTest {

  @Autowired
  private OfficePictureRepository officePictureRepository;

  @Autowired
  private OfficeRepository officeRepository;

  @DisplayName("오피스의 id로 오피스가 가지고 있는 사진들을 찾을 수 있다.")
  @Test
  public void findByOfficeId() throws Exception {
    // given
    Office office = Office.builder()
        .name("office1")
        .leaseFee(10000L)
        .maxCapacity(10)
        .remainRoom(10)
        .build();

    Office savedOffice = officeRepository.save(office);

    OfficePicture picture = OfficePicture.builder()
        .fileName("abcd.png")
        .office(office)
        .build();

    OfficePicture picture2 = OfficePicture.builder()
        .fileName("abce.png")
        .office(office)
        .build();

    OfficePicture picture3 = OfficePicture.builder()
        .fileName("abcf.png")
        .office(office)
        .build();

    officePictureRepository.saveAll(List.of(picture, picture2, picture3));
    // when
    List<OfficePicture> result = officePictureRepository.findByOfficeId(savedOffice.getId());

    // then
  }
}