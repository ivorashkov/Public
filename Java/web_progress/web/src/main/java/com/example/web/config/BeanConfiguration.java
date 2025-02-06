package com.example.web.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

  @Bean
  public Gson gson() {
    return new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();
  }

  @Bean
  public ModelMapper modelMapper() {
    var modelMapper = new ModelMapper();

    modelMapper.addConverter(
        (Converter<String, LocalDate>) mappingContext -> LocalDate.parse(mappingContext.getSource(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    modelMapper.addConverter(
        (Converter<String, LocalDateTime>) mappingContext -> LocalDateTime.parse(
            mappingContext.getSource(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    return modelMapper;
  }

}
