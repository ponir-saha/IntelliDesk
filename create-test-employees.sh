#!/bin/bash

API_GATEWAY="http://localhost:8080/api"

echo "=== Creating 5 Test Employee Users ==="

# Array of employee data
declare -a EMPLOYEES=(
    "employee1:John:Doe:john.employee@intellidesk.com:Engineering:Senior Developer"
    "employee2:Jane:Smith:jane.employee@intellidesk.com:Marketing:Marketing Manager"
    "employee3:Mike:Johnson:mike.employee@intellidesk.com:Sales:Sales Executive"
    "employee4:Emily:Brown:emily.employee@intellidesk.com:HR:HR Assistant"
    "employee5:David:Wilson:david.employee@intellidesk.com:Finance:Financial Analyst"
)

# Create each employee user
for emp in "${EMPLOYEES[@]}"; do
    IFS=':' read -r username firstName lastName email department designation <<< "$emp"
    
    echo ""
    echo "Creating user: $username ($firstName $lastName)"
    
    response=$(curl -s -X POST "$API_GATEWAY/auth/register" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$username\",
            \"email\": \"$email\",
            \"password\": \"password123\",
            \"firstName\": \"$firstName\",
            \"lastName\": \"$lastName\"
        }")
    
    echo "$response" | jq '.' 2>/dev/null || echo "$response"
done

echo ""
echo "=== Assigning ROLE_EMPLOYEE to all employee users ==="

# Update roles in database
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk << 'SQLEOF'
-- Add ROLE_EMPLOYEE to all employee users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username LIKE 'employee%'
  AND r.name = 'ROLE_EMPLOYEE'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Verify roles
SELECT u.username, u.email, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username LIKE 'employee%' OR u.username = 'hr_sarah'
ORDER BY u.username, r.name;
SQLEOF

echo ""
echo "=== Getting User IDs and Creating Employee Profiles ==="

# Login as HR
echo "Logging in as HR..."
HR_TOKEN=$(curl -s -X POST "$API_GATEWAY/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "hr_sarah",
        "password": "password123"
    }' | jq -r '.token')

if [ -z "$HR_TOKEN" ] || [ "$HR_TOKEN" = "null" ]; then
    echo "Failed to get HR token"
    exit 1
fi

echo "HR token obtained successfully"

# Get all employee user IDs and create profiles
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk -t -c "
SELECT id, username, email, first_name, last_name
FROM users
WHERE username LIKE 'employee%'
ORDER BY username;
" | while IFS='|' read -r userId username email firstName lastName; do
    # Trim whitespace
    userId=$(echo "$userId" | xargs)
    username=$(echo "$username" | xargs)
    email=$(echo "$email" | xargs)
    firstName=$(echo "$firstName" | xargs)
    lastName=$(echo "$lastName" | xargs)
    
    if [ -n "$userId" ]; then
        echo ""
        echo "Creating employee profile for $username ($userId)"
        
        # Generate employee number
        empNum=$(echo "$username" | grep -o '[0-9]*')
        employeeId="EMP$(printf '%04d' $empNum)"
        
        # Determine department and designation based on username
        case "$empNum" in
            1)
                dept="Engineering"
                desig="Senior Developer"
                salary="85000"
                ;;
            2)
                dept="Marketing"
                desig="Marketing Manager"
                salary="75000"
                ;;
            3)
                dept="Sales"
                desig="Sales Executive"
                salary="65000"
                ;;
            4)
                dept="HR"
                desig="HR Assistant"
                salary="55000"
                ;;
            5)
                dept="Finance"
                desig="Financial Analyst"
                salary="70000"
                ;;
        esac
        
        curl -s -X POST "$API_GATEWAY/employees" \
            -H "Authorization: Bearer $HR_TOKEN" \
            -H "Content-Type: application/json" \
            -d "{
                \"employeeId\": \"$employeeId\",
                \"userId\": \"$userId\",
                \"email\": \"$email\",
                \"firstName\": \"$firstName\",
                \"lastName\": \"$lastName\",
                \"phoneNumber\": \"+1-555-0${empNum}00\",
                \"dateOfBirth\": \"1990-0${empNum}-15\",
                \"gender\": \"MALE\",
                \"department\": \"$dept\",
                \"designation\": \"$desig\",
                \"joiningDate\": \"2024-01-15\",
                \"employmentType\": \"FULL_TIME\",
                \"status\": \"ACTIVE\",
                \"reportingManager\": \"Sarah HR Manager\",
                \"salary\": $salary,
                \"address\": \"123 Main Street, New York, NY 10001\",
                \"city\": \"New York\",
                \"state\": \"NY\",
                \"country\": \"USA\",
                \"postalCode\": \"10001\",
                \"emergencyContactName\": \"Emergency Contact\",
                \"emergencyContactPhone\": \"+1-555-9999\",
                \"emergencyContactRelation\": \"Family\",
                \"bankName\": \"Chase Bank\",
                \"bankAccountNumber\": \"****${empNum}000\",
                \"bankIfscCode\": \"CHASE001\",
                \"skills\": \"Communication, Leadership, Problem Solving\",
                \"qualifications\": \"Bachelor's Degree\",
                \"certifications\": \"Professional Certification\"
            }" | jq '.' 2>/dev/null || echo "Employee profile created"
    fi
done

echo ""
echo "=== Verification ==="
echo "Fetching all employees..."
curl -s -X GET "$API_GATEWAY/employees" \
    -H "Authorization: Bearer $HR_TOKEN" | jq '.' 2>/dev/null || echo "Unable to format response"

echo ""
echo "Setup complete!"
