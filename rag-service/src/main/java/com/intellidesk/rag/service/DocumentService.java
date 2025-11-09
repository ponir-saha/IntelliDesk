package com.intellidesk.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import com.intellidesk.rag.dto.DocumentUploadResponse;
import com.intellidesk.rag.dto.UploadProgressMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentUploadResponse uploadDocument(MultipartFile file) throws IOException {
        // Validate file
        String filename = file.getOriginalFilename();
        String documentId = UUID.randomUUID().toString();
        
        log.info("📤 Starting upload for file: {} (size: {} bytes)", filename, file.getSize());
        
        // Send WebSocket notification - upload started
        sendProgress(documentId, filename, "started", 0, 0, 0, "Upload started");
        
        if (filename == null || filename.isEmpty()) {
            log.error("❌ Upload failed: Invalid filename");
            sendProgress(documentId, filename, "failed", 0, 0, 0, "Invalid filename");
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename);
        if (!isAllowedExtension(extension)) {
            log.error("❌ Upload failed for {}: File type '{}' not allowed", filename, extension);
            sendProgress(documentId, filename, "failed", 0, 0, 0, "File type not allowed: " + extension);
            throw new IllegalArgumentException("File type not allowed: " + extension);
        }

        try {
            // Save file
            log.info("💾 Saving file: {}", filename);
            sendProgress(documentId, filename, "saving", 0, 0, 0, "Saving file...");
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String storedFilename = documentId + "_" + filename;
            Path filePath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("✅ File saved successfully: {}", storedFilename);

            // Parse document
            log.info("📄 Parsing document: {}", filename);
            sendProgress(documentId, filename, "parsing", 0, 0, 10, "Parsing document...");
            
            Document document = parseDocument(file.getInputStream(), extension);
            log.info("✅ Document parsed successfully");
            
            // Split into segments
            log.info("✂️  Splitting document into segments...");
            sendProgress(documentId, filename, "splitting", 0, 0, 20, "Splitting into segments...");
            
            DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
            List<TextSegment> segments = splitter.split(document);
            log.info("✅ Document split into {} segments", segments.size());

            // Generate embeddings and store
            log.info("🧠 Generating embeddings and storing in vector database...");
            sendProgress(documentId, filename, "embedding", 0, segments.size(), 30, 
                        "Generating embeddings for " + segments.size() + " segments...");
            
            int processedSegments = 0;
            for (TextSegment segment : segments) {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
                processedSegments++;
                
                // Calculate progress (30% to 100% range for embedding phase)
                int progressPercentage = 30 + (processedSegments * 70 / segments.size());
                
                // Send WebSocket update every 10 segments or at completion
                if (processedSegments % 10 == 0 || processedSegments == segments.size()) {
                    sendProgress(documentId, filename, "embedding", 
                                processedSegments, segments.size(), progressPercentage,
                                "Processing segment " + processedSegments + " of " + segments.size());
                }
                
                // Log progress every 50 segments
                if (processedSegments % 50 == 0) {
                    log.info("⏳ Progress: {}/{} segments processed ({}%)", 
                            processedSegments, segments.size(), 
                            (processedSegments * 100 / segments.size()));
                }
            }

            log.info("✅ SUCCESS: Uploaded and processed document '{}' with {} segments", filename, segments.size());
            
            // Send completion notification
            sendProgress(documentId, filename, "completed", segments.size(), segments.size(), 100, 
                        "Document processed successfully with " + segments.size() + " segments");

            return new DocumentUploadResponse(
                    documentId,
                    filename,
                    file.getSize(),
                    "Document uploaded and processed successfully",
                    segments.size()
            );
        } catch (Exception e) {
            log.error("❌ FAILED: Error processing document '{}': {}", filename, e.getMessage(), e);
            sendProgress(documentId, filename, "failed", 0, 0, 0, 
                        "Error: " + e.getMessage());
            throw e;
        }
    }
    
    private void sendProgress(String documentId, String filename, String status, 
                              int currentSegment, int totalSegments, int progressPercentage, 
                              String message) {
        try {
            UploadProgressMessage progressMessage = UploadProgressMessage.builder()
                    .documentId(documentId)
                    .filename(filename)
                    .status(status)
                    .currentSegment(currentSegment)
                    .totalSegments(totalSegments)
                    .progressPercentage(progressPercentage)
                    .message(message)
                    .build();
            
            messagingTemplate.convertAndSend("/topic/upload-progress", progressMessage);
            log.debug("WebSocket progress sent: {} - {}", status, message);
        } catch (Exception e) {
            log.warn("Failed to send WebSocket progress update: {}", e.getMessage());
        }
    }

    private Document parseDocument(InputStream inputStream, String extension) throws IOException {
        DocumentParser parser;
        
        switch (extension.toLowerCase()) {
            case "pdf":
                parser = new ApachePdfBoxDocumentParser();
                break;
            case "doc":
            case "docx":
                parser = new ApachePoiDocumentParser();
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension);
        }

        return parser.parse(inputStream);
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }

    private boolean isAllowedExtension(String extension) {
        return List.of("pdf", "doc", "docx", "txt").contains(extension.toLowerCase());
    }
}
