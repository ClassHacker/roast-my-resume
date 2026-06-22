package com.roastmyresume.service;

import com.roastmyresume.model.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeMatcher {

    private final ChatClient chatClient;

    /**
     * Analyze resume against job description using Ollama
     */
    public AnalysisResult analyzeResumeAgainstJobDescription(String resumeContent, String jobDescription) {
        String prompt = buildAnalysisPrompt(resumeContent, jobDescription);
        // call the Spring AI ChatClient with a Prompt containing a single UserMessage
        String response = chatClient.call(new Prompt(new UserMessage(prompt))).getResult().getOutput().getContent();
        return parseAnalysisResponse(response);
    }

    /**
     * Build the prompt for AI analysis
     */
    private String buildAnalysisPrompt(String resumeContent, String jobDescription) {
        return """
                You are an expert resume analyzer and career coach. Analyze the following resume against the job description and provide a detailed analysis.
                
                RESUME:
                %s
                
                JOB DESCRIPTION:
                %s
                
                Please provide your analysis in the following format:
                
                MATCH_SCORE: [0-100]
                STRENGTHS: [List the candidate's relevant strengths]
                IMPROVEMENTS: [List areas for improvement]
                MISSING_SKILLS: [List missing technical or soft skills]
                FEEDBACK: [Provide overall feedback and recommendations]
                
                Be honest but constructive in your feedback.
                """.formatted(resumeContent, jobDescription);
    }

    /**
     * Parse the AI response into AnalysisResult
     */
    private AnalysisResult parseAnalysisResponse(String response) {
        AnalysisResult result = new AnalysisResult();
        
        try {
            // Extract Match Score
            String matchScoreLine = extractSection(response, "MATCH_SCORE:");
            result.setMatchScore(parseScore(matchScoreLine));
            
            // Extract Strengths
            result.setStrengths(extractSection(response, "STRENGTHS:"));
            
            // Extract Improvements
            result.setImprovements(extractSection(response, "IMPROVEMENTS:"));
            
            // Extract Missing Skills
            result.setMissingSkills(extractSection(response, "MISSING_SKILLS:"));
            
            // Extract Feedback
            result.setFeedback(extractSection(response, "FEEDBACK:"));
            
        } catch (Exception e) {
            // If parsing fails, return the raw response as feedback
            result.setFeedback(response);
            result.setMatchScore(0.0);
        }
        
        return result;
    }

    /**
     * Extract a section from the response
     */
    private String extractSection(String response, String sectionHeader) {
        int startIndex = response.indexOf(sectionHeader);
        if (startIndex == -1) {
            return "";
        }
        
        startIndex += sectionHeader.length();
        int endIndex = response.indexOf("\n", startIndex);
        
        if (endIndex == -1) {
            endIndex = response.length();
        }
        
        String section = response.substring(startIndex, endIndex).trim();
        
        // Find next section header
        String[] headers = {"MATCH_SCORE:", "STRENGTHS:", "IMPROVEMENTS:", "MISSING_SKILLS:", "FEEDBACK:"};
        for (String header : headers) {
            int nextIndex = response.indexOf(header, endIndex);
            if (nextIndex != -1 && nextIndex < response.length()) {
                endIndex = nextIndex;
                break;
            }
        }
        
        return response.substring(startIndex, endIndex).trim();
    }

    /**
     * Parse score from string
     */
    private Double parseScore(String scoreString) {
        try {
            return Double.parseDouble(scoreString.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
