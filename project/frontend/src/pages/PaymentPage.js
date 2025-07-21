import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import "../assets/styles/payment.css";
import { getBookingById } from "../services/Api";

const PaymentPage = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("UPI");

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const res = await getBookingById(bookingId);
        setBooking(res.data);
      } catch (err) {
        console.error("Failed to fetch booking details ❌", err);
      }
    };

    fetchBooking();
  }, [bookingId]);

  const handlePayment = () => {
    alert("Payment Successful ✅");
    navigate("/success");
  };

  if (!booking) {
    return <div className="payment-page">Loading booking details...</div>;
  }

  return (
    <div className="payment-page">
      <Link to="/turfs" className="back-link">← Back to Turfs</Link>
      <div className="payment-card">
        <h2>Payment Details</h2>
        <p><strong>Name:</strong> {booking.userName}</p>
        <p><strong>Email:</strong> {booking.userEmail}</p>
        <p><strong>Date:</strong> {booking.date}</p>
        <p><strong>Time Slot:</strong> {booking.slot}</p>
        <p><strong>Amount:</strong> ₹{booking.price}</p>

        <label>Select Payment Method:</label>
        <select
          value={paymentMethod}
          onChange={(e) => setPaymentMethod(e.target.value)}
        >
          <option value="UPI">UPI</option>
          <option value="Card">Credit / Debit Card</option>
          <option value="Cash">Cash On Arrival</option>
        </select>

        <button className="pay-now-btn" onClick={handlePayment}>
          Pay Now
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;
