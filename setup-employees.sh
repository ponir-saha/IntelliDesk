#!/bin/bash

# Setup script to create HR and Employee users/profiles for IntelliDesk

BASE_URL="http://localhost:8080"
API_GATEWAY="$BASE_URL/api"

echo "=========================================="
echo "IntelliDesk Employee Setup Script"
echo "=========================================="
echo ""

# Function to register a user
register_user() {
    local username=$1
    local email=$2
    local password=$3
    local firstName=$4
    local lastName=$5
    local role=$6
    
    echo "Creating user: $username ($role)"
    
    response=$(curl -s -X POST "$API_GATEWAY/auth/register" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$username\",
            \"email\": \"$email\",
            \"password\": \"$password\",
            \"firstName\": \"$firstName\",
            \"lastName\": \"$lastName\",
            \"role\": \"$role\"
        }")
    
    echo "Response: $response"
    echo ""
}

# Function to login and get token
login_user() {
    local username=$1
    local password=$2
    
    echo "Logging in as: $username"
    
    response=$(curl -s -X POST "$API_GATEWAY/auth/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$username\",
            \"password\": \"$password\"
        }")
    
    token=$(echo $response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token obtained: ${token:0:50}..."
    echo "$token"
}

# Function to create employee record
create_employee() {
    local token=$1
    local employeeId=$2
    local userId=$3
    local email=$4
    local firstName=$5
    local lastName=$6
    local department=$7
    local designation=$8
    local salary=$9
    
    echo "Creating employee record: $employeeId"
    
    curl -s -X POST "$API_GATEWAY/employees" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "{
            \"employeeId\": \"$employeeId\",
            \"userId\": $userId,
            \"email\": \"$email\",
            \"firstName\": \"$firstName\",
            \"lastName\": \"$lastName\",
            \"phoneNumber\": \"+1-555-0${userId}00\",
            \"dateOfBirth\": \"1990-01-15\",
            \"gender\": \"MALE\",
            \"department\": \"$department\",
            \"designation\": \"$designation\",
            \"joiningDate\": \"2024-01-15\",
            \"employmentType\": \"FULL_TIME\",
            \"status\": \"ACTIVE\",
            \"reportingManager\": \"Sarah HR Manager\",
            \"salary\": $salary,
            \"address\": \"123 Main Street, New York, NY 10001\",
            \"emergencyContactName\": \"Emergency Contact\",
            \"emergencyContactPhone\": \"+1-555-9999\",
            \"emergencyContactRelation\": \"Family\",
            \"bankName\": \"Chase Bank\",
            \"bankAccountNumber\": \"****${userId}000\",
            \"bankIfscCode\": \"CHASE001\",
            \"skills\": \"Communication, Leadership, Problem Solving\",
            \"qualifications\": \"Bachelor's Degree\",
            \"certifications\": \"Professional Certification\"
        }" | jq '.' 2>/dev/null || echo "Employee created (jq not available for pretty print)"
    
    echo ""
}

echo "=========================================="
echo "Step 1: Creating HR User"
echo "=========================================="
echo ""

register_user "hr_sarah" "sarah.hr@intellidesk.com" "HrPass@123" "Sarah" "Johnson" "HR"

sleep 2

echo "=========================================="
echo "Step 2: Creating Employee Users"
echo "=========================================="
echo ""

register_user "john_smith" "john.smith@intellidesk.com" "EmpPass@123" "John" "Smith" "EMPLOYEE"
sleep 1
register_user "emma_davis" "emma.davis@intellidesk.com" "EmpPass@123" "Emma" "Davis" "EMPLOYEE"
sleep 1
register_user "michael_brown" "michael.brown@intellidesk.com" "EmpPass@123" "Michael" "Brown" "EMPLOYEE"
sleep 1
register_user "sophia_wilson" "sophia.wilson@intellidesk.com" "EmpPass@123" "Sophia" "Wilson" "EMPLOYEE"
sleep 1
register_user "james_taylor" "james.taylor@intellidesk.com" "EmpPass@123" "James" "Taylor" "EMPLOYEE"

sleep 2

echo "=========================================="
echo "Step 3: Logging in as HR to create employee records"
echo "=========================================="
echo ""

HR_TOKEN=$(login_user "hr_sarah" "HrPass@123")
echo ""

if [ -z "$HR_TOKEN" ]; then
    echo "ERROR: Failed to login as HR. Exiting."
    exit 1
fi

sleep 2

echo "=========================================="
echo "Step 4: Creating Employee Records"
echo "=========================================="
echo ""

# Assuming user IDs will be sequential starting from the HR user
# You may need to adjust these IDs based on actual user creation

create_employee "$HR_TOKEN" "EMP001" 1 "sarah.hr@intellidesk.com" "Sarah" "Johnson" "Human Resources" "HR Manager" 95000
sleep 1

create_employee "$HR_TOKEN" "EMP002" 2 "john.smith@intellidesk.com" "John" "Smith" "Engineering" "Senior Software Engineer" 120000
sleep 1

create_employee "$HR_TOKEN" "EMP003" 3 "emma.davis@intellidesk.com" "Emma" "Davis" "Marketing" "Marketing Manager" 85000
sleep 1

create_employee "$HR_TOKEN" "EMP004" 4 "michael.brown@intellidesk.com" "Michael" "Brown" "Sales" "Sales Executive" 75000
sleep 1

create_employee "$HR_TOKEN" "EMP005" 5 "sophia.wilson@intellidesk.com" "Sophia" "Wilson" "Engineering" "Software Engineer" 95000
sleep 1

create_employee "$HR_TOKEN" "EMP006" 6 "james.taylor@intellidesk.com" "James" "Taylor" "Operations" "Operations Coordinator" 70000

echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "Credentials:"
echo "----------------------------------------"
echo "HR User:"
echo "  Username: hr_sarah"
echo "  Password: HrPass@123"
echo "  Email: sarah.hr@intellidesk.com"
echo ""
echo "Employee Users:"
echo "  1. john_smith / EmpPass@123"
echo "  2. emma_davis / EmpPass@123"
echo "  3. michael_brown / EmpPass@123"
echo "  4. sophia_wilson / EmpPass@123"
echo "  5. james_taylor / EmpPass@123"
echo "----------------------------------------"
echo ""
echo "You can now:"
echo "  - Login as HR to view/manage all employees"
echo "  - Login as any employee to view their own profile"
echo ""
