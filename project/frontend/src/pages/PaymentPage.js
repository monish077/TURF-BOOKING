import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

const PaymentPage = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();

  const handlePayment = async () => {
    try {
      // Simulate payment process delay
      setTimeout(async () => {
        // ✅ Include token in request headers
        await axios.get(
          `http://localhost:8080/api/bookings/send-confirmation/${bookingId}`,
          {
            headers: {
              Authorization: `Bearer ${sessionStorage.getItem("token")}`,
            },
            withCredentials: true,
          }
        );

        // ✅ Redirect to success page
        navigate("/payment-success");
      }, 1500);
    } catch (error) {
      console.error("❌ Payment or email sending failed:", error);
      alert("Something went wrong during payment or email sending.");
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>💳 Payment Page</h2>
        <p style={styles.text}>Booking ID: {bookingId}</p>
        <p style={styles.text}>Proceed to complete your payment.</p>
        <button style={styles.payButton} onClick={handlePayment}>
          Pay Now
        </button>
      </div>
    </div>
  );
};

const styles = {
  container: {
    backgroundColor: "#0f291f",
    height: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "Arial, sans-serif",
  },
  card: {
    backgroundColor: "#1c3a2b",
    color: "#fff",
    padding: "40px",
    borderRadius: "12px",
    textAlign: "center",
    boxShadow: "0px 0px 15px rgba(0,0,0,0.3)",
    width: "90%",
    maxWidth: "400px",
  },
  title: {
    fontSize: "24px",
    marginBottom: "15px",
  },
  text: {
    fontSize: "16px",
    margin: "10px 0",
  },
  payButton: {
    backgroundColor: "#00d395",
    color: "#fff",
    border: "none",
    padding: "12px 25px",
    fontSize: "16px",
    borderRadius: "8px",
    cursor: "pointer",
    marginTop: "20px",
  },
};

export default PaymentPage;
