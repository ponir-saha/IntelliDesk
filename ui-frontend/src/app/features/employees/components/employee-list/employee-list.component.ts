import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService, Employee } from '../../../../core/services/employee.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  searchKeyword: string = '';
  loading: boolean = false;
  error: string = '';
  isHR: boolean = false;
  isAccounts: boolean = false;

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.checkUserRole();
    if (this.canViewEmployeeList()) {
      this.loadEmployees();
    } else {
      this.error = 'You do not have permission to view employee list';
    }
  }

  checkUserRole(): void {
    this.isHR = this.authService.isHR();
    this.isAccounts = this.authService.isAccounts();
  }

  canViewEmployeeList(): boolean {
    return this.authService.canViewEmployeeList();
  }

  canEditInfo(): boolean {
    return this.isHR;
  }

  canEditSalary(): boolean {
    return this.isAccounts;
  }

  loadEmployees(): void {
    this.loading = true;
    this.error = '';
    
    console.log('Loading employees...');
    console.log('Token:', localStorage.getItem('token')?.substring(0, 50) + '...');
    console.log('User:', localStorage.getItem('user'));

    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        console.log('Employees loaded successfully:', data.length);
        this.employees = data;
        this.filteredEmployees = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading employees:', err);
        console.error('Error status:', err.status);
        console.error('Error message:', err.message);
        this.error = 'Failed to load employees. You may not have permission to view all employees.';
        this.loading = false;
      }
    });
  }

  searchEmployees(): void {
    if (!this.searchKeyword.trim()) {
      this.filteredEmployees = this.employees;
      return;
    }

    this.loading = true;
    this.employeeService.searchEmployees(this.searchKeyword).subscribe({
      next: (data) => {
        this.filteredEmployees = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error searching employees:', err);
        this.error = 'Failed to search employees';
        this.loading = false;
      }
    });
  }

  viewEmployee(id: number): void {
    this.router.navigate(['/employees', id]);
  }

  editEmployee(id: number): void {
    this.router.navigate(['/employees', id, 'edit']);
  }

  createEmployee(): void {
    this.router.navigate(['/employees', 'new']);
  }

  deleteEmployee(id: number, employeeId: string): void {
    if (confirm(`Are you sure you want to delete employee ${employeeId}?`)) {
      this.employeeService.deleteEmployee(id).subscribe({
        next: () => {
          alert('Employee deleted successfully');
          this.loadEmployees();
        },
        error: (err) => {
          console.error('Error deleting employee:', err);
          alert('Failed to delete employee');
        }
      });
    }
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
