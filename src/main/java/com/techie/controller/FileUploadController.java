package com.techie.controller;

import com.techie.payload.FileUpload;
import com.techie.payload.ResponseDto;
import com.techie.service.FileUploadService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/s3")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/bucket/list")
    public ResponseEntity<?> getListOfBuckets() {
        ResponseDto response = new ResponseDto();
        response.setBuckets(fileUploadService.getListOfBuckets());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bucket/files/{bucketName}")
    public ResponseEntity<?> getFileByBucketName(@PathVariable String bucketName) {
        ResponseDto response = new ResponseDto();
        response.setFiles(fileUploadService.getBucketfiles(bucketName));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bucket/create/{bucketName}")
    public ResponseEntity<?> createBucket(@PathVariable String bucketName){
        ResponseDto response = new ResponseDto();
        response.setBucketName(bucketName);
        response.setStatus(fileUploadService.createBucket(bucketName));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/bucket/delete/{bucketName}")
    public ResponseEntity<?> deleteBucket(@PathVariable String bucketName){
        ResponseDto response = new ResponseDto();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/file/upload/{bucketName}")
    public ResponseEntity<?> fileUplaod(@PathVariable String bucketName, MultipartFile file) {
        ResponseDto response = new ResponseDto();
        response.setStatus(fileUploadService.fileUplaod(bucketName, file));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/file/delete/{bucketName}/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
        ResponseDto response = new ResponseDto();
        response.setStatus(fileUploadService.deleteFile(bucketName, fileName));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/file/download/{bucketName}/{fileName}")
    public StreamingResponseBody downloadFile(@PathVariable String bucketName, @PathVariable String fileName,
                                              HttpServletResponse httpResponse) {
        ResponseDto response = new ResponseDto();
        FileUpload downloadFile = fileUploadService.downloadFile(bucketName, fileName);
        httpResponse.setContentType("application/octet-stream");
        httpResponse.setHeader("Content-Disposition",
                String.format("inline; filename=\"%s\"", downloadFile.getFileName()));
        return new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                outputStream.write(downloadFile.getFile());
                outputStream.flush();
            }
        };
    }


}
