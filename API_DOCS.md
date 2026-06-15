# REST API Documentation

Endpoints exposed by the Spring Boot backend.

## 🔑 Authentication

### Register a User
- **URL**: `/api/users/register`
- **Method**: `POST`
- **Payload**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```
- **Response** (201):
  ```json
  {
    "email": "john@example.com",
    "message": "Registration successful. Please verify your email."
  }
  ```

### Verify Email
- **URL**: `/api/users/verify`
- **Method**: `GET`
- **Params**: `token=<token>`
- **Response** (200):
  ```json
  {
    "status": "success",
    "message": "Email verified successfully!"
  }
  ```

### Login
- **URL**: `/api/users/login`
- **Method**: `POST`
- **Payload**:
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Response** (200):
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "role": "USER",
    "email": "john@example.com"
  }
  ```

---

## ⚽ Turfs

### Get All Public Turfs
- **URL**: `/api/turfs/public`
- **Method**: `GET`
- **Auth**: None

### Create Turf (Admin only)
- **URL**: `/api/turfs`
- **Method**: `POST`
- **Auth**: `Bearer <token>` (Admin Role)
