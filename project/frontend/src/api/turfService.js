import axiosInstance from "./axiosConfig";

export const getAllTurfs = async () => {
  try {
    const response = await axiosInstance.get("/turfs");
    return response.data;
  } catch (error) {
    console.error("Error fetching turfs: ", error);
    throw error;
  }
};
