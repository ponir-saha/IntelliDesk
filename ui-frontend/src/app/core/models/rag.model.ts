export interface Document {
  documentId: string;
  filename: string;
  size: number;
  uploadedAt: Date;
}

export interface DocumentUploadResponse {
  documentId: string;
  filename: string;
  size: number;
  message: string;
  segmentsCreated: number;
}

export interface QuestionRequest {
  question: string;
  maxResults?: number;
}

export interface QuestionResponse {
  answer: string;
  sources: string[];
  confidence: number;
}
