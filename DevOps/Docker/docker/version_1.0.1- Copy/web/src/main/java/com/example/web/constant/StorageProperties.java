package com.example.web.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("storage")
public class StorageProperties {

  //TODO: PLEASE ENTER YOUR FULL PATH:
  private String location =
      "C:\\Users\\User\\Desktop\\Java\\WebProject\\version_1.0.1-crashed\\web\\src\\main\\resources\\static\\directory_storage";
}
