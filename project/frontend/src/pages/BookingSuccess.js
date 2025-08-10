import React, { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import { getBookingById } from "../services/Api";
import "../assets/styles/bookingsuccess.css";

const BookingSuccess = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [secondsLeft, setSecondsLeft] = useState(5);

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

  // Countdown + redirect
  useEffect(() => {
    const countdown = setInterval(() => {
      setSecondsLeft((prev) => prev - 1);
    }, 1000);

    const redirectTimer = setTimeout(() => {
      navigate("/slot");
    }, 5000);

    return () => {
      clearInterval(countdown);
      clearTimeout(redirectTimer);
    };
  }, [navigate]);

  if (loading) {
    return (
      <div className="success-page">
        <div className="success-card">
          <p>Loading booking details...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="success-page">
      <div className="success-card">
        <h2>✅ Booking Confirmed!</h2>
        <p>Thank you for booking with us.</p>

        {booking ? (
          <div className="success-details">
            <p>
              <b>Name:</b> {booking.userName}
            </p>
            <p>
              <b>Email:</b> {booking.userEmail}
            </p>
            <p>
              <b>Date:</b>{" "}
              {new Date(booking.date).toLocaleDateString("en-IN", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric",
              })}
            </p>
            <p>
              <b>Time:</b> {booking.slot}
            </p>
            <p>
              <b>Price:</b> ₹{booking.price}
            </p>
            <p>
              <b>Turf:</b> {booking.turfName}
            </p>
          </div>
        ) : (
          <p style={{ color: "red" }}>Booking not found.</p>
        )}

        <p style={{ marginTop: "15px", fontSize: "14px", opacity: 0.8 }}>
          Redirecting to slots page in <strong>{secondsLeft}</strong> seconds...
        </p>

        <Link to="/slot" className="go-home-btn">
          Go to Slots Now
        </Link>
      </div>
    </div>
  );
};

export default BookingSuccess;
