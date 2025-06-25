import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",  // your Spring Boot backend base URL
});

export default axiosInstance;
