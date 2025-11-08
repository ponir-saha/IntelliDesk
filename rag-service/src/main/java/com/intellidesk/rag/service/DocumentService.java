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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentUploadResponse uploadDocument(MultipartFile file) throws IOException {
        // Validate file
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename);
        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("File type not allowed: " + extension);
        }

        // Save file
        String documentId = UUID.randomUUID().toString();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String storedFilename = documentId + "_" + filename;
        Path filePath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Parse document
        Document document = parseDocument(file.getInputStream(), extension);
        
        // Split into segments
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
        List<TextSegment> segments = splitter.split(document);

        // Generate embeddings and store
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }

        log.info("Uploaded and processed document: {} with {} segments", filename, segments.size());

        return new DocumentUploadResponse(
                documentId,
                filename,
                file.getSize(),
                "Document uploaded and processed successfully",
                segments.size()
        );
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
