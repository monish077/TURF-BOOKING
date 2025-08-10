import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "https://turf-booking-pp67.onrender.com/api",
  withCredentials: true, // if you use cookies/session, else remove this
});

export default axiosInstance;
