package com.example.web.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler(TourOfferNotFoundException.class)
  public ResponseEntity<Object> handleTourOfferNotFoundException(
      TourOfferNotFoundException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Offer not found");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NoSuchRoleException.class)
  public ResponseEntity<Object> handleTourOfferNotFoundException(
      NoSuchRoleException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "No such Role Type, please check the existing Roles");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }


  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Object> handleUserNotFoundException
      (UserNotFoundException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "User not found");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MapEntityPageIntoDtoPageException.class)
  public ResponseEntity<Object> handleMapEntityPageIntoDTOPageException
      (MapEntityPageIntoDtoPageException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Error while trying to Map Page<Offer> into Page<DTO>");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(PageWithOffersNotFoundException.class)
  public ResponseEntity<Object> handlePageWithOffersNotFoundException
      (PageWithOffersNotFoundException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Page not found");
//    body.put("additional", ex); stack trace

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<Object> handleStorageException(StorageException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Issue while trying to store file");
//    body.put("additional", ex); stack trace

    return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(TourOfferFilePathNotFoundException.class)
  public ResponseEntity<Object> handleTourOfferFilePathNotFoundException() {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Issue while trying to find Offer files");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NoDataFoundException.class)
  public ResponseEntity<Object> handleNodataFoundException(NoDataFoundException ex) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "No cities found");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

}
