package com.dokkebi.officefinder.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadImageFile(MultipartFile multipartFile, String dirName) throws IOException {
    File uploadFile = convert(multipartFile)
        .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> file 변환 문제 발생"));

    return uploadImage(uploadFile, dirName);
  }

  private String uploadImage(File uploadFile, String dirName){
    String fileName = dirName + "/" + uploadFile.getName();
    String uploadImageUrl = putS3(uploadFile, fileName);

    removeFile(uploadFile);

    return uploadImageUrl;
  }

  private String putS3(File uploadFile, String fileName){
    amazonS3Client.putObject(
        new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead)
    );

    return amazonS3Client.getUrl(bucket, fileName).toString();
  }

  private void removeFile(File targetFile){
    if (targetFile.delete()){
      log.info("file delete success");
    } else{
      log.info("file delete failed");
    }
  }

  private Optional<File> convert(MultipartFile multipartFile) throws IOException {
    File convertFile = new File(multipartFile.getOriginalFilename());

    if (convertFile.createNewFile()) {
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(multipartFile.getBytes());
      }
      return Optional.of(convertFile);
    }
    return Optional.empty();
  }
}
