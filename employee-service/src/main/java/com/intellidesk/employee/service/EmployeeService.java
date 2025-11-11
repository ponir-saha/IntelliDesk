package com.intellidesk.employee.service;

import com.intellidesk.employee.dto.EmployeeRequest;
import com.intellidesk.employee.dto.EmployeeResponse;
import com.intellidesk.employee.entity.Employee;
import com.intellidesk.employee.exception.EmployeeNotFoundException;
import com.intellidesk.employee.exception.ResourceAlreadyExistsException;
import com.intellidesk.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String createdBy) {
        log.info("Creating employee with ID: {}", request.getEmployeeId());

        // Check if employee ID already exists
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new ResourceAlreadyExistsException("Employee with ID " + request.getEmployeeId() + " already exists");
        }

        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Employee with email " + request.getEmail() + " already exists");
        }

        Employee employee = Employee.builder()
                .employeeId(request.getEmployeeId())
                .userId(request.getUserId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .phoneNumber(request.getPhoneNumber())
                .alternatePhone(request.getAlternatePhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .employmentType(request.getEmploymentType())
                .status(request.getStatus() != null ? request.getStatus() : Employee.EmployeeStatus.ACTIVE)
                .reportingManager(request.getReportingManager())
                .salary(request.getSalary())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankIfscCode(request.getBankIfscCode())
                .skills(request.getSkills())
                .qualifications(request.getQualifications())
                .certifications(request.getCertifications())
                .notes(request.getNotes())
                .profileImageUrl(request.getProfileImageUrl())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {}", savedEmployee.getEmployeeId());

        return EmployeeResponse.fromEntity(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request, String updatedBy) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        // Check if email is being changed and if it already exists
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Employee with email " + request.getEmail() + " already exists");
        }

        // Update fields
        employee.setEmail(request.getEmail());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setMiddleName(request.getMiddleName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setAlternatePhone(request.getAlternatePhone());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        employee.setReportingManager(request.getReportingManager());
        employee.setSalary(request.getSalary());
        employee.setAddress(request.getAddress());
        employee.setCity(request.getCity());
        employee.setState(request.getState());
        employee.setCountry(request.getCountry());
        employee.setPostalCode(request.getPostalCode());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEmergencyContactRelation(request.getEmergencyContactRelation());
        employee.setBankName(request.getBankName());
        employee.setBankAccountNumber(request.getBankAccountNumber());
        employee.setBankIfscCode(request.getBankIfscCode());
        employee.setSkills(request.getSkills());
        employee.setQualifications(request.getQualifications());
        employee.setCertifications(request.getCertifications());
        employee.setNotes(request.getNotes());
        employee.setProfileImageUrl(request.getProfileImageUrl());
        employee.setUpdatedBy(updatedBy);

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with ID: {}", updatedEmployee.getId());

        return EmployeeResponse.fromEntity(updatedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployeeByHR(Long id, EmployeeRequest request, String updatedBy) {
        log.info("HR updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        // Check if email is being changed and if it already exists
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Employee with email " + request.getEmail() + " already exists");
        }

        // HR can update ALL fields EXCEPT salary
        employee.setEmail(request.getEmail());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setMiddleName(request.getMiddleName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setAlternatePhone(request.getAlternatePhone());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        employee.setReportingManager(request.getReportingManager());
        // NOTE: Salary is NOT updated - only Accounts can update salary
        employee.setAddress(request.getAddress());
        employee.setCity(request.getCity());
        employee.setState(request.getState());
        employee.setCountry(request.getCountry());
        employee.setPostalCode(request.getPostalCode());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEmergencyContactRelation(request.getEmergencyContactRelation());
        employee.setBankName(request.getBankName());
        employee.setBankAccountNumber(request.getBankAccountNumber());
        employee.setBankIfscCode(request.getBankIfscCode());
        employee.setSkills(request.getSkills());
        employee.setQualifications(request.getQualifications());
        employee.setCertifications(request.getCertifications());
        employee.setNotes(request.getNotes());
        employee.setProfileImageUrl(request.getProfileImageUrl());
        employee.setUpdatedBy(updatedBy);

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated by HR successfully with ID: {}", updatedEmployee.getId());

        return EmployeeResponse.fromEntity(updatedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployeeSalary(Long id, Double newSalary, String updatedBy) {
        log.info("Accounts updating salary for employee ID: {} to {}", id, newSalary);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        
        employee.setSalary(java.math.BigDecimal.valueOf(newSalary));
        employee.setUpdatedBy(updatedBy);
        
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee salary updated successfully");
        
        return EmployeeResponse.fromEntity(updatedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        log.info("Fetching employee with employee ID: {}", employeeId);
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with employee ID: " + employeeId));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUserId(String userId) {
        log.info("Fetching employee with user ID: {}", userId);
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with user ID: " + userId));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(String department) {
        log.info("Fetching employees from department: {}", department);
        return employeeRepository.findByDepartment(department).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String keyword) {
        log.info("Searching employees with keyword: {}", keyword);
        return employeeRepository.searchEmployees(keyword).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEmployee(Long id, String deletedBy) {
        log.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        
        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with ID: {}", id);
    }

    @Transactional
    public EmployeeResponse updateEmployeeStatus(Long id, Employee.EmployeeStatus status, String updatedBy) {
        log.info("Updating status for employee ID: {} to {}", id, status);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        
        employee.setStatus(status);
        employee.setUpdatedBy(updatedBy);
        
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee status updated successfully");
        
        return EmployeeResponse.fromEntity(updatedEmployee);
    }
}
