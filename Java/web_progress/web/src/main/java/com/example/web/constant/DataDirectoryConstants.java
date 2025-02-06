package com.example.web.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("storage")
public class DataDirectoryConstants {

  //TODO: PLEASE ENTER YOUR FULL PATH:
  private String location = "C:\\Users\\User\\Desktop\\Java\\WebProject\\web_progress\\client\\src\\files";
//      "C:\\Users\\User\\Desktop\\Java\\WebProject\\web_progress\\web\\src\\main\\resources\\static\\directory_storage";

}

