import axiosInstance from "./axiosConfig";

// 🔐 Auth header using JWT token from sessionStorage
const authHeader = () => {
  const token = sessionStorage.getItem("token");
  return token
    ? { headers: { Authorization: `Bearer ${token}` } }
    : {};
};

// ================== AUTH (User & Admin) ==================

// ✅ Register user/admin - USING axiosInstance
export const registerUser = (userData) =>
  axiosInstance.post('/users/register', userData);

// ✅ Login user/admin - USING axiosInstance
export const loginUser = (userData) =>
  axiosInstance.post('/users/login', userData);

// ✅ Verify email - USING axiosInstance
export const verifyEmail = (token) =>
  axiosInstance.get(`/users/verify?token=${token}`);

// ✅ Forgot password - USING axiosInstance
export const forgotPassword = (email) =>
  axiosInstance.post('/users/forgot-password', { email });

// ✅ Reset password - USING axiosInstance
export const resetPassword = (token, newPassword) =>
  axiosInstance.post('/users/reset-password', { token, newPassword });

// ================== TURF APIs ==================

// ✅ Get all turfs added by current admin - USING axiosInstance
export const getAllTurfs = (adminEmail) => {
  const url = adminEmail
    ? `/turfs/admin/${encodeURIComponent(adminEmail)}`
    : `/turfs/admin`;
  return axiosInstance.get(url, authHeader());
};

// ✅ Get all public turfs - USING axiosInstance
export const getPublicTurfs = () =>
  axiosInstance.get('/turfs/public');

// ✅ Get turf by ID - USING axiosInstance
export const getTurfById = (id) =>
  axiosInstance.get(`/turfs/${id}`, authHeader());

// ✅ Add a new turf - USING axiosInstance
export const addTurf = (data) =>
  axiosInstance.post('/turfs', data, authHeader());

// ✅ Update existing turf - USING axiosInstance
export const updateTurf = (id, data) =>
  axiosInstance.put(`/turfs/${id}`, data, authHeader());

// ✅ Delete turf by ID - USING axiosInstance
export const deleteTurf = (id) =>
  axiosInstance.delete(`/turfs/${id}`, authHeader());

// ✅ Upload images for a turf - USING axiosInstance
export const uploadImages = (turfId, formData) =>
  axiosInstance.post(`/turfs/${turfId}/images`, formData, {
    headers: {
      ...authHeader().headers,
      "Content-Type": "multipart/form-data",
    },
  });

// ================== BOOKING APIs ==================

// ✅ Create a new booking - USING axiosInstance
export const createBooking = (bookingData) =>
  axiosInstance.post('/bookings', bookingData, authHeader());

// ✅ Get all bookings - USING axiosInstance
export const getAllBookings = () =>
  axiosInstance.get('/bookings/all', authHeader());

// ✅ Get booking by ID - USING axiosInstance
export const getBookingById = (id) =>
  axiosInstance.get(`/bookings/${id}`, authHeader());

// ✅ Get bookings for a specific turf - USING axiosInstance
export const getBookingsByTurfId = (turfId) =>
  axiosInstance.get(`/bookings/turf/${turfId}`, authHeader());

// ✅ Get bookings by user email - USING axiosInstance
export const getBookingsByUserEmail = (email) =>
  axiosInstance.get(`/bookings/user/${email}`, authHeader());

// ✅ Get admin's bookings using JWT - USING axiosInstance
export const getAdminBookings = () =>
  axiosInstance.get('/bookings/admin', authHeader());

// ✅ Delete a booking - USING axiosInstance
export const deleteBooking = (id) =>
  axiosInstance.delete(`/bookings/${id}`, authHeader());

// ✅ Send booking confirmation - USING axiosInstance
export const sendBookingConfirmation = (bookingId) =>
  axiosInstance.get(`/bookings/send-confirmation/${bookingId}`, authHeader());

export default axiosInstance;