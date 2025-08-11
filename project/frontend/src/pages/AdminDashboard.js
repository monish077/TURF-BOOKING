import React, { useState, useEffect, useCallback } from "react";
import {
  getAllTurfs,
  deleteTurf,
  updateTurf,
} from "../services/Api";
import axiosInstance from "../services/axiosInstance";
import "../assets/styles/admin.css";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  const [turfs, setTurfs] = useState([]);
  const [newTurf, setNewTurf] = useState({
    name: "",
    location: "",
    pricePerHour: "",
    description: "",
    facilities: "",
    availableSlots: "",
  });
  const [imageFiles, setImageFiles] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [editingTurfId, setEditingTurfId] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // Fetch admin's turfs
  const fetchTurfs = useCallback(async () => {
    try {
      const token = sessionStorage.getItem("token");
      const res = await axiosInstance.get("/turfs/admin", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTurfs(res.data || []);
    } catch (err) {
      console.error("Error fetching turfs:", err);
      alert("Failed to load turfs.");
    }
  }, []);

  useEffect(() => {
    fetchTurfs();
  }, [fetchTurfs]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewTurf((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageFiles(files);
    setImagePreviews(files.map((file) => URL.createObjectURL(file)));
  };

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

  // UPDATED handleSubmit
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const token = sessionStorage.getItem("token");

      if (editingTurfId) {
        await updateTurf(editingTurfId, newTurf);
        alert("✅ Turf updated successfully!");
      } else {
        const formData = new FormData();
        formData.append("name", newTurf.name);
        formData.append("location", newTurf.location);
        formData.append("price", newTurf.pricePerHour);
        formData.append("description", newTurf.description);
        formData.append("facilities", newTurf.facilities);
        formData.append("availableSlots", newTurf.availableSlots);

        if (imageFiles.length > 0) {
          // First image as "image"
          formData.append("image", imageFiles[0]);

          // Rest images as "images"
          for (let i = 1; i < imageFiles.length; i++) {
            formData.append("images", imageFiles[i]);
          }
        }

        await axiosInstance.post("/turfs/add-with-image", formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            // No Content-Type header here
          },
        });

        alert("✅ Turf added successfully with images!");
      }

      resetForm();
      fetchTurfs();
    } catch (err) {
      console.error("❌ Error saving turf:", err);
      alert("Failed to save turf: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  // rest of your component (handleDelete, handleEdit, JSX) unchanged

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

        <input type="file" multiple accept="image/*" onChange={handleImageChange} />

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

        <button type="submit" disabled={loading}>
          {loading ? "Saving..." : editingTurfId ? "Update Turf" : "Add Turf"}
        </button>
        {editingTurfId && (
          <button type="button" onClick={resetForm} className="cancel-btn">
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
