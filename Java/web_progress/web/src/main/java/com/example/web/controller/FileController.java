package com.example.web.controller;

import com.example.web.service.FileService;
import com.example.web.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileController {

  private final FileService fileService;
  private final ValidatorUtil validatorUtil;

  @PostMapping("/upload/{userId}")
  public ResponseEntity<?> handleAllFilesUpload(
      @PathVariable("userId") Long userId,
      @RequestPart("file") List<MultipartFile> files,
      @RequestParam(name = "offerId", defaultValue = "-1") Long offerId
  ) {

    return this.validatorUtil.responseEntityBoolean
        (this.fileService.handleAllFilesUpload(files, userId, offerId));
  }

}
