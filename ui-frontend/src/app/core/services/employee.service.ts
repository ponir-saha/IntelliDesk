import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Employee {
  id?: number;
  employeeId: string;
  userId?: string;  // Changed from number to string (UUID)
  email: string;
  firstName: string;
  lastName: string;
  middleName?: string;
  fullName?: string;  // Added fullName field
  phoneNumber: string;
  alternatePhone?: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  department: string;
  designation: string;
  joiningDate: string;
  employmentType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERN' | 'CONSULTANT';
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'TERMINATED' | 'RESIGNED';
  reportingManager?: string;
  salary?: number;
  address?: string;  // Made optional
  city?: string;  // Added city field
  state?: string;  // Added state field
  country?: string;  // Added country field
  postalCode?: string;  // Added postalCode field
  emergencyContactName?: string;  // Made optional
  emergencyContactPhone?: string;  // Made optional
  emergencyContactRelation?: string;  // Made optional
  bankName?: string;
  bankAccountNumber?: string;
  bankIfscCode?: string;
  skills?: string;
  qualifications?: string;
  certifications?: string;
  notes?: string;
  profileImageUrl?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface EmployeeRequest {
  employeeId: string;
  userId?: number;
  email: string;
  firstName: string;
  lastName: string;
  middleName?: string;
  phoneNumber: string;
  alternatePhone?: string;
  dateOfBirth: string;
  gender: string;
  department: string;
  designation: string;
  joiningDate: string;
  employmentType: string;
  status: string;
  reportingManager?: string;
  salary?: number;
  address: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  emergencyContactRelation: string;
  bankName?: string;
  bankAccountNumber?: string;
  bankIfscCode?: string;
  skills?: string;
  qualifications?: string;
  certifications?: string;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Get all employees (HR/ADMIN only)
   */
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl, {
      headers: this.getHeaders()
    });
  }

  /**
   * Get employee by ID (HR/ADMIN only)
   */
  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Get current user's profile
   */
  getMyProfile(): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/my-profile`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Search employees (HR/ADMIN only)
   */
  searchEmployees(keyword: string): Observable<Employee[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Employee[]>(`${this.apiUrl}/search`, {
      headers: this.getHeaders(),
      params: params
    });
  }

  /**
   * Create new employee (HR/ADMIN only)
   */
  createEmployee(employee: EmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee, {
      headers: this.getHeaders()
    });
  }

  /**
   * Update employee (HR/ADMIN only)
   */
  updateEmployee(id: number, employee: EmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}`, employee, {
      headers: this.getHeaders()
    });
  }

  /**
   * Update my profile (any authenticated user)
   */
  updateMyProfile(employee: Partial<EmployeeRequest>): Observable<Employee> {
    return this.http.patch<Employee>(`${this.apiUrl}/my-profile`, employee, {
      headers: this.getHeaders()
    });
  }

  /**
   * Update employee status (HR/ADMIN only)
   */
  updateEmployeeStatus(id: number, status: string): Observable<Employee> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<Employee>(`${this.apiUrl}/${id}/status`, {}, {
      headers: this.getHeaders(),
      params: params
    });
  }

  /**
   * Delete employee (HR/ADMIN only)
   */
  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Get employees by department (HR/ADMIN only)
   */
  getEmployeesByDepartment(department: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/department/${department}`, {
      headers: this.getHeaders()
    });
  }
}
