import axios from "axios";

const API_BASE_URL = "https://turf-booking-pp67.onrender.com/api";

// 🔐 Auth header using JWT token from sessionStorage
const authHeader = () => {
  const token = sessionStorage.getItem("token");
  return token
    ? { headers: { Authorization: `Bearer ${token}` }, withCredentials: true }
    : { withCredentials: true };
};

// ================== AUTH (User & Admin) ==================

// ✅ Register user/admin
export const registerUser = (userData) =>
  axios.post(`${API_BASE_URL}/users/register`, userData);

// ✅ Login user/admin
export const loginUser = (userData) =>
  axios.post(`${API_BASE_URL}/users/login`, userData);

// ✅ Verify email
export const verifyEmail = (token) =>
  axios.get(`${API_BASE_URL}/users/verify?token=${token}`);

// ✅ Forgot password - request reset link
export const forgotPassword = (email) =>
  axios.post(`${API_BASE_URL}/users/forgot-password`, { email });

// ✅ Reset password - with token from email
export const resetPassword = (token, newPassword) =>
  axios.post(`${API_BASE_URL}/users/reset-password`, { token, newPassword });

// ================== TURF APIs ==================

// ✅ Get all turfs added by current admin (optional email filter)
export const getAllTurfs = (adminEmail) => {
  const url = adminEmail
    ? `${API_BASE_URL}/turfs/admin/${encodeURIComponent(adminEmail)}`
    : `${API_BASE_URL}/turfs/admin`;
  return axios.get(url, authHeader());
};

// ✅ Get all public turfs (for users)
export const getPublicTurfs = () =>
  axios.get(`${API_BASE_URL}/turfs/public`);

// ✅ Get turf by ID
export const getTurfById = (id) =>
  axios.get(`${API_BASE_URL}/turfs/${id}`, authHeader());

// ✅ Add a new turf (ADMIN)
export const addTurf = (data) =>
  axios.post(`${API_BASE_URL}/turfs`, data, authHeader());

// ✅ Update existing turf (ADMIN)
export const updateTurf = (id, data) =>
  axios.put(`${API_BASE_URL}/turfs/${id}`, data, authHeader());

// ✅ Delete turf by ID (ADMIN)
export const deleteTurf = (id) =>
  axios.delete(`${API_BASE_URL}/turfs/${id}`, authHeader());

// ✅ Upload images for a turf
export const uploadImages = (turfId, formData) =>
  axios.post(`${API_BASE_URL}/turfs/${turfId}/images`, formData, {
    headers: {
      Authorization: `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "multipart/form-data",
    },
    withCredentials: true,
  });

// ================== BOOKING APIs ==================

// ✅ Create a new booking (USER)
export const createBooking = (bookingData) =>
  axios.post(`${API_BASE_URL}/bookings`, bookingData, authHeader());

// ✅ Get all bookings (ADMIN)
export const getAllBookings = () =>
  axios.get(`${API_BASE_URL}/bookings/all`, authHeader());

// ✅ Get booking by ID
export const getBookingById = (id) =>
  axios.get(`${API_BASE_URL}/bookings/${id}`, authHeader());

// ✅ Get bookings for a specific turf
export const getBookingsByTurfId = (turfId) =>
  axios.get(`${API_BASE_URL}/bookings/turf/${turfId}`, authHeader());

// ✅ Get bookings by user email (USER)
export const getBookingsByUserEmail = (email) =>
  axios.get(`${API_BASE_URL}/bookings/user/${email}`, authHeader());

// ✅ Get admin’s bookings using JWT (ADMIN)
export const getAdminBookings = () =>
  axios.get(`${API_BASE_URL}/bookings/admin`, authHeader());

// ✅ Delete a booking
export const deleteBooking = (id) =>
  axios.delete(`${API_BASE_URL}/bookings/${id}`, authHeader());

// ✅ Send booking confirmation (email or WhatsApp)
export const sendBookingConfirmation = (bookingId) =>
  axios.get(`${API_BASE_URL}/bookings/send-confirmation/${bookingId}`, authHeader());

// ================== DEFAULT AXIOS INSTANCE ==================
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

export default axiosInstance;
