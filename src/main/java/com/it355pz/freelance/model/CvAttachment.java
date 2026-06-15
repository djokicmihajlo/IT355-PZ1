package com.it355pz.freelance.model;

public class CvAttachment {

    private Long id;
    private String originalFileName;
    private String contentType;
    private long size;
    private String storagePath;

    public CvAttachment() {
    }

    public CvAttachment(Long id, String originalFileName, String contentType, long size, String storagePath) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.storagePath = storagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
