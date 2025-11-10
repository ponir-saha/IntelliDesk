package com.intellidesk.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadProgressMessage {
    private String documentId;
    private String filename;
    private String status; // "started", "saving", "parsing", "splitting", "embedding", "completed", "failed"
    private int currentSegment;
    private int totalSegments;
    private int progressPercentage;
    private String message;
    private String error;
}
