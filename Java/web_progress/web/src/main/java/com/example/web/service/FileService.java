package com.example.web.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.multipart.MultipartFile;


public interface FileService {

  boolean handleAllFilesUpload
      (
          List<MultipartFile> files,
          Long userId,
          Long offerId
      );

  Stream<Path> loadAll();
}
