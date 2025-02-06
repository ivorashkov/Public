package com.example.web.service.impl;

import com.example.web.constant.FileServiceConstants;
import com.example.web.exception.StorageException;
import com.example.web.constant.DataDirectoryConstants;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.service.AccountInfoService;
import com.example.web.service.FileService;
import com.example.web.service.TourOfferFilePathService;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

  private final TourOfferService tourOfferService;
  private final UserService userService;
  private final AccountInfoService additionalInfoService;
  private final TourOfferFilePathService offerDataService;
  private final Path rootLocation;

  public FileServiceImpl
      (
          TourOfferService tourOfferService,
          UserService userService, TourOfferFilePathService offerDataService,
          DataDirectoryConstants properties,
          AccountInfoService additionalInfoService
      ) {
    this.tourOfferService = tourOfferService;
    this.userService = userService;
    this.offerDataService = offerDataService;
    this.rootLocation = Paths.get(properties.getLocation());
    this.additionalInfoService = additionalInfoService;
  }

  @Override
  public boolean handleAllFilesUpload(List<MultipartFile> files, Long userId, Long offerId) {
    log.info(" [INFO] FileServiceImpl { handleAllFilesUpload } {}, {}", userId, offerId);

    files.stream()
        .filter(f -> !f.isEmpty())
        .forEach(file -> {
          Path path = handleSingleFileUpload(file, userId, offerId);

          if (offerId > 0) {
            TourOfferFullDTO offerDTO = this.tourOfferService.findByIdAndUserId(offerId, userId);
            this.offerDataService.saveFileUri(offerDTO, path);

          } else {
            UserDTO userDTO = this.userService.findUserDTOById(userId);
            this.additionalInfoService.saveFileUri(userDTO, path);
          }
        });
    return true;
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
          .filter(path -> !path.equals(this.rootLocation))
          .map(this.rootLocation::relativize);
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }
  }

  public Path handleSingleFileUpload(MultipartFile file, Long userId, Long offerId) {

    /** http://localhost:8091/home/upload?userId=1&offerId=-1 */
    Path initPath = pathInitialization(userId, offerId,
        FileServiceConstants.FORMAT_ADDON_TEMPLATE);

    return storeFile(file, initPath);
  }

  private Path pathInitialization(Long userId, Long offerId, String stringFormat) {
    StringBuilder stringDirectory = new StringBuilder();

    stringDirectory
        .append(rootLocation)
        .append(FileServiceConstants.DIRECTORY_SEPARATOR)
        .append(String.format(stringFormat, FileServiceConstants.USER, userId));

    if (!Files.exists(Paths.get(stringDirectory.toString()))) {
      /** if the user has no directory already -> \\ creating folders using template %s_%d **/
      /** creating folder if such does not exist **/
      new File(stringDirectory.toString()).mkdirs();
    }

    if (offerId >= 0) {
      /** then we should create Offer directory with offerId */
      stringDirectory
          .append(FileServiceConstants.DIRECTORY_SEPARATOR)
          .append(String.format(stringFormat, FileServiceConstants.OFFER, offerId));

      new File(stringDirectory.toString()).mkdirs();
    }

    return Paths.get(stringDirectory.toString());
  }

  private Path storeFile(MultipartFile file, Path pathFromInitialization) {
    Path destinationFile;
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file.");
      }

      destinationFile = pathFromInitialization
          .resolve(Paths.get(file.getOriginalFilename()))
          .normalize().toAbsolutePath();

      if (!destinationFile.getParent().equals(pathFromInitialization.toAbsolutePath())) {
        throw new StorageException("Cannot store file outside current directory.");
      }

      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile,
            StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        log.error(" [ERROR] Failed to store file in FileServiceImpl {}", e.getMessage());
      }

      return destinationFile;
    } catch (StorageException s) {
      log.error(" [ERROR] Failed to store file in FileServiceImpl {}", s.getMessage());

      throw s;
    }
  }


}
