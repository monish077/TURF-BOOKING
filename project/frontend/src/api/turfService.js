import axiosInstance from "./axiosConfig";

// ✅ Optionally pass adminEmail to filter turfs for a specific admin
export const getAllTurfs = async (adminEmail) => {
  try {
    const url = adminEmail
      ? `/turfs/admin/${encodeURIComponent(adminEmail)}`
      : "/turfs"; // fallback to all turfs
    const response = await axiosInstance.get(url);
    return response.data;
  } catch (error) {
    console.error("Error fetching turfs: ", error);
    throw error;
  }
};
