import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

// Helper: Get auth header from sessionStorage
const authHeader = () => ({
  headers: {
    Authorization: `Bearer ${sessionStorage.getItem("token")}`,
  },
  withCredentials: true
});


// === AUTH (User & Admin) ===

// Register user or admin
export const registerUser = (userData) =>
  axios.post(`${API_BASE_URL}/users/register`, userData);

export const loginUser = (userData) =>
  axios.post(`${API_BASE_URL}/users/login`, userData);

export const verifyEmail = (token) =>
  axios.get(`${API_BASE_URL}/users/verify?token=${token}`);

export const forgotPassword = (email) =>
  axios.post(`${API_BASE_URL}/users/forgot-password`, { email });

export const resetPassword = (token, newPassword) =>
  axios.post(`${API_BASE_URL}/users/reset-password`, { token, newPassword });


// === TURF APIs ===

export const getAllTurfs = () =>
  axios.get(`${API_BASE_URL}/turfs`, authHeader());

export const addTurf = (data) =>
  axios.post(`${API_BASE_URL}/turfs`, data, authHeader());

export const updateTurf = (id, data) =>
  axios.put(`${API_BASE_URL}/turfs/${id}`, data, authHeader());

export const deleteTurf = (id) =>
  axios.delete(`${API_BASE_URL}/turfs/${id}`, authHeader());

export const getTurfById = (id) =>
  axios.get(`${API_BASE_URL}/turfs/${id}`, authHeader());


// === BOOKING APIs ===

export const createBooking = (bookingData) =>
  axios.post(`${API_BASE_URL}/bookings`, bookingData, authHeader());

export const getBookingsByTurfId = (turfId) =>
  axios.get(`${API_BASE_URL}/bookings/turf/${turfId}`, authHeader());

export const getBookingById = (id) =>
  axios.get(`${API_BASE_URL}/bookings/${id}`, authHeader());

export const getAllBookings = () =>
  axios.get(`${API_BASE_URL}/bookings/all`, authHeader());

export const deleteBooking = (id) =>
  axios.delete(`${API_BASE_URL}/bookings/${id}`, authHeader());

export const getBookingsByUserEmail = (email) =>
  axios.get(`${API_BASE_URL}/bookings/user/${email}`, authHeader());
