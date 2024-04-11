package com.studio.sa.controllers;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class FirebaseStorageService {
    public String uploadPDF(File pdfFile) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get("garmenta-e8cd2.appspot.com"); // Replace with your Firebase Storage bucket name

        FileInputStream fileInputStream = new FileInputStream(pdfFile);
        Blob down = bucket.create("Orders/"+pdfFile.getName(), fileInputStream, "application/pdf");
        String downloadUrl = down.getMediaLink();
        downloadUrl = downloadUrl.split("\\?")[0];
        downloadUrl += "?alt=media";
        fileInputStream.close();
        return downloadUrl;
    }
}