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
          image: turf.imageUrl, 
          price: `₹${turf.pricePerHour}`,
        }));

        setAllTurfs(turfsWithImages);
      } catch (error) {
        console.error("Error fetching turfs from backend:", error);
      }
    };

    fetchTurfs();
  }, []);

  return (
    <div className="slots-page">
      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
        <ul className="nav-links">
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <Link to="/view-bookings">My Booking Details</Link>
          </li>
          <li>
            <a href="/contact">Contact</a>
          </li>
        </ul>
        <button className="book-btn">Book Now</button>
      </nav>

      <div className="slots-header">
        <h2>🎯 Find Your Perfect Play!</h2>
        <p>
          Explore premium indoor/outdoor slots with seamless booking and excellent facilities.
        </p>
      </div>

      <div className="turf-grid">
        {allTurfs.map((turf, index) => (
          <div className="turf-card" key={index}>
            <img src={turf.image} alt={turf.name} />
            <h4>{turf.name}</h4>
            <p>{turf.price} / hour</p>
            <p>{turf.location}</p>
            <Link to={`/turfs/${turf.id}`}>
              <button className="book-now-btn">Book Now</button>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Slot;
