package com.intellidesk.employee.dto;

import com.intellidesk.employee.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String employeeId;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String phoneNumber;
    private String alternatePhone;
    private LocalDate dateOfBirth;
    private Employee.Gender gender;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private Employee.EmploymentType employmentType;
    private Employee.EmployeeStatus status;
    private String reportingManager;
    private BigDecimal salary;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private String bankName;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String skills;
    private String qualifications;
    private String certifications;
    private String notes;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static EmployeeResponse fromEntity(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .userId(employee.getUserId())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .fullName(employee.getFirstName() + 
                         (employee.getMiddleName() != null ? " " + employee.getMiddleName() : "") + 
                         " " + employee.getLastName())
                .phoneNumber(employee.getPhoneNumber())
                .alternatePhone(employee.getAlternatePhone())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .employmentType(employee.getEmploymentType())
                .status(employee.getStatus())
                .reportingManager(employee.getReportingManager())
                .salary(employee.getSalary())
                .address(employee.getAddress())
                .city(employee.getCity())
                .state(employee.getState())
                .country(employee.getCountry())
                .postalCode(employee.getPostalCode())
                .emergencyContactName(employee.getEmergencyContactName())
                .emergencyContactPhone(employee.getEmergencyContactPhone())
                .emergencyContactRelation(employee.getEmergencyContactRelation())
                .bankName(employee.getBankName())
                .bankAccountNumber(employee.getBankAccountNumber())
                .bankIfscCode(employee.getBankIfscCode())
                .skills(employee.getSkills())
                .qualifications(employee.getQualifications())
                .certifications(employee.getCertifications())
                .notes(employee.getNotes())
                .profileImageUrl(employee.getProfileImageUrl())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .updatedBy(employee.getUpdatedBy())
                .build();
    }

    public static EmployeeResponse fromEntityLimited(Employee employee) {
        // Limited view for employees (hides sensitive information)
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .fullName(employee.getFirstName() + 
                         (employee.getMiddleName() != null ? " " + employee.getMiddleName() : "") + 
                         " " + employee.getLastName())
                .phoneNumber(employee.getPhoneNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .employmentType(employee.getEmploymentType())
                .status(employee.getStatus())
                .reportingManager(employee.getReportingManager())
                .address(employee.getAddress())
                .city(employee.getCity())
                .state(employee.getState())
                .country(employee.getCountry())
                .emergencyContactName(employee.getEmergencyContactName())
                .emergencyContactPhone(employee.getEmergencyContactPhone())
                .emergencyContactRelation(employee.getEmergencyContactRelation())
                .skills(employee.getSkills())
                .qualifications(employee.getQualifications())
                .certifications(employee.getCertifications())
                .profileImageUrl(employee.getProfileImageUrl())
                .build();
    }
}
