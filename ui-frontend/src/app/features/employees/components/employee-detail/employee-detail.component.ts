import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService, Employee } from '../../../../core/services/employee.service';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee-detail.component.html',
  styleUrls: ['./employee-detail.component.css']
})
export class EmployeeDetailComponent implements OnInit {
  employee: Employee | null = null;
  loading: boolean = false;
  error: string = '';
  isHR: boolean = false;
  isAccounts: boolean = false;
  isOwnProfile: boolean = false;
  
  // For salary editing
  editingSalary: boolean = false;
  newSalary: number = 0;

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log('EmployeeDetailComponent initialized');
    this.checkUserRole();
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Route parameter id:', id);
    console.log('Current URL:', this.router.url);
    
    if (id === 'my-profile' || this.router.url.includes('/my-profile')) {
      console.log('Loading my profile...');
      this.loadMyProfile();
    } else if (id) {
      console.log('Loading employee by id:', id);
      this.loadEmployee(+id);
    } else {
      console.error('No id parameter found!');
    }
  }

  checkUserRole(): void {
    this.isHR = this.authService.isHR();
    this.isAccounts = this.authService.isAccounts();
  }

  canEditInfo(): boolean {
    return this.isHR && !this.isOwnProfile;
  }

  canEditSalary(): boolean {
    return this.isAccounts && !this.isOwnProfile;
  }

  canViewSalary(): boolean {
    return this.isHR || this.isAccounts || this.isOwnProfile;
  }

  loadEmployee(id: number): void {
    this.loading = true;
    this.error = '';

    this.employeeService.getEmployeeById(id).subscribe({
      next: (data) => {
        this.employee = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading employee:', err);
        this.error = 'Failed to load employee details';
        this.loading = false;
      }
    });
  }

  loadMyProfile(): void {
    this.loading = true;
    this.error = '';
    this.isOwnProfile = true;
    
    console.log('Calling employeeService.getMyProfile()');
    console.log('Token exists:', !!localStorage.getItem('token'));

    this.employeeService.getMyProfile().subscribe({
      next: (data) => {
        console.log('Profile loaded successfully:', data);
        this.employee = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading profile:', err);
        console.error('Error status:', err.status);
        console.error('Error message:', err.message);
        console.error('Full error:', JSON.stringify(err, null, 2));
        this.error = 'Failed to load your profile. Please try logging in again.';
        this.loading = false;
      }
    });
  }

  editEmployee(): void {
    if (this.isOwnProfile) {
      this.router.navigate(['/employees/my-profile/edit']);
    } else if (this.employee?.id) {
      this.router.navigate(['/employees', this.employee.id, 'edit']);
    }
  }

  startEditSalary(): void {
    if (!this.canEditSalary()) return;
    this.editingSalary = true;
    this.newSalary = this.employee?.salary || 0;
  }

  cancelEditSalary(): void {
    this.editingSalary = false;
    this.newSalary = 0;
  }

  saveSalary(): void {
    if (!this.employee?.id || !this.canEditSalary()) return;

    this.loading = true;
    this.employeeService.updateEmployeeSalary(this.employee.id, this.newSalary).subscribe({
      next: (data) => {
        this.employee = data;
        this.editingSalary = false;
        this.notificationService.success('Salary updated successfully');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error updating salary:', err);
        this.notificationService.error('Failed to update salary');
        this.loading = false;
      }
    });
  }

  deleteEmployee(): void {
    if (!this.employee?.id) return;

    if (confirm(`Are you sure you want to delete this employee?`)) {
      this.employeeService.deleteEmployee(this.employee.id).subscribe({
        next: () => {
          this.notificationService.success('Employee deleted successfully');
          this.router.navigate(['/employees']);
        },
        error: (err) => {
          console.error('Error deleting employee:', err);
          this.notificationService.error('Failed to delete employee');
        }
      });
    }
  }

  updateStatus(status: string): void {
    if (!this.employee?.id) return;

    this.employeeService.updateEmployeeStatus(this.employee.id, status).subscribe({
      next: (data) => {
        this.employee = data;
        this.notificationService.success('Status updated successfully');
      },
      error: (err) => {
        console.error('Error updating status:', err);
        this.notificationService.error('Failed to update status');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/employees']);
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'status-active';
      case 'INACTIVE': return 'status-inactive';
      case 'ON_LEAVE': return 'status-leave';
      case 'TERMINATED': return 'status-terminated';
      case 'RESIGNED': return 'status-resigned';
      default: return '';
    }
  }
}
