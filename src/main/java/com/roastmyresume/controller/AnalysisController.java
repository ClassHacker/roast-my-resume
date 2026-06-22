package com.roastmyresume.controller;

import com.roastmyresume.model.AnalysisResult;
import com.roastmyresume.model.Resume;
import com.roastmyresume.service.ResumeParserService;
import com.roastmyresume.service.ResumeMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/analyze")
@RequiredArgsConstructor
public class AnalysisController {

    private final ResumeParserService resumeParserService;
    private final ResumeMatcher resumeMatcher;

    /**
     * Analyze resume against job description
     * POST /api/analyze
     * Request body:
     * - resumeFile: PDF file (multipart)
     * - jobDescription: Job description text
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription) {
        
        try {
            // Validate PDF file
            if (!resumeParserService.isPdfFile(resumeFile)) {
                return ResponseEntity.badRequest().body("File must be a PDF");
            }

            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Job description cannot be empty");
            }

            // Parse resume
            Resume resume = resumeParserService.parseResume(resumeFile);

            // Analyze against job description
            AnalysisResult result = resumeMatcher.analyzeResumeAgainstJobDescription(
                    resume.getContent(),
                    jobDescription
            );

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing PDF: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error analyzing resume: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Resume Analyzer is running!");
    }
}
