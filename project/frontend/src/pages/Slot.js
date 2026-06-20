import React, { useState, useEffect } from "react";
import { getPublicTurfs } from "../services/Api";
import { Link, useNavigate } from "react-router-dom";
import "../assets/styles/slot.css";
import "../assets/styles/landing.css";

const Slot = () => {
  const [allTurfs, setAllTurfs] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/");
  };

  useEffect(() => {
    const fetchTurfs = async () => {
      try {
        const response = await getPublicTurfs();
        const backendTurfs = response.data || [];

        const turfsWithImages = backendTurfs.map((turf) => ({
          id: turf.id,
          name: turf.name || "Unnamed Turf",
          location: turf.location || "Location not available",
          price: turf.pricePerHour ? `₹${turf.pricePerHour}` : "N/A",
          rawPrice: turf.pricePerHour,
          image:
            turf.imageUrls?.[0] && turf.imageUrls[0].trim() !== ""
              ? turf.imageUrls[0]
              : "/default-turf.jpg",
        }));

        setAllTurfs(turfsWithImages);
      } catch (error) {
        // Error handled silently
      } finally {
        setLoading(false);
      }
    };

    fetchTurfs();
  }, []);

  return (
    <div className="slots-page">
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
        <ul className="nav-links">
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/view-bookings">My Bookings</Link>
          </li>
          <li>
            <button
              onClick={handleLogout}
              style={{
                background: "none",
                border: "1px solid rgba(255,255,255,0.15)",
                color: "rgba(240,244,248,0.75)",
                padding: "6px 14px",
                borderRadius: "8px",
                cursor: "pointer",
                fontSize: "14px",
                fontFamily: "inherit",
                transition: "all 0.2s",
              }}
              onMouseEnter={(e) => {
                e.target.style.borderColor = "rgba(0,255,157,0.4)";
                e.target.style.color = "#00ff9d";
              }}
              onMouseLeave={(e) => {
                e.target.style.borderColor = "rgba(255,255,255,0.15)";
                e.target.style.color = "rgba(240,244,248,0.75)";
              }}
            >
              Logout
            </button>
          </li>
        </ul>
      </nav>

      {/* Header */}
      <div className="slots-header">
        <h2>
          Find Your <span>Perfect Arena</span>
        </h2>
        <p>
          Explore premium indoor &amp; outdoor turfs with seamless booking and
          top-notch facilities. Pick a turf, pick a time, play your game.
        </p>
      </div>

      {/* Turf Grid */}
      <div className="turf-grid">
        {loading ? (
          <div className="loading-text">
            <div className="loading-spinner" />
            <p>Loading arenas…</p>
          </div>
        ) : allTurfs.length > 0 ? (
          allTurfs.map((turf) => (
            <div className="turf-card" key={turf.id}>
              <img
                src={turf.image}
                alt={turf.name}
                className="turf-image"
                onError={(e) => {
                  e.target.src = "/default-turf.jpg";
                }}
              />
              <div className="turf-card-body">
                <h4>{turf.name}</h4>
                <div className="turf-card-meta">📍 {turf.location}</div>
                <div className="turf-card-price">
                  {turf.price} <span>/ hour</span>
                </div>
                <Link to={`/turfs/${turf.id}`}>
                  <button className="book-now-btn">Book Now →</button>
                </Link>
              </div>
            </div>
          ))
        ) : (
          <div className="loading-text">
            <div style={{ fontSize: "48px", marginBottom: "16px" }}>🏟️</div>
            <p>No turfs available right now. Please check back later.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Slot;
