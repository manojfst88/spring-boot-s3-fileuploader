package com.techie.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.techie.payload.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileUploadService {

    @Autowired
    private AmazonS3 amazonS3;

    public List<String> getListOfBuckets() {
        return amazonS3.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
    }

    public List<FileUpload> getBucketfiles(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            log.error("No Bucket Found");
            return null;
        }
        return amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
                .map(file -> new FileUpload(file.getKey(), file.getSize(), file.getETag()))
                .collect(Collectors.toList());
    }

    public String createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(new CreateBucketRequest(bucketName));
            return "Bucket Created \nBucket Name:-" + bucketName +"\nregion:-"
                    + amazonS3.getBucketLocation(new GetBucketLocationRequest(bucketName));
        }
        return "Bucket Already Exist";
    }

    public String fileUplaod(String bucketName, MultipartFile file) {
        String fileName = "";
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                return "Bucket Not Exist";
            }
            fileName = UUID.randomUUID() + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
            log.info("File Uploaded");

        } catch (SdkClientException | IOException e) {
            log.info("File Uploading exception");
            return "Exception";
        }
        return "File Uploaded Successfully \nFileName:- " + fileName;
    }

    public FileUpload downloadFile(String bucketName, String fileName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            log.error("No Bucket Found");
            return null;
        }
        S3Object s3object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUpload fileUpload = new FileUpload();
        try {
            fileUpload.setFile(FileCopyUtils.copyToByteArray(inputStream));
            fileUpload.setFileName(s3object.getKey());
            return fileUpload;
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteFile(String bucketName, String fileName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            log.error("No Bucket Found");
            return "No Bucket Found";
        }
        amazonS3.deleteObject(bucketName, fileName);
        return "File Deleted Successfully";
    }
}
