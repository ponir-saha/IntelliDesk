package com.intellidesk.rag.controller;

import com.intellidesk.rag.dto.DocumentUploadResponse;
import com.intellidesk.rag.dto.QuestionRequest;
import com.intellidesk.rag.dto.QuestionResponse;
import com.intellidesk.rag.service.DocumentService;
import com.intellidesk.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

    private final DocumentService documentService;
    private final RagService ragService;

    @PostMapping("/documents/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file));
    }

    @PostMapping("/question")
    public ResponseEntity<QuestionResponse> askQuestion(@RequestBody QuestionRequest request) {
        return ResponseEntity.ok(ragService.answerQuestion(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG Service is running");
    }
}
