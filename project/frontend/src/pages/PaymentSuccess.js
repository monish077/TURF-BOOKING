import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const PaymentSuccess = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const timeout = setTimeout(() => {
      navigate("/slot"); // Redirect after 5 seconds
    }, 5000);

    return () => clearTimeout(timeout);
  }, [navigate]);

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>✅ Payment Successful!</h2>
        <p style={styles.text}>Check your email for booking details.</p>
        <p style={styles.text}>Redirecting to Slots page...</p>
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
};

export default PaymentSuccess;
