package com.dokkebi.officefinder.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dokkebi.officefinder.exception.CustomException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3Client amazonS3Client;
  private static final String OFFICE_IMAGE_STORE_PATH = "/office/images";

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public List<String> uploadOfficeImages(List<MultipartFile> multipartFileList) {
    List<String> imageUrlList = new ArrayList<>();

    for (var image : multipartFileList) {
      if (image.getOriginalFilename().equals("")) continue;
      String fileName = createUniqueFileName(image.getOriginalFilename());

      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(image.getSize());
      objectMetadata.setContentType(image.getContentType());

      try (InputStream inputStream = image.getInputStream()) {
        amazonS3Client.putObject(
            new PutObjectRequest(bucket + OFFICE_IMAGE_STORE_PATH, fileName, inputStream,
                objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead)
        );

        imageUrlList.add(
            amazonS3Client.getUrl(bucket + OFFICE_IMAGE_STORE_PATH, fileName).toString()
        );
      } catch (IOException e) {
        throw new CustomException();
      }
    }

    return imageUrlList;
  }

  public void deleteImages(List<String> fileUrlList) {
    String separator = ".com/";

    for (String fileUrl : fileUrlList) {
      String fileName = fileUrl.substring(fileUrl.lastIndexOf(separator) + separator.length());
      amazonS3Client.deleteObject(bucket, fileName);
    }
  }

  private String createUniqueFileName(String fileName) {
    return UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  private String getFileExtension(String fileName) {
    Set<String> fileExtensionSet = new HashSet<>(
        List.of(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG")
    );
    String idxFileName = fileName.substring(fileName.lastIndexOf("."));

    if (!fileExtensionSet.contains(idxFileName)) {
      throw new CustomException();
    }

    return fileName.substring(fileName.lastIndexOf("."));
  }
}
