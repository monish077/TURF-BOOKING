import axiosInstance from "./axiosConfig";

// Helper to get Authorization header if token exists
const authHeader = () => {
  const token = sessionStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
};

// Get all turfs (adminEmail optional to filter admin turfs)
export const getAllTurfs = async (adminEmail) => {
  try {
    const url = adminEmail
      ? `/turfs/admin/${encodeURIComponent(adminEmail)}`
      : "/turfs/public"; // fallback to public turfs if no admin email

    const config = adminEmail
      ? { headers: authHeader() }
      : {}; // public turfs do not need auth

    const response = await axiosInstance.get(url, config);
    return response.data;
  } catch (error) {
    console.error("Error fetching turfs:", error);
    throw error;
  }
};

// Get turf by ID (requires auth)
export const getTurfById = async (id) => {
  try {
    const response = await axiosInstance.get(`/turfs/${id}`, {
      headers: authHeader(),
    });
    return response.data;
  } catch (error) {
    console.error(`Error fetching turf with id ${id}:`, error);
    throw error;
  }
};

// Add a new turf (ADMIN)
export const addTurf = async (data) => {
  try {
    const response = await axiosInstance.post("/turfs", data, {
      headers: authHeader(),
    });
    return response.data;
  } catch (error) {
    console.error("Error adding turf:", error);
    throw error;
  }
};

// Update existing turf (ADMIN)
export const updateTurf = async (id, data) => {
  try {
    const response = await axiosInstance.put(`/turfs/${id}`, data, {
      headers: authHeader(),
    });
    return response.data;
  } catch (error) {
    console.error(`Error updating turf with id ${id}:`, error);
    throw error;
  }
};

// Delete turf by ID (ADMIN)
export const deleteTurf = async (id) => {
  try {
    const response = await axiosInstance.delete(`/turfs/${id}`, {
      headers: authHeader(),
    });
    return response.data;
  } catch (error) {
    console.error(`Error deleting turf with id ${id}:`, error);
    throw error;
  }
};

// Upload images for a turf (ADMIN)
export const uploadImages = async (turfId, formData) => {
  try {
    const response = await axiosInstance.post(`/turfs/${turfId}/images`, formData, {
      headers: {
        ...authHeader(),
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error(`Error uploading images for turf ${turfId}:`, error);
    throw error;
  }
};
