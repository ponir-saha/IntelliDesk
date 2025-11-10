#!/bin/bash

# Script to fix roles and create employee records

BASE_URL="http://localhost:8080"
API_GATEWAY="$BASE_URL/api"

echo "=========================================="
echo "IntelliDesk - Fix Roles & Create Employees"
echo "=========================================="
echo ""

# First, let's login as HR to get the token
echo "Logging in as HR..."
HR_RESPONSE=$(curl -s -X POST "$API_GATEWAY/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"hr_sarah","password":"HrPass@123"}')

HR_TOKEN=$(echo $HR_RESPONSE | jq -r '.token')
HR_USER_ID=$(echo $HR_RESPONSE | jq -r '.user.id')

if [ -z "$HR_TOKEN" ] || [ "$HR_TOKEN" = "null" ]; then
    echo "ERROR: Failed to login as HR"
    exit 1
fi

echo "HR Token obtained: ${HR_TOKEN:0:50}..."
echo "HR User ID: $HR_USER_ID"
echo ""

# Get other user IDs by logging in
echo "Getting user IDs..."

get_user_id() {
    local username=$1
    local password=$2
    response=$(curl -s -X POST "$API_GATEWAY/auth/login" \
      -H "Content-Type: application/json" \
      -d "{\"username\":\"$username\",\"password\":\"$password\"}")
    echo $response | jq -r '.user.id'
}

JOHN_ID=$(get_user_id "john_smith" "EmpPass@123")
EMMA_ID=$(get_user_id "emma_davis" "EmpPass@123")
MICHAEL_ID=$(get_user_id "michael_brown" "EmpPass@123")
SOPHIA_ID=$(get_user_id "sophia_wilson" "EmpPass@123")
JAMES_ID=$(get_user_id "james_taylor" "EmpPass@123")

echo "User IDs retrieved:"
echo "  hr_sarah: $HR_USER_ID"
echo "  john_smith: $JOHN_ID"
echo "  emma_davis: $EMMA_ID"
echo "  michael_brown: $MICHAEL_ID"
echo "  sophia_wilson: $SOPHIA_ID"
echo "  james_taylor: $JAMES_ID"
echo ""

# Function to create employee record
create_employee() {
    local empId=$1
    local userId=$2
    local email=$3
    local firstName=$4
    local lastName=$5
    local department=$6
    local designation=$7
    local salary=$8
    
    echo "Creating employee: $empId - $firstName $lastName"
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$API_GATEWAY/employees" \
        -H "Authorization: Bearer $HR_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
            \"employeeId\": \"$empId\",
            \"userId\": \"$userId\",
            \"email\": \"$email\",
            \"firstName\": \"$firstName\",
            \"lastName\": \"$lastName\",
            \"phoneNumber\": \"+1-555-0100\",
            \"dateOfBirth\": \"1990-01-15\",
            \"gender\": \"MALE\",
            \"department\": \"$department\",
            \"designation\": \"$designation\",
            \"joiningDate\": \"2024-01-15\",
            \"employmentType\": \"FULL_TIME\",
            \"status\": \"ACTIVE\",
            \"reportingManager\": \"Sarah Johnson\",
            \"salary\": $salary,
            \"address\": \"123 Main Street, New York, NY 10001\",
            \"emergencyContactName\": \"Emergency Contact\",
            \"emergencyContactPhone\": \"+1-555-9999\",
            \"emergencyContactRelation\": \"Family\",
            \"bankName\": \"Chase Bank\",
            \"bankAccountNumber\": \"****1234\",
            \"bankIfscCode\": \"CHASE001\",
            \"skills\": \"Communication, Leadership, Problem Solving\",
            \"qualifications\": \"Bachelor's Degree\",
            \"certifications\": \"Professional Certification\"
        }")
    
    http_status=$(echo "$response" | grep "HTTP_STATUS" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS/d')
    
    if [ "$http_status" = "201" ] || [ "$http_status" = "200" ]; then
        echo "✓ Employee created successfully"
    else
        echo "✗ Failed to create employee (HTTP $http_status)"
        echo "Response: $body"
    fi
    echo ""
}

echo "=========================================="
echo "Creating Employee Records"
echo "=========================================="
echo ""

create_employee "EMP001" "$HR_USER_ID" "sarah.hr@intellidesk.com" "Sarah" "Johnson" "Human Resources" "HR Manager" 95000
sleep 1

create_employee "EMP002" "$JOHN_ID" "john.smith@intellidesk.com" "John" "Smith" "Engineering" "Senior Software Engineer" 120000
sleep 1

create_employee "EMP003" "$EMMA_ID" "emma.davis@intellidesk.com" "Emma" "Davis" "Marketing" "Marketing Manager" 85000
sleep 1

create_employee "EMP004" "$MICHAEL_ID" "michael.brown@intellidesk.com" "Michael" "Brown" "Sales" "Sales Executive" 75000
sleep 1

create_employee "EMP005" "$SOPHIA_ID" "sophia.wilson@intellidesk.com" "Sophia" "Wilson" "Engineering" "Software Engineer" 95000
sleep 1

create_employee "EMP006" "$JAMES_ID" "james.taylor@intellidesk.com" "James" "Taylor" "Operations" "Operations Coordinator" 70000

echo ""
echo "=========================================="
echo "Testing Employee Access"
echo "=========================================="
echo ""

# Test HR access
echo "Testing HR access (should see all employees)..."
hr_list=$(curl -s -X GET "$API_GATEWAY/employees" \
    -H "Authorization: Bearer $HR_TOKEN")
employee_count=$(echo $hr_list | jq '. | length' 2>/dev/null || echo "error")

if [ "$employee_count" != "error" ] && [ "$employee_count" != "null" ]; then
    echo "✓ HR can see $employee_count employees"
else
    echo "✗ HR cannot access employee list"
    echo "Response: $hr_list"
fi
echo ""

# Test employee access to their own profile
echo "Testing employee access (john_smith viewing own profile)..."
JOHN_TOKEN=$(curl -s -X POST "$API_GATEWAY/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"john_smith","password":"EmpPass@123"}' | jq -r '.token')

john_profile=$(curl -s -X GET "$API_GATEWAY/employees/my-profile" \
    -H "Authorization: Bearer $JOHN_TOKEN")
    
profile_name=$(echo $john_profile | jq -r '.firstName' 2>/dev/null)

if [ "$profile_name" = "John" ]; then
    echo "✓ John can see his own profile"
    has_salary=$(echo $john_profile | jq 'has("salary")' 2>/dev/null)
    if [ "$has_salary" = "false" ]; then
        echo "✓ Salary is hidden from employee view (correct)"
    else
        echo "⚠ Salary is visible to employee (check backend logic)"
    fi
else
    echo "✗ John cannot access his profile"
    echo "Response: $john_profile"
fi
echo ""

echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "You can now test:"
echo "1. Login as HR (hr_sarah / HrPass@123)"
echo "   - Go to http://localhost:4200/employees"
echo "   - Should see all 6 employees"
echo ""
echo "2. Login as Employee (john_smith / EmpPass@123)"
echo "   - Go to http://localhost:4200/employees/my-profile"
echo "   - Should see only own profile (no salary)"
echo ""
