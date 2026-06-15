package com.it355pz.freelance.service;

import com.it355pz.freelance.model.CvAttachment;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    CvAttachment storeCv(MultipartFile file);

    Resource loadAsResource(CvAttachment attachment);
}
