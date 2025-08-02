import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import "../assets/styles/turfdetails.css";

const TurfDetails = () => {
  const { id } = useParams();
  const [turf, setTurf] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTurf = async () => {
      try {
        const token = sessionStorage.getItem("token");

        const response = await axios.get(
          `http://localhost:8080/api/turfs/${id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setTurf(response.data);
      } catch (error) {
        console.error("Error fetching turf:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTurf();
  }, [id]);

  if (loading) {
    return (
      <div className="turf-details-page">
        <h2>Loading Turf Details...</h2>
      </div>
    );
  }

  if (!turf) {
    return (
      <div className="turf-details-page">
        <h2>Turf Not Found</h2>
        <Link to="/slot" className="back-link">← Back to Slots</Link>
      </div>
    );
  }

  return (
    <div className="turf-details-page">
      <Link to="/slot" className="back-link">← Back to Slots</Link>

      <div className="turf-card-details">
        {/* ✅ Image Carousel Section */}
        <div className="turf-image-carousel">
          {turf.imageUrls && turf.imageUrls.length > 0 ? (
            turf.imageUrls.map((url, idx) => (
              <img
                key={idx}
                src={url}
                alt={turf.name} // ✅ Removed redundant "Image" word
                className="turf-carousel-image"
              />
            ))
          ) : (
            <p>No images available for this turf.</p>
          )}
        </div>

        <div className="turf-card-info">
          <h2>{turf.name}</h2>
          <p><strong>Price:</strong> ₹{turf.pricePerHour} / hour</p>
          <p><strong>Location:</strong> {turf.location}</p>
          <p><strong>Description:</strong> {turf.description}</p>

          <h3>Available Slots:</h3>
          <ul>
            {turf.availableSlots?.split(",").map((slot, idx) => (
              <li key={idx}>{slot.trim()}</li>
            ))}
          </ul>

          <h3>Facilities:</h3>
          <ul>
            {turf.facilities?.split(",").map((facility, idx) => (
              <li key={idx}>{facility.trim()}</li>
            ))}
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
