package com.roastmyresume.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private Double matchScore;
    private String feedback;
    private String strengths;
    private String improvements;
    private String missingSkills;
}
