import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(5); // 5-second countdown

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          navigate("/slot");
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [navigate]);

  return (
    <div style={styles.container}>
      <div style={{ ...styles.card, animation: "fadeIn 0.5s ease-in-out" }}>
        <h2 style={styles.title}>✅ Payment Successful!</h2>
        <p style={styles.text}>Check your email for booking details.</p>
        <p style={styles.text}>
          Redirecting to Slots page in <strong>{countdown}</strong> seconds...
        </p>
      </div>

      {/* Add animation styles inside a style tag */}
      <style>
        {`
          @keyframes fadeIn {
            from { opacity: 0; transform: scale(0.95); }
            to { opacity: 1; transform: scale(1); }
          }
        `}
      </style>
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
    fontFamily: "'Segoe UI', Arial, sans-serif",
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
  },
  title: {
    fontSize: "24px",
    marginBottom: "15px",
  },
  text: {
    fontSize: "16px",
    margin: "10px 0",
  },
};

export default PaymentSuccess;
