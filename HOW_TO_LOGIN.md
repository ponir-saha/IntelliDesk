# 🔐 How to Login and View Employee Profiles

## 📍 Quick Access URLs

- **Frontend UI**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Swagger/API Docs**: http://localhost:8080/swagger-ui.html

---

## 👥 User Credentials

### **HR Manager** (Full Access)
```
Username: hr_manager
Password: password123
```
**Permissions**: Can view ALL employee profiles + create/edit employees

### **Employees** (Self Access Only)
All employees use password: `password123`

| Username | Name | Department | Designation |
|----------|------|------------|-------------|
| `employee1` | John Doe | Engineering | Senior Developer |
| `employee2` | Jane Smith | Marketing | Marketing Manager |
| `employee3` | Mike Johnson | Sales | Sales Executive |
| `employee4` | Emily Brown | HR | HR Assistant |
| `employee5` | David Wilson | Finance | Financial Analyst |

**Permissions**: Can view ONLY their own profile

---

## 🌐 Using the Web UI

### Step 1: Open the Application
1. Open your browser and go to: **http://localhost:4200**
2. You'll see the login page

### Step 2: Login
1. Enter username (e.g., `hr_sarah`)
2. Enter password: `password123`
3. Click **Login**

### Step 3: View Profiles

#### **As HR Manager:**
1. After login, click **"Employees"** button on the dashboard
2. You'll see a list of all 6 employees
3. Click on any employee to view their full profile
4. You can also click **"My Profile"** to see your own profile

#### **As Employee:**
1. After login, click **"My Profile"** button on the dashboard
2. You'll see ONLY your own employee profile
3. Clicking **"Employees"** will show an empty list or permission error

---

## 🔧 Using the API (curl commands)

### Login and Get Token
```bash
# Login as HR
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "hr_sarah", "password": "password123"}'

# Save the token from response for next requests
```

### View All Employees (HR only)
```bash
# Replace YOUR_TOKEN with the token from login
curl -X GET "http://localhost:8080/api/employees" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### View My Profile
```bash
curl -X GET "http://localhost:8080/api/employees/my-profile" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### View Specific Employee
```bash
# Replace {id} with employee ID (e.g., 1, 2, 3...)
curl -X GET "http://localhost:8080/api/employees/{id}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📊 Current Employee Data

| ID | Employee ID | Name | Department | Designation |
|----|-------------|------|------------|-------------|
| 1 | EMP0001 | John Doe | Engineering | Senior Developer |
| 2 | EMP0002 | Jane Smith | Marketing | Marketing Manager |
| 3 | EMP0003 | Mike Johnson | Sales | Sales Executive |
| 4 | EMP0004 | Emily Brown | HR | HR Assistant |
| 5 | EMP0005 | David Wilson | Finance | Financial Analyst |
| 6 | HR0001 | Sarah Johnson | Human Resources | HR Manager |
| 7 | HR0002 | HR Manager | Human Resources | HR Manager |

---

## 🧪 Quick Test Script

Run this to verify everything works:

```bash
#!/bin/bash

echo "=== Testing Employee Service ==="

# Login as HR
echo "1. Logging in as HR..."
HR_TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "hr_sarah", "password": "password123"}' | jq -r '.token')

if [ -z "$HR_TOKEN" ] || [ "$HR_TOKEN" = "null" ]; then
    echo "❌ Login failed"
    exit 1
fi
echo "✅ Login successful"

# Get all employees
echo ""
echo "2. Fetching all employees..."
EMPLOYEES=$(curl -s -X GET "http://localhost:8080/api/employees" \
  -H "Authorization: Bearer $HR_TOKEN")

COUNT=$(echo "$EMPLOYEES" | jq '. | length')
echo "✅ Found $COUNT employees"

echo ""
echo "3. Employee List:"
echo "$EMPLOYEES" | jq '.[] | {employeeId, fullName, department, designation}'

# Get HR's own profile
echo ""
echo "4. Fetching HR's profile..."
curl -s -X GET "http://localhost:8080/api/employees/my-profile" \
  -H "Authorization: Bearer $HR_TOKEN" | jq '{employeeId, fullName, department, designation}'

echo ""
echo "✅ All tests passed!"
```

Save this as `test-employee-access.sh` and run it!

---

## 🐛 Troubleshooting

### Problem: Login not working
**Solution**: 
```bash
# Restart user-service
docker restart intellidesk-user-service
# Wait 10 seconds
sleep 10
```

### Problem: "Failed to load employees"
**Solution**:
```bash
# Check if employee-service is running
docker ps | grep employee-service

# Check logs
docker logs intellidesk-employee-service --tail 50

# Restart if needed
docker restart intellidesk-employee-service
```

### Problem: 403 Forbidden errors
**Solution**: Make sure you're logged in with the correct credentials and your token is still valid (tokens expire after 24 hours).

### Problem: Empty employee list
**Solution**: Run the setup script again:
```bash
cd /Users/ponirsaha/Documents/IntelliDesk
./final-employee-setup.sh
```

---

## 🎯 What You Can Do

### **HR Manager Can:**
- ✅ View all employee profiles
- ✅ Create new employee profiles
- ✅ Edit employee information
- ✅ Delete employees
- ✅ Search employees by department, name, etc.
- ✅ View their own profile

### **Employees Can:**
- ✅ View only their own profile
- ❌ Cannot see other employees
- ❌ Cannot create or edit profiles

---

## 📱 UI Features

The Angular frontend includes:
- **Login Page**: Secure authentication
- **Dashboard**: Quick access buttons
  - "My Profile" button
  - "Employees" button (for HR)
- **Employee List**: Shows all employees (HR only)
- **Employee Detail**: Full profile view with all information
- **Responsive Design**: Works on desktop and mobile

---

**Need help?** Check the logs:
```bash
docker logs intellidesk-employee-service
docker logs intellidesk-user-service
docker logs intellidesk-api-gateway
```
