package io.github.qifan777.server.infrastructure.model;

import lombok.Data;
import lombok.Getter;

@Data
public class UploadFile {
    String name;
    String url;
    UploadFileStatus status;
    Integer size;
    long uid;
    Integer percentage;

    @Getter
    public enum UploadFileStatus {
        ready,
        uploading,
        success,
        fail,
    }
}

