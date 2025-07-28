import React, { useState, useEffect } from "react";
import { getAllTurfs } from "../services/Api";
import "../assets/styles/slot.css";
import { Link } from "react-router-dom";

const Slot = () => {
  const [allTurfs, setAllTurfs] = useState([]);

  useEffect(() => {
    const fetchTurfs = async () => {
      try {
        const response = await getAllTurfs();
        const backendTurfs = response.data;

        const turfsWithImages = backendTurfs.map((turf) => ({
          ...turf,
          image: turf.imageUrl || "/default-turf.jpg", // fallback if image missing
          price: `₹${turf.pricePerHour || 0}`,
        }));

        setAllTurfs(turfsWithImages);
      } catch (error) {
        console.error("❌ Error fetching turfs:", error);
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
          <li><Link to="/">Home</Link></li>
          <li><Link to="/view-bookings">My Bookings</Link></li>
          <li><Link to="/contact">Contact</Link></li>
        </ul>
      </nav>

      {/* Page Header */}
      <div className="slots-header">
        <h2>🎯 Find Your Perfect Play!</h2>
        <p>
          Explore premium indoor/outdoor slots with seamless booking and excellent facilities.
        </p>
      </div>

      {/* Turf Grid */}
      <div className="turf-grid">
        {allTurfs.length > 0 ? (
          allTurfs.map((turf) => (
            <div className="turf-card" key={turf.id}>
              <img src={turf.image} alt={turf.name} />
              <h4>{turf.name}</h4>
              <p>{turf.price} / hour</p>
              <p>{turf.location}</p>
              <Link to={`/turfs/${turf.id}`}>
                <button className="book-now-btn">Book Now</button>
              </Link>
            </div>
          ))
        ) : (
          <p className="loading-text">Loading turfs...</p>
        )}
      </div>
    </div>
  );
};

export default Slot;
