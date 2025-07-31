import React, { useState, useEffect } from "react";
import { addTurf, getAllTurfs, deleteTurf, updateTurf } from "../services/Api";
import "../assets/styles/admin.css";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  const [turfs, setTurfs] = useState([]);
  const [newTurf, setNewTurf] = useState({
    name: "",
    location: "",
    pricePerHour: "",
    imageUrl: "",
    description: "",
    facilities: "",
    availableSlots: "",
  });
  const [editingTurfId, setEditingTurfId] = useState(null);

  const navigate = useNavigate();
  const adminEmail = sessionStorage.getItem("email"); // 👈 Get admin's email

  const fetchTurfs = async () => {
    try {
      const res = await getAllTurfs(adminEmail); // 👈 Filter by email
      setTurfs(res.data);
    } catch (err) {
      console.error("Error fetching turfs:", err);
    }
  };

  useEffect(() => {
    fetchTurfs();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewTurf({ ...newTurf, [name]: value });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onloadend = () => {
      setNewTurf({ ...newTurf, imageUrl: reader.result });
    };
    if (file) reader.readAsDataURL(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingTurfId) {
        await updateTurf(editingTurfId, newTurf);
        alert("Turf updated successfully!");
      } else {
        const turfWithOwner = { ...newTurf, ownerEmail: adminEmail }; // 👈 Add ownerEmail
        await addTurf(turfWithOwner);
        alert("Turf added successfully!");
      }

      setNewTurf({
        name: "",
        location: "",
        pricePerHour: "",
        imageUrl: "",
        description: "",
        facilities: "",
        availableSlots: "",
      });
      setEditingTurfId(null);
      fetchTurfs();
    } catch (err) {
      console.error("Error saving turf:", err);
      alert("Failed to save turf.");
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
      imageUrl: turf.imageUrl,
      description: turf.description,
      facilities: turf.facilities,
      availableSlots: turf.availableSlots,
    });
    setEditingTurfId(turf.id);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="admin-dashboard">
      <h2>Admin Dashboard</h2>

      {/* View Bookings button */}
      <div className="btn-group" style={{ marginBottom: "20px" }}>
        <button onClick={() => navigate("/admin-bookings")} className="edit-btn">
          View Bookings
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        <input type="text" name="name" value={newTurf.name} onChange={handleInputChange} placeholder="Turf Name" required />
        <input type="text" name="location" value={newTurf.location} onChange={handleInputChange} placeholder="Location" required />
        <input type="number" name="pricePerHour" value={newTurf.pricePerHour} onChange={handleInputChange} placeholder="Price per Hour" required />
        <input type="file" accept="image/*" onChange={handleImageChange} />
        <input type="text" name="description" value={newTurf.description} onChange={handleInputChange} placeholder="Description" />
        <input type="text" name="facilities" value={newTurf.facilities} onChange={handleInputChange} placeholder="Facilities (comma separated)" />
        <input type="text" name="availableSlots" value={newTurf.availableSlots} onChange={handleInputChange} placeholder="Available Slots (comma separated)" />
        <button type="submit">{editingTurfId ? "Update Turf" : "Add Turf"}</button>
      </form>

      <div className="turf-list">
        <h3>Existing Turfs</h3>
        {turfs.length === 0 ? (
          <p>No turfs found for your account.</p>
        ) : (
          turfs.map((turf) => (
            <div className="turf-card" key={turf.id}>
              <img src={turf.imageUrl} alt={turf.name} />
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
