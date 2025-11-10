# WebSocket Integration for Document Upload Progress

## Backend WebSocket Endpoint

The RAG service now exposes a WebSocket endpoint for real-time upload progress:

- **WebSocket URL**: `ws://localhost:8083/ws`
- **Subscribe Topic**: `/topic/upload-progress`

## Progress Message Format

```json
{
  "documentId": "uuid-string",
  "filename": "document.pdf",
  "status": "embedding",
  "currentSegment": 50,
  "totalSegments": 834,
  "progressPercentage": 60,
  "message": "Processing segment 50 of 834"
}
```

### Status Values:
- `started` - Upload initiated
- `saving` - Saving file to disk
- `parsing` - Parsing document content
- `splitting` - Splitting into text segments
- `embedding` - Generating embeddings (main progress phase)
- `completed` - Successfully completed
- `failed` - Error occurred

## Angular Frontend Integration

### 1. Install SockJS and STOMP dependencies:
```bash
npm install sockjs-client @stomp/stompjs
npm install --save-dev @types/sockjs-client
```

### 2. Create WebSocket Service (websocket.service.ts):

```typescript
import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';

export interface UploadProgress {
  documentId: string;
  filename: string;
  status: string;
  currentSegment: number;
  totalSegments: number;
  progressPercentage: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private progressSubject = new Subject<UploadProgress>();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8083/ws'),
      debug: (str) => {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to WebSocket');
      this.client.subscribe('/topic/upload-progress', (message) => {
        const progress: UploadProgress = JSON.parse(message.body);
        this.progressSubject.next(progress);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
    };
  }

  connect(): void {
    this.client.activate();
  }

  disconnect(): void {
    this.client.deactivate();
  }

  getProgressUpdates(): Observable<UploadProgress> {
    return this.progressSubject.asObservable();
  }
}
```

### 3. Use in Component (document-upload.component.ts):

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService, UploadProgress } from './websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrls: ['./document-upload.component.css']
})
export class DocumentUploadComponent implements OnInit, OnDestroy {
  uploadProgress = 0;
  uploadStatus = '';
  uploadMessage = '';
  currentSegment = 0;
  totalSegments = 0;
  isUploading = false;
  
  private progressSubscription?: Subscription;

  constructor(private wsService: WebSocketService) {}

  ngOnInit(): void {
    // Connect to WebSocket
    this.wsService.connect();
    
    // Subscribe to progress updates
    this.progressSubscription = this.wsService.getProgressUpdates().subscribe(
      (progress: UploadProgress) => {
        console.log('Progress update:', progress);
        this.uploadProgress = progress.progressPercentage;
        this.uploadStatus = progress.status;
        this.uploadMessage = progress.message;
        this.currentSegment = progress.currentSegment;
        this.totalSegments = progress.totalSegments;
        
        if (progress.status === 'completed') {
          this.isUploading = false;
          setTimeout(() => {
            this.resetProgress();
          }, 3000);
        } else if (progress.status === 'failed') {
          this.isUploading = false;
        } else {
          this.isUploading = true;
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.progressSubscription?.unsubscribe();
    this.wsService.disconnect();
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadFile(file);
    }
  }

  uploadFile(file: File): void {
    const formData = new FormData();
    formData.append('file', file);

    // Upload file via HTTP
    fetch('http://localhost:8083/rag/documents/upload', {
      method: 'POST',
      body: formData,
      // Add auth headers if needed
    })
    .then(response => response.json())
    .then(data => {
      console.log('Upload response:', data);
    })
    .catch(error => {
      console.error('Upload error:', error);
      this.isUploading = false;
    });
  }

  resetProgress(): void {
    this.uploadProgress = 0;
    this.uploadStatus = '';
    this.uploadMessage = '';
    this.currentSegment = 0;
    this.totalSegments = 0;
  }
}
```

### 4. Template (document-upload.component.html):

```html
<div class="upload-container">
  <h2>Upload Document</h2>
  
  <input type="file" 
         (change)="onFileSelected($event)" 
         [disabled]="isUploading"
         accept=".pdf,.doc,.docx">

  <div *ngIf="isUploading || uploadProgress > 0" class="progress-container">
    <h3>{{ uploadMessage }}</h3>
    
    <!-- Progress Bar -->
    <div class="progress-bar-container">
      <div class="progress-bar" 
           [style.width.%]="uploadProgress">
        {{ uploadProgress }}%
      </div>
    </div>
    
    <!-- Status -->
    <div class="status-info">
      <p><strong>Status:</strong> {{ uploadStatus }}</p>
      <p *ngIf="totalSegments > 0">
        <strong>Progress:</strong> {{ currentSegment }} / {{ totalSegments }} segments
      </p>
    </div>

    <!-- Status Icons -->
    <div class="status-icons">
      <span *ngIf="uploadStatus === 'started'">📤 Starting...</span>
      <span *ngIf="uploadStatus === 'saving'">💾 Saving...</span>
      <span *ngIf="uploadStatus === 'parsing'">📄 Parsing...</span>
      <span *ngIf="uploadStatus === 'splitting'">✂️ Splitting...</span>
      <span *ngIf="uploadStatus === 'embedding'">🧠 Processing...</span>
      <span *ngIf="uploadStatus === 'completed'">✅ Completed!</span>
      <span *ngIf="uploadStatus === 'failed'">❌ Failed!</span>
    </div>
  </div>
</div>
```

### 5. Styles (document-upload.component.css):

```css
.upload-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.progress-container {
  margin-top: 20px;
}

.progress-bar-container {
  width: 100%;
  height: 30px;
  background-color: #f0f0f0;
  border-radius: 15px;
  overflow: hidden;
  margin: 15px 0;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.status-info {
  margin: 15px 0;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 5px;
}

.status-icons {
  font-size: 1.5em;
  text-align: center;
  padding: 10px;
}

input[type="file"] {
  padding: 10px;
  border: 2px dashed #ddd;
  border-radius: 5px;
  width: 100%;
  cursor: pointer;
}

input[type="file"]:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
```

## Testing the WebSocket

You can test the WebSocket connection using this simple HTML page:

```html
<!DOCTYPE html>
<html>
<head>
    <title>RAG Upload Progress Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>
</head>
<body>
    <h1>Document Upload Progress</h1>
    <div id="status">Connecting...</div>
    <div id="progress"></div>
    
    <script>
        const socket = new SockJS('http://localhost:8083/ws');
        const stompClient = Stomp.over(socket);
        
        stompClient.connect({}, function(frame) {
            document.getElementById('status').textContent = 'Connected!';
            console.log('Connected:', frame);
            
            stompClient.subscribe('/topic/upload-progress', function(message) {
                const progress = JSON.parse(message.body);
                console.log('Progress:', progress);
                
                document.getElementById('progress').innerHTML = `
                    <h3>${progress.filename}</h3>
                    <p>Status: ${progress.status}</p>
                    <p>Progress: ${progress.progressPercentage}%</p>
                    <p>Segments: ${progress.currentSegment} / ${progress.totalSegments}</p>
                    <p>${progress.message}</p>
                `;
            });
        });
    </script>
</body>
</html>
```

## API Gateway Configuration

If accessing through API Gateway (port 8080), update the WebSocket URL:

```typescript
// Instead of: http://localhost:8083/ws
// Use: http://localhost:8080/rag-service/ws
```

And ensure API Gateway routes WebSocket connections properly.
