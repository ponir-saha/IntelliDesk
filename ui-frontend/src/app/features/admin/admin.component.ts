import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatCardModule],
  template: `
    <mat-toolbar color="primary">
      <span>Admin Panel</span>
    </mat-toolbar>

    <div class="container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>User Management</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>User management features coming soon...</p>
        </mat-card-content>
      </mat-card>

      <mat-card>
        <mat-card-header>
          <mat-card-title>Role Management</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Role management features coming soon...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .container {
      padding: 20px;
    }

    mat-card {
      margin-bottom: 20px;
    }
  `]
})
export class AdminComponent {}
