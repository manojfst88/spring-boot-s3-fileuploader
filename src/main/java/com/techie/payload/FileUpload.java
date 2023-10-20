package com.techie.payload;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    private String fileName;
    private Long fileSize;
    private String filePath;
    private byte[] file;

    public FileUpload(String fileName, Long fileSize, String filePath) {
        super();
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }
}
