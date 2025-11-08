package com.intellidesk.rag.service;

import com.intellidesk.rag.dto.QuestionRequest;
import com.intellidesk.rag.dto.QuestionResponse;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final OpenAiChatModel chatModel;

    public QuestionResponse answerQuestion(QuestionRequest request) {
        String question = request.getQuestion();
        int maxResults = request.getMaxResults() != null ? request.getMaxResults() : 5;

        // Generate embedding for the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // Find relevant segments
        List<EmbeddingMatch<TextSegment>> relevantSegments = 
                embeddingStore.findRelevant(questionEmbedding, maxResults);

        if (relevantSegments.isEmpty()) {
            return new QuestionResponse(
                    "I don't have enough information to answer this question.",
                    List.of(),
                    0.0
            );
        }

        // Build context from relevant segments
        String context = relevantSegments.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        // Build prompt
        String prompt = buildPrompt(question, context);

        // Generate answer using LLM
        String answer = chatModel.generate(prompt);

        // Extract sources
        List<String> sources = relevantSegments.stream()
                .map(match -> match.embedded().text().substring(0, 
                        Math.min(100, match.embedded().text().length())) + "...")
                .collect(Collectors.toList());

        // Calculate average confidence score
        double confidence = relevantSegments.stream()
                .mapToDouble(EmbeddingMatch::score)
                .average()
                .orElse(0.0);

        log.info("Answered question with confidence: {}", confidence);

        return new QuestionResponse(answer, sources, confidence);
    }

    private String buildPrompt(String question, String context) {
        return String.format(
                """
                You are a helpful AI assistant for IntelliDesk. 
                Answer the following question based on the provided context.
                If the context doesn't contain enough information, say so clearly.
                
                Context:
                %s
                
                Question: %s
                
                Answer:
                """,
                context,
                question
        );
    }
}
