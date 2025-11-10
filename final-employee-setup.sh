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
SQLEOF

echo ""
echo "=== Creating Employee Profiles ==="

# Employee 2
echo "Creating profile for employee2 (Jane Smith)..."
TOKEN2=$(curl -s -X POST "$API_GATEWAY/auth/login" -H "Content-Type: application/json" -d '{"username": "employee2", "password": "password123"}' | jq -r '.token')
curl -s -X POST "$API_GATEWAY/employees" -H "Authorization: Bearer $TOKEN2" -H "Content-Type: application/json" -d '{"employeeId":"EMP0002","userId":"ca242aa8-7daa-49a9-a112-518ea0d6007e","email":"jane.employee@intellidesk.com","firstName":"Jane","lastName":"Smith","phoneNumber":"5550102000","dateOfBirth":"1992-02-15","gender":"FEMALE","department":"Marketing","designation":"Marketing Manager","joiningDate":"2024-01-15","employmentType":"FULL_TIME","status":"ACTIVE","reportingManager":"Sarah HR Manager","salary":75000,"address":"123 Main Street","city":"New York","state":"NY","country":"USA","postalCode":"10001","emergencyContactName":"Emergency Contact","emergencyContactPhone":"5559999999","emergencyContactRelation":"Family","bankName":"Chase Bank","bankAccountNumber":"1234567892","bankIfscCode":"CHASE001","skills":"Marketing, Communication","qualifications":"Bachelor Degree","certifications":"Marketing Pro"}' | jq -c '.id, .firstName, .lastName'

# Employee 3
echo "Creating profile for employee3 (Mike Johnson)..."
TOKEN3=$(curl -s -X POST "$API_GATEWAY/auth/login" -H "Content-Type: application/json" -d '{"username": "employee3", "password": "password123"}' | jq -r '.token')
curl -s -X POST "$API_GATEWAY/employees" -H "Authorization: Bearer $TOKEN3" -H "Content-Type: application/json" -d '{"employeeId":"EMP0003","userId":"2341b3ef-1dcb-4156-ad37-ae189fc75cf7","email":"mike.employee@intellidesk.com","firstName":"Mike","lastName":"Johnson","phoneNumber":"5550103000","dateOfBirth":"1993-03-15","gender":"MALE","department":"Sales","designation":"Sales Executive","joiningDate":"2024-01-15","employmentType":"FULL_TIME","status":"ACTIVE","reportingManager":"Sarah HR Manager","salary":65000,"address":"123 Main Street","city":"New York","state":"NY","country":"USA","postalCode":"10001","emergencyContactName":"Emergency Contact","emergencyContactPhone":"5559999999","emergencyContactRelation":"Family","bankName":"Chase Bank","bankAccountNumber":"1234567893","bankIfscCode":"CHASE001","skills":"Sales, Negotiation","qualifications":"Bachelor Degree","certifications":"Sales Pro"}' | jq -c '.id, .firstName, .lastName'

# Employee 4
echo "Creating profile for employee4 (Emily Brown)..."
TOKEN4=$(curl -s -X POST "$API_GATEWAY/auth/login" -H "Content-Type: application/json" -d '{"username": "employee4", "password": "password123"}' | jq -r '.token')
curl -s -X POST "$API_GATEWAY/employees" -H "Authorization: Bearer $TOKEN4" -H "Content-Type: application/json" -d '{"employeeId":"EMP0004","userId":"54fd4a0e-493e-421d-a926-dcbabab38503","email":"emily.employee@intellidesk.com","firstName":"Emily","lastName":"Brown","phoneNumber":"5550104000","dateOfBirth":"1994-04-15","gender":"FEMALE","department":"HR","designation":"HR Assistant","joiningDate":"2024-01-15","employmentType":"FULL_TIME","status":"ACTIVE","reportingManager":"Sarah HR Manager","salary":55000,"address":"123 Main Street","city":"New York","state":"NY","country":"USA","postalCode":"10001","emergencyContactName":"Emergency Contact","emergencyContactPhone":"5559999999","emergencyContactRelation":"Family","bankName":"Chase Bank","bankAccountNumber":"1234567894","bankIfscCode":"CHASE001","skills":"HR, Communication","qualifications":"Bachelor Degree","certifications":"HR Cert"}' | jq -c '.id, .firstName, .lastName'

# Employee 5
echo "Creating profile for employee5 (David Wilson)..."
TOKEN5=$(curl -s -X POST "$API_GATEWAY/auth/login" -H "Content-Type: application/json" -d '{"username": "employee5", "password": "password123"}' | jq -r '.token')
curl -s -X POST "$API_GATEWAY/employees" -H "Authorization: Bearer $TOKEN5" -H "Content-Type: application/json" -d '{"employeeId":"EMP0005","userId":"5ed2c48b-8f1d-44fa-b285-265a011f5197","email":"david.employee@intellidesk.com","firstName":"David","lastName":"Wilson","phoneNumber":"5550105000","dateOfBirth":"1995-05-15","gender":"MALE","department":"Finance","designation":"Financial Analyst","joiningDate":"2024-01-15","employmentType":"FULL_TIME","status":"ACTIVE","reportingManager":"Sarah HR Manager","salary":70000,"address":"123 Main Street","city":"New York","state":"NY","country":"USA","postalCode":"10001","emergencyContactName":"Emergency Contact","emergencyContactPhone":"5559999999","emergencyContactRelation":"Family","bankName":"Chase Bank","bankAccountNumber":"1234567895","bankIfscCode":"CHASE001","skills":"Finance, Analysis","qualifications":"Bachelor Degree","certifications":"CPA"}' | jq -c '.id, .firstName, .lastName'

# HR Sarah
echo "Creating HR profile for hr_sarah..."
HR_TOKEN=$(curl -s -X POST "$API_GATEWAY/auth/login" -H "Content-Type: application/json" -d '{"username": "hr_sarah", "password": "password123"}' | jq -r '.token')
curl -s -X POST "$API_GATEWAY/employees" -H "Authorization: Bearer $HR_TOKEN" -H "Content-Type: application/json" -d '{"employeeId":"HR0001","userId":"f058ee8a-02ab-4912-806c-73a49699a228","email":"sarah.hr@intellidesk.com","firstName":"Sarah","lastName":"Johnson","phoneNumber":"5551234567","dateOfBirth":"1985-05-20","gender":"FEMALE","department":"Human Resources","designation":"HR Manager","joiningDate":"2020-01-01","employmentType":"FULL_TIME","status":"ACTIVE","salary":95000,"address":"456 HR Avenue","city":"New York","state":"NY","country":"USA","postalCode":"10002","emergencyContactName":"John Johnson","emergencyContactPhone":"5559876543","emergencyContactRelation":"Spouse","bankName":"Bank of America","bankAccountNumber":"9876543210","bankIfscCode":"BOA001","skills":"HR Management, Recruitment","qualifications":"Masters in HR","certifications":"SHRM-CP"}' | jq -c '.id, .firstName, .lastName'

echo ""
echo "=== Final Verification ==="
curl -s -X GET "$API_GATEWAY/employees" -H "Authorization: Bearer $HR_TOKEN" | jq '.[] | {id, employeeId, fullName, department, designation}'

echo ""
echo "Setup complete! Total employees created:"
curl -s -X GET "$API_GATEWAY/employees" -H "Authorization: Bearer $HR_TOKEN" | jq '. | length'
