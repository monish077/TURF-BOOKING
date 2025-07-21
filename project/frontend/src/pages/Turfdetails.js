import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import "../assets/styles/turfdetails.css";

const TurfDetails = () => {
  const { id } = useParams();
  const [turf, setTurf] = useState(null);

  useEffect(() => {
    fetchTurf();
  }, []);

  const fetchTurf = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/turfs/${id}`);
      setTurf(response.data);
    } catch (error) {
      console.error("Error fetching turf:", error);
    }
  };

  if (!turf) {
    return (
      <div className="turf-details-page">
        <h2>Turf Not Found</h2>
        <Link to="/slots" className="back-link">← Back to Slots</Link>
      </div>
    );
  }

  return (
    <div className="turf-details-page">
      <Link to="/slots" className="back-link">← Back to Slots</Link>

      <div className="turf-card-details">
        <img src={turf.imageUrl} alt={turf.name} className="turf-card-image" />

        <div className="turf-card-info">
          <h2>{turf.name}</h2>
          <p><b>Price:</b> ₹{turf.pricePerHour} / hour</p>
          <p><b>Location:</b> {turf.location}</p>
          <p><b>Description:</b> A well-lit turf with lush synthetic grass, perfect for 5-a-side football matches and cricket nets.</p>

          <h3>Available Slots:</h3>
          <ul>
            <li>6:00 AM - 7:00 AM</li>
            <li>7:00 AM - 8:00 AM</li>
            <li>5:00 PM - 6:00 PM</li>
          </ul>

          <h3>Facilities:</h3>
          <ul>
            <li>Restroom</li>
            <li>Seating Area</li>
            <li>Cafeteria</li>
            <li>LED Lights</li>
          </ul>

          <Link to={`/book/${turf.id}`}>
            <button className="book-btn">Book This Turf</button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default TurfDetails;
