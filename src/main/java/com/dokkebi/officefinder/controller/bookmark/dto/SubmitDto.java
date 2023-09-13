package com.dokkebi.officefinder.controller.bookmark.dto;

import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmitDto {

  @Positive
  private Long officeId;
}
