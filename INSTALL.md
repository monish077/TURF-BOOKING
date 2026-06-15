# Local Installation Guide

Follow these steps to configure and run the Turf Booking Platform locally.

## Prerequisites
- **Node.js**: v18 or later
- **Java JDK**: 17 or 21
- **Maven**: 3.8+
- **MongoDB**: Local MongoDB instance or a MongoDB Atlas connection string

---

## 1. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd project/backend
   ```
2. Create a `.env` file from the environment specification:
   ```env
   MONGODB_URI=mongodb://localhost:27017/turfbooking
   MONGODB_DATABASE=turfbooking
   JWT_SECRET=your_very_long_secure_jwt_secret_key_at_least_512_bits
   JWT_EXPIRATION=86400000
   EMAIL_HOST=smtp.gmail.com
   EMAIL_PORT=587
   EMAIL_USERNAME=your-email@gmail.com
   EMAIL_PASSWORD=your-app-password
   PORT=8080
   ```
3. Build and compile the project using Maven:
   ```bash
   ./mvnw clean compile
   ```
4. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 2. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd project/frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the local development server:
   ```bash
   npm start
   ```
4. Open your browser and navigate to `http://localhost:3000`.
