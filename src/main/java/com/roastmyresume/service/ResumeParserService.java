package com.roastmyresume.service;

import com.roastmyresume.model.Resume;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeParserService {

    /**
     * Parse PDF resume file and extract text content
     */
    public Resume parseResume(MultipartFile file) throws IOException {
        String content = extractTextFromPDF(file);
        return new Resume(content, file.getOriginalFilename());
    }

    /**
     * Extract text from PDF file using Apache PDFBox
     */
    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream(); PDDocument document = Loader.loadPDF(is.readAllBytes())) {
            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Validate if file is a PDF
     */
    public boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}
