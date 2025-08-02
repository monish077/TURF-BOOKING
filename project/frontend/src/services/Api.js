import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

// 🔐 Auth header using JWT from sessionStorage
const authHeader = () => ({
  headers: {
    Authorization: `Bearer ${sessionStorage.getItem("token")}`,
  },
  withCredentials: true,
});


// ================== AUTH (User & Admin) ==================

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


// ================== TURF APIs ==================

// ✅ ADMIN: Fetch turfs owned by the logged-in admin
export const getAllTurfs = () =>
  axios.get(`${API_BASE_URL}/turfs/admin`, authHeader());

// ✅ PUBLIC: Fetch all turfs (used on Slot page)
export const getPublicTurfs = () =>
  axios.get(`${API_BASE_URL}/turfs/public`);

// ✅ Get single turf by ID
export const getTurfById = (id) =>
  axios.get(`${API_BASE_URL}/turfs/${id}`, authHeader());

// ✅ Add turf (admin only)
export const addTurf = (data) =>
  axios.post(`${API_BASE_URL}/turfs`, data, authHeader());

// ✅ Update turf (admin only)
export const updateTurf = (id, data) =>
  axios.put(`${API_BASE_URL}/turfs/${id}`, data, authHeader());

// ✅ Delete turf (admin only)
export const deleteTurf = (id) =>
  axios.delete(`${API_BASE_URL}/turfs/${id}`, authHeader());

export const uploadImages = (turfId, formData) =>
  axios.post(`${API_BASE_URL}/turfs/${turfId}/images`, formData, {
    headers: {
      Authorization: `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "multipart/form-data",
    },
    withCredentials: true,
  });



// ================== BOOKING APIs ==================

// ✅ Create a new booking
export const createBooking = (bookingData) =>
  axios.post(`${API_BASE_URL}/bookings`, bookingData, authHeader());

// ✅ Get all bookings (admin view)
export const getAllBookings = () =>
  axios.get(`${API_BASE_URL}/bookings/all`, authHeader());

// ✅ Get booking by ID
export const getBookingById = (id) =>
  axios.get(`${API_BASE_URL}/bookings/${id}`, authHeader());

// ✅ Get all bookings for a specific turf
export const getBookingsByTurfId = (turfId) =>
  axios.get(`${API_BASE_URL}/bookings/turf/${turfId}`, authHeader());

// ✅ Get bookings by user email
export const getBookingsByUserEmail = (email) =>
  axios.get(`${API_BASE_URL}/bookings/user/${email}`, authHeader());

// ✅ Admin bookings (server extracts from JWT)
export const getAdminBookings = () =>
  axios.get(`${API_BASE_URL}/bookings/admin`, authHeader());

// ✅ Delete a booking
export const deleteBooking = (id) =>
  axios.delete(`${API_BASE_URL}/bookings/${id}`, authHeader());

// ✅ Send booking confirmation email
export const sendBookingConfirmation = (bookingId) =>
  axios.get(`${API_BASE_URL}/bookings/send-confirmation/${bookingId}`, authHeader());
