import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "https://turf-booking-pp67.onrender.com/api",
  withCredentials: true, // remove if not using cookies
});

export default axiosInstance;
