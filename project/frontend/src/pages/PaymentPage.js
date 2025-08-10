import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "../services/Api"; // ✅ Using your centralized Axios config

const PaymentPage = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    try {
      setLoading(true);

      // Simulate payment process delay
      setTimeout(async () => {
        await axiosInstance.get(`/bookings/send-confirmation/${bookingId}`, {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem("token")}`,
          },
          withCredentials: true,
        });

        navigate("/payment-success");
      }, 1500);
    } catch (error) {
      console.error("❌ Payment or email sending failed:", error);
      alert("Something went wrong during payment or email sending.");
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>💳 Payment Page</h2>
        <p style={styles.text}>Booking ID: {bookingId}</p>
        <p style={styles.text}>Proceed to complete your payment.</p>

        <button
          style={{
            ...styles.payButton,
            backgroundColor: loading ? "#aaa" : "#00d395",
            cursor: loading ? "not-allowed" : "pointer",
          }}
          onClick={handlePayment}
          disabled={loading}
        >
          {loading ? "Processing..." : "Pay Now"}
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
    padding: "20px",
  },
  card: {
    backgroundColor: "#1c3a2b",
    color: "#fff",
    padding: "40px 20px",
    borderRadius: "12px",
    textAlign: "center",
    boxShadow: "0px 0px 15px rgba(0,0,0,0.3)",
    width: "100%",
    maxWidth: "400px",
    animation: "fadeIn 0.4s ease-in-out",
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
    transition: "background-color 0.2s ease",
  },
};

// ✅ Keyframes for smooth fade-in
const styleSheet = document.styleSheets[0];
if (styleSheet) {
  styleSheet.insertRule(`
    @keyframes fadeIn {
      from { opacity: 0; transform: scale(0.95); }
      to { opacity: 1; transform: scale(1); }
    }
  `, styleSheet.cssRules.length);
}

export default PaymentPage;
