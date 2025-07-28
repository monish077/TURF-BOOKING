import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getBookingById } from "../services/Api";
import "../assets/styles/bookingsuccess.css";

const BookingSuccess = () => {
  const { id } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const response = await getBookingById(id);
        setBooking(response.data);
      } catch (error) {
        console.error("Failed to fetch booking:", error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchBooking();
    }
  }, [id]);

  if (loading) {
    return <div className="success-page"><p>Loading booking details...</p></div>;
  }

  return (
    <div className="success-page">
      <div className="success-card">
        <h2>✅ Booking Confirmed!</h2>
        <p>Thank you for booking with us.</p>

        {booking ? (
          <div className="success-details">
            <p><b>Name:</b> {booking.userName}</p>
            <p><b>Email:</b> {booking.userEmail}</p>
            <p><b>Date:</b> {booking.date}</p>
            <p><b>Time:</b> {booking.slot}</p>
            <p><b>Price:</b> ₹{booking.price}</p>
            <p><b>Turf:</b> {booking.turfName}</p>
          </div>
        ) : (
          <p style={{ color: "red" }}>Booking not found.</p>
        )}

        <Link to="/slot" className="go-home-btn">Go to Slots</Link>
      </div>
    </div>
  );
};

export default BookingSuccess;
