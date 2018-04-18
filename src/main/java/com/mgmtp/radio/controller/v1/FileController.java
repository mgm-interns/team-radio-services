package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.support.CloudinaryHelper;
import com.mgmtp.radio.support.ContentTypeValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(FileController.BASE_URL)
public class FileController {

    public static final String BASE_URL = "/api/v1/files";

    private final CloudinaryHelper cloudinaryHelper;
    private final ContentTypeValidator contentTypeValidator;


    public FileController(CloudinaryHelper cloudinaryHelper, ContentTypeValidator contentTypeValidator) {
        this.cloudinaryHelper = cloudinaryHelper;
        this.contentTypeValidator = contentTypeValidator;
    }

    @PostMapping("/")
    public RadioSuccessResponse<Object> uploadImage(@RequestParam("file") MultipartFile multipartFile) throws RadioBadRequestException, IOException {
        if (multipartFile == null
                || multipartFile.isEmpty()
                || !contentTypeValidator.isImage(multipartFile)) {
            throw new RadioBadRequestException("Bad request");
        }

        Map<String, String> result = cloudinaryHelper.pushFileToCloud(multipartFile);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("url", result.get("secure_url"));
        return new RadioSuccessResponse<>(response);
    }
}
