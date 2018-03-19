package com.mgmtp.radio.support;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContentTypeValidator {

    public static final String IMAGE_JPEG = "image/jpeg";

    public boolean isJpeg(final byte[] fileContent) throws IOException
    {
        return IMAGE_JPEG.equals(getContentType(convertStreamToHex(new ByteArrayInputStream(fileContent))));
    }

    public boolean isJpeg(final File file) throws IOException
    {
        return IMAGE_JPEG.equals(getContentType(convertStreamToHex(new FileInputStream(file))));
    }

    private List<String> convertStreamToHex(final InputStream stream) throws IOException
    {
        final List<String> hex = new ArrayList<>();

        int value;

        while ( (value = stream.read()) != -1 )
        {
            hex.add(String.format("%02X", value));
        }

        stream.close();

        return hex;
    }

    private String getContentType(final List<String> hex)
    {
        final int hexSize = hex.size();

        final String contentType;

        if ( hex.get(0).equals("FF") && hex.get(1).equals("D8") && hex.get(hexSize - 2).equals("FF") && hex.get(hexSize - 1).equals("D9"))
        {
            contentType = IMAGE_JPEG;
        }
        else
        {
            contentType = null;
        }

        return contentType;
    }
}
