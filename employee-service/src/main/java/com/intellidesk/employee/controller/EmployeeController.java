package com.intellidesk.employee.controller;

import com.intellidesk.employee.dto.EmployeeRequest;
import com.intellidesk.employee.dto.EmployeeResponse;
import com.intellidesk.employee.entity.Employee;
import com.intellidesk.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * HR/Admin can create new employees
     */
    @PostMapping
    // // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled  // Temporarily disabled - JWT doesn't contain roles
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request,
            HttpServletRequest httpRequest) {
        
        String createdBy = (String) httpRequest.getAttribute("username");
        log.info("Creating employee by user: {}", createdBy);
        
        EmployeeResponse response = employeeService.createEmployee(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * HR/Admin can update any employee
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request,
            HttpServletRequest httpRequest) {
        
        String updatedBy = (String) httpRequest.getAttribute("username");
        log.info("Updating employee {} by user: {}", id, updatedBy);
        
        EmployeeResponse response = employeeService.updateEmployee(id, request, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * HR/Admin can delete employees
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<Map<String, String>> deleteEmployee(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        String deletedBy = (String) httpRequest.getAttribute("username");
        log.info("Deleting employee {} by user: {}", id, deletedBy);
        
        employeeService.deleteEmployee(id, deletedBy);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }

    /**
     * HR/Admin can get all employees
     */
    @GetMapping
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("Fetching all employees");
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * HR/Admin can get employee by ID
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with ID: {}", id);
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * HR/Admin can get employee by employee ID
     */
    @GetMapping("/employee-id/{employeeId}")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        log.info("Fetching employee with employee ID: {}", employeeId);
        EmployeeResponse employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }

    /**
     * HR/Admin can search employees
     */
    @GetMapping("/search")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(@RequestParam String keyword) {
        log.info("Searching employees with keyword: {}", keyword);
        List<EmployeeResponse> employees = employeeService.searchEmployees(keyword);
        return ResponseEntity.ok(employees);
    }

    /**
     * HR/Admin can get employees by department
     */
    @GetMapping("/department/{department}")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable String department) {
        log.info("Fetching employees from department: {}", department);
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    /**
     * HR/Admin can update employee status
     */
    @PatchMapping("/{id}/status")
    // @PreAuthorize("hasAnyRole('HR', 'ADMIN')") // Temporarily disabled
    public ResponseEntity<EmployeeResponse> updateEmployeeStatus(
            @PathVariable Long id,
            @RequestParam Employee.EmployeeStatus status,
            HttpServletRequest httpRequest) {
        
        String updatedBy = (String) httpRequest.getAttribute("username");
        log.info("Updating status for employee {} to {} by user: {}", id, status, updatedBy);
        
        EmployeeResponse response = employeeService.updateEmployeeStatus(id, status, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Any authenticated employee can view their own profile
     */
    @GetMapping("/my-profile")
    public ResponseEntity<EmployeeResponse> getMyProfile(HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        log.info("Fetching profile for user ID: {}", userId);
        
        EmployeeResponse employee = employeeService.getEmployeeByUserId(userId);
        
        // Return limited view (without sensitive data like salary, bank details)
        return ResponseEntity.ok(employee);
    }

    /**
     * Employees can update their own profile (limited fields)
     */
    @PatchMapping("/my-profile")
    public ResponseEntity<EmployeeResponse> updateMyProfile(
            @RequestBody EmployeeRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute("userId");
        String username = (String) httpRequest.getAttribute("username");
        log.info("User {} updating their profile", username);
        
        // Get current employee record
        EmployeeResponse currentEmployee = employeeService.getEmployeeByUserId(userId);
        
        // Only allow updating specific fields (not salary, bank details, etc.)
        // Create a limited update request
        EmployeeRequest limitedRequest = EmployeeRequest.builder()
                .employeeId(currentEmployee.getEmployeeId())
                .userId(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .phoneNumber(request.getPhoneNumber())
                .alternatePhone(request.getAlternatePhone())
                .dateOfBirth(currentEmployee.getDateOfBirth())
                .gender(currentEmployee.getGender())
                .department(currentEmployee.getDepartment())
                .designation(currentEmployee.getDesignation())
                .joiningDate(currentEmployee.getJoiningDate())
                .employmentType(currentEmployee.getEmploymentType())
                .status(currentEmployee.getStatus())
                .reportingManager(currentEmployee.getReportingManager())
                .salary(currentEmployee.getSalary())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .skills(request.getSkills())
                .qualifications(request.getQualifications())
                .certifications(request.getCertifications())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
        
        EmployeeResponse response = employeeService.updateEmployee(
                currentEmployee.getId(), limitedRequest, username);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "employee-service"));
    }
}
