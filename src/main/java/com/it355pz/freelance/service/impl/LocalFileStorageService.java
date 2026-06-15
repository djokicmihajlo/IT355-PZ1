package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.service.FileStorageService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final long MAX_CV_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx");

    private final Path cvUploadDirectory;

    public LocalFileStorageService(@Value("${app.upload.cv-dir:uploads/cv}") String cvUploadDirectory) {
        this.cvUploadDirectory = Paths.get(cvUploadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public CvAttachment storeCv(MultipartFile file) {
        validateCv(file);

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "-" + originalFileName;
        Path destination = cvUploadDirectory.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(cvUploadDirectory);
            file.transferTo(destination);
        } catch (IOException ex) {
            throw new ValidationException("CV fajl nije mogao da bude sacuvan.");
        }

        return new CvAttachment(null, originalFileName, file.getContentType(), file.getSize(), destination.toString());
    }

    @Override
    public Resource loadAsResource(CvAttachment attachment) {
        if (attachment == null || attachment.getStoragePath() == null) {
            throw new ResourceNotFoundException("CV fajl nije pronadjen.");
        }

        try {
            Path path = Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("CV fajl nije pronadjen.");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("CV fajl nije pronadjen.");
        }
    }

    private void validateCv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("CV fajl je obavezan.");
        }

        if (file.getSize() > MAX_CV_SIZE) {
            throw new ValidationException("CV fajl ne sme biti veci od 5MB.");
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (!hasAllowedExtension(originalFileName) && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ValidationException("CV mora biti PDF ili DOCX fajl.");
        }
    }

    private String sanitizeFileName(String originalFileName) {
        String fileName = originalFileName == null ? "" : Paths.get(originalFileName).getFileName().toString();
        String safeName = fileName.replaceAll("[^A-Za-z0-9._-]", "_");
        if (safeName.isBlank()) {
            throw new ValidationException("Naziv CV fajla nije validan.");
        }
        return safeName;
    }

    private boolean hasAllowedExtension(String fileName) {
        String lowerCaseName = fileName.toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.stream().anyMatch(lowerCaseName::endsWith);
    }
}
