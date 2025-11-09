import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RagService } from '../../core/services/rag.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule
  ],
  template: `
    <mat-toolbar color="primary">
      <span>IntelliDesk</span>
      <span class="spacer"></span>
      <button mat-button routerLink="/employees/my-profile">
        <mat-icon>person</mat-icon>
        My Profile
      </button>
      <button mat-button routerLink="/employees" *ngIf="isHR">
        <mat-icon>people</mat-icon>
        Employees
      </button>
      <span>{{currentUser?.username}}</span>
      <button mat-icon-button (click)="logout()">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>

    <div class="container">
      <div class="dashboard-grid">
        <!-- Document Upload -->
        <mat-card>
          <mat-card-header>
            <mat-card-title>Upload Document</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <input type="file" (change)="onFileSelected($event)" accept=".pdf,.doc,.docx">
            <button mat-raised-button color="primary" (click)="uploadDocument()" [disabled]="!selectedFile">
              Upload
            </button>
            <div *ngIf="uploadMessage" class="message">{{uploadMessage}}</div>
          </mat-card-content>
        </mat-card>

        <!-- Chat Interface -->
        <mat-card class="chat-card">
          <mat-card-header>
            <mat-card-title>Ask a Question</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="chat-messages">
              <div *ngFor="let msg of messages" [class]="'message ' + msg.type">
                <div class="message-content">
                  <strong>{{msg.type === 'question' ? 'You' : 'AI'}}:</strong>
                  <p>{{msg.text}}</p>
                  <div *ngIf="msg.sources && msg.sources.length > 0" class="sources">
                    <small>Sources: {{msg.sources.length}}</small>
                  </div>
                </div>
              </div>
            </div>

            <form [formGroup]="questionForm" (ngSubmit)="askQuestion()">
              <mat-form-field class="full-width">
                <mat-label>Your question</mat-label>
                <input matInput formControlName="question">
              </mat-form-field>
              <button mat-raised-button color="primary" type="submit">Ask</button>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .container {
      padding: 20px;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 20px;
    }

    .chat-card {
      min-height: 500px;
    }

    .chat-messages {
      max-height: 400px;
      overflow-y: auto;
      margin-bottom: 20px;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
    }

    .message {
      margin-bottom: 16px;
      padding: 12px;
      border-radius: 8px;
    }

    .message.question {
      background-color: #e3f2fd;
      margin-left: 20%;
    }

    .message.answer {
      background-color: #f5f5f5;
      margin-right: 20%;
    }

    .message-content p {
      margin: 8px 0;
    }

    .sources {
      margin-top: 8px;
      color: #666;
    }

    .spacer {
      flex: 1 1 auto;
    }

    .message {
      margin-top: 16px;
      padding: 8px;
      background: #e8f5e9;
      border-radius: 4px;
    }
  `]
})
export class DashboardComponent {
  private authService = inject(AuthService);
  private ragService = inject(RagService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  currentUser = this.authService.getCurrentUser();
  isHR = false;
  selectedFile: File | null = null;
  uploadMessage = '';
  
  messages: Array<{type: 'question' | 'answer', text: string, sources?: string[]}> = [];

  questionForm: FormGroup = this.fb.group({
    question: ['']
  });

  constructor() {
    // Check if user has HR role
    if (this.currentUser?.roles) {
      this.isHR = this.currentUser.roles.includes('ROLE_HR') || 
                  this.currentUser.roles.includes('ROLE_ADMIN');
    }
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  uploadDocument(): void {
    if (this.selectedFile) {
      this.ragService.uploadDocument(this.selectedFile).subscribe({
        next: (response) => {
          this.uploadMessage = `${response.message} (${response.segmentsCreated} segments created)`;
          this.selectedFile = null;
        },
        error: (error) => {
          this.uploadMessage = 'Upload failed: ' + error.message;
        }
      });
    }
  }

  askQuestion(): void {
    const question = this.questionForm.value.question;
    if (question) {
      this.messages.push({ type: 'question', text: question });
      
      this.ragService.askQuestion({ question }).subscribe({
        next: (response) => {
          this.messages.push({ 
            type: 'answer', 
            text: response.answer,
            sources: response.sources
          });
          this.questionForm.reset();
        },
        error: (error) => {
          this.messages.push({ 
            type: 'answer', 
            text: 'Error: ' + error.message
          });
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
