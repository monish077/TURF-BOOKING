import axios from "axios";

const API_BASE_URL = "https://turf-booking-pp67.onrender.com/api";

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // needed if your backend uses cookies (e.g., sessions)
});

export default axiosInstance;
