package com.dokkebi.officefinder.repository.office.picture;

import com.dokkebi.officefinder.entity.office.OfficePicture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficePictureRepository extends JpaRepository<OfficePicture, Long> {

  @Query("select op from OfficePicture op where op.office.id=:officeId")
  List<OfficePicture> findByOfficeId(@Param("officeId") Long officeId);
}
