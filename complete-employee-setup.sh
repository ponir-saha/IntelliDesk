#!/bin/bash

API_GATEWAY="http://localhost:8080/api"

echo "=== Assigning ROLE_EMPLOYEE to all employee users ==="
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk << 'SQLEOF'
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

SELECT u.username, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username LIKE 'employee%' OR u.username = 'hr_sarah'
ORDER BY u.username, r.name;
SQLEOF

echo ""
echo "=== Creating Remaining Employee Profiles ==="

# Get all users and create profiles
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk -t -c "
SELECT u.id, u.username, u.email, u.first_name, u.last_name
FROM users u
WHERE u.username LIKE 'employee%'
  AND NOT EXISTS (SELECT 1 FROM employees e WHERE e.user_id = u.id)
ORDER BY u.username;
" | while IFS='|' read -r userId username email firstName lastName; do
    userId=$(echo "$userId" | xargs)
    username=$(echo "$username" | xargs)
    email=$(echo "$email" | xargs)
    firstName=$(echo "$firstName" | xargs)
    lastName=$(echo "$lastName" | xargs)
    
    if [ -n "$userId" ]; then
        empNum=$(echo "$username" | grep -o '[0-9]*')
        employeeId="EMP$(printf '%04d' $empNum)"
        
        case "$empNum" in
            2) dept="Marketing"; desig="Marketing Manager"; salary="75000"; gender="FEMALE" ;;
            3) dept="Sales"; desig="Sales Executive"; salary="65000"; gender="MALE" ;;
            4) dept="HR"; desig="HR Assistant"; salary="55000"; gender="FEMALE" ;;
            5) dept="Finance"; desig="Financial Analyst"; salary="70000"; gender="MALE" ;;
            *) dept="Engineering"; desig="Developer"; salary="60000"; gender="MALE" ;;
        esac
        
        echo "Creating employee profile for $username..."
        
        # Login as this user to get their token
        token=$(curl -s -X POST "$API_GATEWAY/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"username\": \"$username\", \"password\": \"password123\"}" | jq -r '.token')
        
        if [ -n "$token" ] && [ "$token" != "null" ]; then
            curl -s -X POST "$API_GATEWAY/employees" \
                -H "Authorization: Bearer $token" \
                -H "Content-Type: application/json" \
                -d "{
                    \"employeeId\": \"$employeeId\",
                    \"userId\": \"$userId\",
                    \"email\": \"$email\",
                    \"firstName\": \"$firstName\",
                    \"lastName\": \"$lastName\",
                    \"phoneNumber\": \"555010${empNum}000\",
                    \"dateOfBirth\": \"199${empNum}-0${empNum}-15\",
                    \"gender\": \"$gender\",
                    \"department\": \"$dept\",
                    \"designation\": \"$desig\",
                    \"joiningDate\": \"2024-01-15\",
                    \"employmentType\": \"FULL_TIME\",
                    \"status\": \"ACTIVE\",
                    \"reportingManager\": \"Sarah HR Manager\",
                    \"salary\": $salary,
                    \"address\": \"123 Main Street\",
                    \"city\": \"New York\",
                    \"state\": \"NY\",
                    \"country\": \"USA\",
                    \"postalCode\": \"10001\",
                    \"emergencyContactName\": \"Emergency Contact\",
                    \"emergencyContactPhone\": \"5559999999\",
                    \"emergencyContactRelation\": \"Family\",
                    \"bankName\": \"Chase Bank\",
                    \"bankAccountNumber\": \"123456789${empNum}\",
                    \"bankIfscCode\": \"CHASE001\",
                    \"skills\": \"Communication, Leadership\",
                    \"qualifications\": \"Bachelor Degree\",
                    \"certifications\": \"Professional Certification\"
                }" | jq '.'
        else
            echo "Failed to get token for $username"
        fi
        
        sleep 1
    fi
done

echo ""
echo "=== Creating HR Profile for hr_sarah ==="
HR_ID=$(docker exec intellidesk-postgres psql -U intellidesk -d intellidesk -t -c "SELECT id FROM users WHERE username='hr_sarah';")
HR_ID=$(echo "$HR_ID" | xargs)

if [ -n "$HR_ID" ]; then
    # Login as HR
    HR_TOKEN=$(curl -s -X POST "$API_GATEWAY/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username": "hr_sarah", "password": "password123"}' | jq -r '.token')
    
    if [ -n "$HR_TOKEN" ] && [ "$HR_TOKEN" != "null" ]; then
        curl -s -X POST "$API_GATEWAY/employees" \
            -H "Authorization: Bearer $HR_TOKEN" \
            -H "Content-Type: application/json" \
            -d "{
                \"employeeId\": \"HR0001\",
                \"userId\": \"$HR_ID\",
                \"email\": \"sarah.hr@intellidesk.com\",
                \"firstName\": \"Sarah\",
                \"lastName\": \"Johnson\",
                \"phoneNumber\": \"5551234567\",
                \"dateOfBirth\": \"1985-05-20\",
                \"gender\": \"FEMALE\",
                \"department\": \"Human Resources\",
                \"designation\": \"HR Manager\",
                \"joiningDate\": \"2020-01-01\",
                \"employmentType\": \"FULL_TIME\",
                \"status\": \"ACTIVE\",
                \"salary\": 95000,
                \"address\": \"456 HR Avenue\",
                \"city\": \"New York\",
                \"state\": \"NY\",
                \"country\": \"USA\",
                \"postalCode\": \"10002\",
                \"emergencyContactName\": \"John Johnson\",
                \"emergencyContactPhone\": \"5559876543\",
                \"emergencyContactRelation\": \"Spouse\",
                \"bankName\": \"Bank of America\",
                \"bankAccountNumber\": \"9876543210\",
                \"bankIfscCode\": \"BOA001\",
                \"skills\": \"HR Management, Recruitment, Employee Relations\",
                \"qualifications\": \"Masters in HR Management\",
                \"certifications\": \"SHRM-CP, PHR\"
            }" | jq '.'
    fi
fi

echo ""
echo "=== Final Verification ==="
echo "Total employee records:"
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk -t -c "SELECT COUNT(*) FROM employees;"

echo ""
echo "Employee list:"
docker exec intellidesk-postgres psql -U intellidesk -d intellidesk -c "
SELECT e.employee_id, e.first_name, e.last_name, e.department, e.designation
FROM employees e
ORDER BY e.employee_id;
"

echo ""
echo "Setup complete!"
