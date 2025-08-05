import React, { useState, useEffect, useCallback } from "react";
import {
  getAllTurfs,
  deleteTurf,
  updateTurf,
  uploadImages,
} from "../services/Api";
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

  const navigate = useNavigate();
  const adminEmail = sessionStorage.getItem("email");

  const fetchTurfs = useCallback(async () => {
    try {
      const res = await getAllTurfs(adminEmail);
      setTurfs(res.data);
    } catch (err) {
      console.error("Error fetching turfs:", err);
    }
  }, [adminEmail]);

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
    const previews = files.map((file) => URL.createObjectURL(file));
    setImagePreviews(previews);
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
    try {
      const token = sessionStorage.getItem("token");

      if (editingTurfId) {
        await updateTurf(editingTurfId, newTurf);
        alert("Turf updated successfully!");
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
        }

        const response = await fetch("http://localhost:8080/api/turfs/add-with-image", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: formData,
        });

        if (!response.ok) {
          const errText = await response.text();
          throw new Error("Failed to add turf: " + errText);
        }

        const createdTurf = await response.json();
        const createdTurfId = createdTurf?.id;

        // Upload remaining images (if any)
        if (imageFiles.length > 1) {
          const moreImagesForm = new FormData();
          imageFiles.slice(1).forEach((file) => moreImagesForm.append("images", file));
          await uploadImages(createdTurfId, moreImagesForm);
        }

        alert("Turf added successfully with images!");
      }

      resetForm();
      fetchTurfs();
    } catch (err) {
      console.error("Error saving turf:", err);
      alert("Failed to save turf: " + err.message);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this turf?")) {
      try {
        await deleteTurf(id);
        fetchTurfs();
      } catch (err) {
        console.error("Error deleting turf:", err);
      }
    }
  };

  const handleEdit = (turf) => {
    setNewTurf({
      name: turf.name,
      location: turf.location,
      pricePerHour: turf.pricePerHour,
      description: turf.description,
      facilities: turf.facilities,
      availableSlots: turf.availableSlots,
    });
    setEditingTurfId(turf.id);
    setImagePreviews(turf.imageUrls || []);
    setImageFiles([]);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="admin-dashboard">
      <h2>Admin Dashboard</h2>

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

        <button type="submit">
          {editingTurfId ? "Update Turf" : "Add Turf"}
        </button>
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
