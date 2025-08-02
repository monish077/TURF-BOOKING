import React, { useState, useEffect } from "react";
import { getPublicTurfs } from "../services/Api";
import "../assets/styles/slot.css";
import { Link } from "react-router-dom";

const Slot = () => {
  const [allTurfs, setAllTurfs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTurfs = async () => {
      try {
        const response = await getPublicTurfs();
        const backendTurfs = response.data;

        // ✅ Format turfs with proper image handling
        const turfsWithImages = backendTurfs.map((turf) => ({
          id: turf.id,
          name: turf.name,
          location: turf.location,
          price: turf.pricePerHour ? `₹${turf.pricePerHour}` : "N/A",
          image:
            turf.imageUrls && turf.imageUrls.length > 0
              ? turf.imageUrls[0]
              : "/default-turf.jpg", // fallback image if none uploaded
        }));

        setAllTurfs(turfsWithImages);
      } catch (error) {
        console.error("❌ Error fetching turfs:", error);
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
          <li><Link to="/">Home</Link></li>
          <li><Link to="/view-bookings">My Bookings</Link></li>
          <li><Link to="/contact">Contact</Link></li>
        </ul>
      </nav>

      {/* Header */}
      <div className="slots-header">
        <h2>🎯 Find Your Perfect Play!</h2>
        <p>Explore premium indoor/outdoor slots with seamless booking and excellent facilities.</p>
      </div>

      {/* Turf Grid */}
      <div className="turf-grid">
        {loading ? (
          <p className="loading-text">Loading turfs...</p>
        ) : allTurfs.length > 0 ? (
          allTurfs.map((turf) => (
            <div className="turf-card" key={turf.id}>
              <img
                src={turf.image}
                alt={`Thumbnail of ${turf.name}`}
                className="turf-image"
                onError={(e) => (e.target.src = "/default-turf.jpg")}
              />
              <h4>{turf.name}</h4>
              <p>{turf.price} / hour</p>
              <p>{turf.location}</p>
              <Link to={`/turfs/${turf.id}`}>
                <button className="book-now-btn">Book Now</button>
              </Link>
            </div>
          ))
        ) : (
          <p className="loading-text">No turfs available currently. Please check back later.</p>
        )}
      </div>
    </div>
  );
};

export default Slot;
