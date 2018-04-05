package com.mgmtp.radio.support;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryHelper {

    @Autowired
    Cloudinary cloudinary;

    public Map<String, String> pushFileToCloud(MultipartFile file) throws IOException {

        return this.pushFileToCloud(file.getBytes());
    }

    public Map<String, String> pushFileToCloud(byte[] file) throws IOException {
        Map<String, String> uploadResult = cloudinary.uploader().upload(file,  ObjectUtils.asMap("resource_type", "auto"));

        return uploadResult;
    }

    public Map<String, String> deleteFileByUrl(String url) throws IOException {
        String fileNameWithType = url.substring(url.lastIndexOf("/") + 1);
        String fileNameWithoutType = fileNameWithType.substring(0, fileNameWithType.lastIndexOf("."));

        return cloudinary.uploader().destroy(fileNameWithoutType, ObjectUtils.emptyMap());
    }


}
