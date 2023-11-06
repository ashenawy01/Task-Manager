package com.sigma.taskmanagaer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
public class FileService {


    private final AmazonS3 amazonS3;
    private final String bucketName;

    public FileService(AmazonS3 amazonS3, @Value("${aws.s3.bucketName}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String saveImg(MultipartFile multipartFile, String phoneNo) throws IOException {

        long currentSizeInBytes = multipartFile.getSize();
        long targetSizeInBytes = 100 * 1024; // 100 KB target size

        String fileName = nameImg(multipartFile.getOriginalFilename(), phoneNo);
        String folderName = "images/";
        String key = folderName + fileName;

        if (currentSizeInBytes <= targetSizeInBytes) {
            // Image is already smaller than or equal to the target size, no need to compress
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(currentSizeInBytes);

            amazonS3.putObject(bucketName, key, multipartFile.getInputStream(), metadata);
        } else {
            // Compress the image before uploading
            byte[] compressedImageBytes = compressImage(multipartFile);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(compressedImageBytes.length);

            InputStream inputStream = new ByteArrayInputStream(compressedImageBytes);

            amazonS3.putObject(bucketName, key, inputStream, metadata);
        }
        return fileName;
    }

    public String saveFile(MultipartFile multipartFile) throws IOException {

        String key = "files/" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        amazonS3.putObject(bucketName, key, multipartFile.getInputStream(), metadata);

        return key;
    }

    private String nameImg(String orgName, String phoneNo){
        int dotPos = orgName.lastIndexOf(".");
        String ext = orgName.substring(dotPos);
        return "user" + phoneNo + ext ;
    }

    // Compress Image to approach 100 KB
    private byte[] compressImage(MultipartFile multipartFile) throws IOException {
        BufferedImage image = ImageIO.read(multipartFile.getInputStream());

        // Target size in bytes (100 KB in this case)
        long targetSizeInBytes = 100 * 1024;

        // Start with a high compression quality
        double compressionQuality = 1.0;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean success = false;

        // Keep reducing the compression quality until the image size is smaller than the target
        while (!success && compressionQuality >= 0.05) {
            baos.reset();

            // Compress the image with the current compression quality
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality((float) compressionQuality);

            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);

            // Check if the compressed image size is within the target range
            success = baos.size() <= targetSizeInBytes;

            compressionQuality -= 0.05;
        }

        return baos.toByteArray();
    }

}
