# Production Deployment Guide

This guide details steps to deploy the frontend to Vercel and the backend to Render.

---

## 1. Database (MongoDB Atlas)

1. Set up a free cluster on MongoDB Atlas.
2. Under **Network Access**, whitelist `0.0.0.0/30` or specific IP ranges of Render servers.
3. Under **Database Access**, create a user and retrieve the connection string (`mongodb+srv://...`).

---

## 2. Backend (Render)

1. Create a new **Web Service** on Render connected to your GitHub repository.
2. Set the root directory to `project/backend`.
3. Set the Runtime to **Docker** (it automatically uses the configured `Dockerfile`).
4. Define the Environment Variables:
   - `MONGODB_URI`
   - `MONGODB_DATABASE`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
   - `EMAIL_HOST`
   - `EMAIL_PORT`
   - `EMAIL_USERNAME`
   - `EMAIL_PASSWORD`
   - `PORT=8080`
   - `FRONTEND_URL=https://turf-booking-seven.vercel.app`

---

## 3. Frontend (Vercel)

1. Import your GitHub repository into Vercel.
2. Select `project/frontend` as the root directory.
3. Define the Build & Development Settings as a standard **Create React App**.
4. Set the Environment Variables:
   - `REACT_APP_API_URL=https://turf-booking-pp67.onrender.com/api`
5. Click **Deploy**.
