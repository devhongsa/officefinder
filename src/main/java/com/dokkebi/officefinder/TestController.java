package com.dokkebi.officefinder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/test")
  public String test(){
    return "success";
  }

  @GetMapping("/test/test2")
  public String test2(){
    return "success test2";
  }
}
