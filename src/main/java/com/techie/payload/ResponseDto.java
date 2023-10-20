package com.techie.payload;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    private String bucketName;
    private List<String> buckets;
    private List<FileUpload> files;
    private String status;
}
