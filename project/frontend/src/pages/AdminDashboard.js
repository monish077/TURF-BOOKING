import React, { useState, useEffect, useCallback } from "react";
import { deleteTurf, updateTurf } from "../services/Api";
import axiosInstance from "../services/axiosInstance";
import "../assets/styles/admin.css";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  // State for list of turfs
  const [turfs, setTurfs] = useState([]);

  // State for form input fields
  const [newTurf, setNewTurf] = useState({
    name: "",
    location: "",
    pricePerHour: "",
    description: "",
    facilities: "",
    availableSlots: "",
  });

  // State to hold selected image files and their previews
  const [imageFiles, setImageFiles] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);

  // ID of turf currently being edited; null means add new turf
  const [editingTurfId, setEditingTurfId] = useState(null);

  // Loading indicator for form submission
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // Fetch all turfs for the logged-in admin
  const fetchTurfs = useCallback(async () => {
    try {
      const token = sessionStorage.getItem("token");
      const response = await axiosInstance.get("/turfs/admin", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTurfs(response.data || []);
    } catch (error) {
      console.error("Error fetching turfs:", error);
      alert("Failed to load turfs.");
    }
  }, []);

  // On mount, load turfs
  useEffect(() => {
    fetchTurfs();
  }, [fetchTurfs]);

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewTurf((prev) => ({ ...prev, [name]: value }));
  };

  // Handle image file selection and preview generation
  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageFiles(files);
    setImagePreviews(files.map((file) => URL.createObjectURL(file)));
  };

  // Reset form to initial state
  const resetForm = () => {
    setNewTurf({
      name: "",
      location: "",
      pricePerHour: "",
      description: "",
      facilities: "",
      availableSlots: "",
    });
    setImageFiles([]);
    setImagePreviews([]);
    setEditingTurfId(null);
  };

  // Form submission for adding or updating a turf
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const token = sessionStorage.getItem("token");

      if (editingTurfId) {
        // Update existing turf (without image change)
        await updateTurf(editingTurfId, newTurf);
        alert("✅ Turf updated successfully!");
      } else {
        // Add new turf with images
        const formData = new FormData();
        formData.append("name", newTurf.name);
        formData.append("location", newTurf.location);
        formData.append("price", newTurf.pricePerHour); // Use key 'price' for backend
        formData.append("description", newTurf.description);
        formData.append("facilities", newTurf.facilities);
        formData.append("availableSlots", newTurf.availableSlots);

        if (imageFiles.length > 0) {
          formData.append("image", imageFiles[0]); // First image as main image
          for (let i = 1; i < imageFiles.length; i++) {
            formData.append("images", imageFiles[i]); // Additional images
          }
        }

        await axiosInstance.post("/turfs/add-with-image", formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            // Content-Type will be set automatically to multipart/form-data
          },
        });

        alert("✅ Turf added successfully with images!");
      }

      resetForm();
      fetchTurfs();
    } catch (error) {
      // Improved error logging
      console.error("❌ Error saving turf:", error.response || error.message || error);
      alert(
        "Failed to save turf: " +
          (error.response?.data || error.message || "Unknown error")
      );
    } finally {
      setLoading(false);
    }
  };

  // Populate the form with turf data for editing
  const handleEdit = (turf) => {
    setNewTurf({
      name: turf.name || "",
      location: turf.location || "",
      pricePerHour: turf.pricePerHour || "",
      description: turf.description || "",
      facilities: turf.facilities || "",
      availableSlots: turf.availableSlots || "",
    });
    setEditingTurfId(turf.id);
    setImagePreviews(turf.imageUrls || []);
    setImageFiles([]);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  // Delete a turf by ID with confirmation
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this turf?")) return;

    try {
      await deleteTurf(id);
      alert("🗑 Turf deleted successfully!");
      fetchTurfs();
    } catch (error) {
      console.error("❌ Error deleting turf:", error);
      alert("Failed to delete turf.");
    }
  };

  return (
    <div className="admin-dashboard">
      <h2>{editingTurfId ? "✏ Edit Turf" : "🏟 Admin Dashboard"}</h2>

      <div className="btn-group" style={{ marginBottom: "20px" }}>
        <button onClick={() => navigate("/admin-bookings")} className="edit-btn">
          View Bookings
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          value={newTurf.name}
          onChange={handleInputChange}
          placeholder="Turf Name"
          required
        />
        <input
          type="text"
          name="location"
          value={newTurf.location}
          onChange={handleInputChange}
          placeholder="Location"
          required
        />
        <input
          type="number"
          name="pricePerHour"
          value={newTurf.pricePerHour}
          onChange={handleInputChange}
          placeholder="Price per Hour"
          required
        />
        <input
          type="text"
          name="description"
          value={newTurf.description}
          onChange={handleInputChange}
          placeholder="Description"
        />
        <input
          type="text"
          name="facilities"
          value={newTurf.facilities}
          onChange={handleInputChange}
          placeholder="Facilities (comma separated)"
        />
        <input
          type="text"
          name="availableSlots"
          value={newTurf.availableSlots}
          onChange={handleInputChange}
          placeholder="Available Slots (comma separated)"
        />

        <input
          type="file"
          multiple
          accept="image/*"
          onChange={handleImageChange}
          style={{ marginTop: "10px" }}
        />

        {imagePreviews.length > 0 && (
          <div className="image-preview-container">
            {imagePreviews.map((preview, idx) => (
              <img
                key={idx}
                src={preview}
                alt={`Turf Preview ${idx + 1}`}
                className="preview-img"
              />
            ))}
          </div>
        )}

        <button type="submit" disabled={loading} style={{ marginTop: "15px" }}>
          {loading ? "Saving..." : editingTurfId ? "Update Turf" : "Add Turf"}
        </button>

        {editingTurfId && (
          <button
            type="button"
            onClick={resetForm}
            className="cancel-btn"
            style={{ marginLeft: "10px" }}
          >
            Cancel Edit
          </button>
        )}
      </form>

      <div className="turf-list">
        <h3>Existing Turfs</h3>
        {turfs.length === 0 ? (
          <p>No turfs found for your account.</p>
        ) : (
          turfs.map((turf) => (
            <div className="turf-card" key={turf.id}>
              <div className="thumbnail-wrapper">
                {turf.imageUrls && turf.imageUrls.length > 0 ? (
                  <img src={turf.imageUrls[0]} alt={turf.name} />
                ) : (
                  <div className="no-img">No Image</div>
                )}
              </div>
              <h4>{turf.name}</h4>
              <p>{turf.location}</p>
              <p>₹{turf.pricePerHour} / hour</p>
              <div className="btn-group">
                <button className="edit-btn" onClick={() => handleEdit(turf)}>
                  Edit
                </button>
                <button className="delete-btn" onClick={() => handleDelete(turf.id)}>
                  Delete
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
