import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getBookingById } from "../services/Api";
import "../assets/styles/bookingsuccess.css";

const BookingSuccess = () => {
  const { id } = useParams();
  const [booking, setBooking] = useState(null);

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const response = await getBookingById(id);
        setBooking(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchBooking();
  }, [id]);

  return (
    <div className="success-page">
      <div className="success-card">
        <h2>✅ Booking Confirmed!</h2>
        <p>Thank you for booking with us.</p>

        {booking && (
          <div className="success-details">
            <p><b>Name:</b> {booking.name}</p>
            <p><b>Email:</b> {booking.userEmail}</p>
            <p><b>Date:</b> {booking.date}</p>
            <p><b>Time:</b> {booking.time}</p>
          </div>
        )}

        <Link to="/slots" className="go-home-btn">Go to Slots</Link>
      </div>
    </div>
  );
};

export default BookingSuccess;
