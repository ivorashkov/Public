package com.example.web.service;


import com.example.web.model.dto.OfficeDTO;
import java.util.List;

public interface OfficeService {

  List<OfficeDTO> addOffice(OfficeDTO officeDTO);

  boolean deleteOffice(OfficeDTO officeDTO);

  List<OfficeDTO> editOffice(OfficeDTO officeDTO);
}
