import React, { useState, useEffect, useCallback } from "react";
import { deleteTurf, updateTurf } from "../services/Api";
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

  const fetchTurfs = useCallback(async () => {
    try {
      const token = sessionStorage.getItem("token");
      const response = await axiosInstance.get("/turfs/admin", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTurfs(response.data || []);
    } catch (error) {
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
          formData.append("image", imageFiles[0]);
          for (let i = 1; i < imageFiles.length; i++) {
            formData.append("images", imageFiles[i]);
          }
        }

        await axiosInstance.post("/turfs/add-with-image", formData, {
          headers: { Authorization: `Bearer ${token}` },
        });

        alert("✅ Turf added successfully with images!");
      }

      resetForm();
      fetchTurfs();
    } catch (error) {
      alert(
        "Failed to save turf: " +
          (error.response?.data || error.message || "Unknown error")
      );
    } finally {
      setLoading(false);
    }
  };

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

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this turf?")) return;

    try {
      await deleteTurf(id);
      alert("🗑 Turf deleted successfully!");
      fetchTurfs();
    } catch (error) {
      alert("Failed to delete turf.");
    }
  };

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/");
  };

  return (
    <div className="admin-dashboard">
      {/* Topbar */}
      <div className="admin-topbar">
        <div className="admin-brand">MARS ARENA</div>
        <span className="admin-badge">Admin Panel</span>
        <button
          onClick={handleLogout}
          style={{
            background: "none",
            border: "1px solid rgba(255,255,255,0.15)",
            color: "rgba(240,244,248,0.75)",
            padding: "7px 16px",
            borderRadius: "8px",
            cursor: "pointer",
            fontSize: "13px",
            fontFamily: "inherit",
            transition: "all 0.2s",
          }}
          onMouseEnter={(e) => {
            e.target.style.borderColor = "rgba(255,80,80,0.4)";
            e.target.style.color = "#ff6b6b";
          }}
          onMouseLeave={(e) => {
            e.target.style.borderColor = "rgba(255,255,255,0.15)";
            e.target.style.color = "rgba(240,244,248,0.75)";
          }}
        >
          Logout
        </button>
      </div>

      <div className="admin-content">
        <h2>{editingTurfId ? "✏️ Edit Turf" : "🏟️ Admin Dashboard"}</h2>
        <p className="admin-sub">
          {editingTurfId
            ? "Update the selected turf's information below."
            : "Add new turfs or manage existing ones."}
        </p>

        {/* View Bookings Button */}
        <div className="btn-group" style={{ marginBottom: "28px" }}>
          <button
            onClick={() => navigate("/admin-bookings")}
            className="edit-btn"
          >
            📋 View All Bookings
          </button>
        </div>

        {/* Add / Edit Form */}
        <div className="admin-section">
          <div className="admin-section-title">
            <span />
            {editingTurfId ? "Update Turf Details" : "Add New Turf"}
          </div>

          <form onSubmit={handleSubmit}>
            <input
              type="text"
              name="name"
              value={newTurf.name}
              onChange={handleInputChange}
              placeholder="Turf Name *"
              required
            />
            <input
              type="text"
              name="location"
              value={newTurf.location}
              onChange={handleInputChange}
              placeholder="Location *"
              required
            />
            <input
              type="number"
              name="pricePerHour"
              value={newTurf.pricePerHour}
              onChange={handleInputChange}
              placeholder="Price per Hour (₹) *"
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
              placeholder="Facilities (comma separated, e.g. Floodlights, Parking)"
            />
            <input
              type="text"
              name="availableSlots"
              value={newTurf.availableSlots}
              onChange={handleInputChange}
              placeholder="Available Slots (comma separated, e.g. 6AM-8AM, 8AM-10AM)"
            />

            <input
              type="file"
              multiple
              accept="image/*"
              onChange={handleImageChange}
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

            <div className="btn-group" style={{ marginTop: "8px" }}>
              <button type="submit" disabled={loading}>
                {loading ? "Saving…" : editingTurfId ? "Update Turf" : "Add Turf"}
              </button>

              {editingTurfId && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="cancel-btn"
                >
                  Cancel Edit
                </button>
              )}
            </div>
          </form>
        </div>

        {/* Existing Turfs List */}
        <div className="admin-section">
          <div className="admin-section-title">
            <span />
            Existing Turfs ({turfs.length})
          </div>

          <div className="turf-list">
            {turfs.length === 0 ? (
              <p style={{ color: "#4a5568", fontSize: "14px" }}>
                No turfs found for your account. Add one above!
              </p>
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

                  <div className="turf-card-info">
                    <h4>{turf.name}</h4>
                    <p>📍 {turf.location}</p>
                    <p>₹{turf.pricePerHour} / hour</p>
                  </div>

                  <div className="btn-group">
                    <button
                      className="edit-btn"
                      onClick={() => handleEdit(turf)}
                    >
                      Edit
                    </button>
                    <button
                      className="delete-btn"
                      onClick={() => handleDelete(turf.id)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
