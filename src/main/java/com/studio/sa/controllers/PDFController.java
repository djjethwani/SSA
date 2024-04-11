package com.studio.sa.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class PDFController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    
    @GetMapping("/upload-pdf")
    public String uploadPDF() throws IOException {
        // Assuming you have the PDF file available
        File pdfFile = ResourceUtils.getFile("classpath:sample.pdf");

        // Upload PDF file to Firebase Storage
        return firebaseStorageService.uploadPDF(pdfFile);
    }
}