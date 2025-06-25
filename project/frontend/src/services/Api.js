import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/";

// User APIs
export const loginUser = (userData) => axios.post(`${API_BASE_URL}users/login`, userData);
export const registerUser = (userData) => axios.post(`${API_BASE_URL}users/register`, userData);

// Admin APIs
export const registerAdmin = (adminData) => axios.post(`${API_BASE_URL}admin/register`, adminData);
export const loginAdmin = (adminData) => axios.post(`${API_BASE_URL}admin/login`, adminData);

// Turf APIs
export const getAllTurfs = () => axios.get(`${API_BASE_URL}turfs`);
export const addTurf = (data) => axios.post(`${API_BASE_URL}turfs`, data);
export const updateTurf = (id, data) => axios.put(`${API_BASE_URL}turfs/${id}`, data);
export const deleteTurf = (id) => axios.delete(`${API_BASE_URL}turfs/${id}`);
export const getTurfById = (id) => axios.get(`${API_BASE_URL}turfs/${id}`);

// Booking APIs
export const createBooking = (bookingData) => axios.post(`${API_BASE_URL}bookings`, bookingData);
export const getBookingsByTurfId = (turfId) => axios.get(`${API_BASE_URL}bookings/turf/${turfId}`);
export const getBookingById = (id) => axios.get(`${API_BASE_URL}bookings/${id}`);
export const getAllBookings = () => axios.get(`${API_BASE_URL}bookings/all`);
export const deleteBooking = (id) => axios.delete(`${API_BASE_URL}bookings/${id}`);
export const getBookingsByUserEmail = (email) => axios.get(`${API_BASE_URL}bookings/user/${email}`);