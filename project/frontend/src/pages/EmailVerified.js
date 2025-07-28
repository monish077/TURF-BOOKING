import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const EmailVerified = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/login");
    }, 5000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        <h2 style={styles.title}>✅ Email Verified!</h2>
        <p style={styles.message}>Your email has been successfully verified.</p>
        <p style={styles.redirect}>Redirecting to login page...</p>
      </div>
    </div>
  );
};

const styles = {
  wrapper: {
    height: "100vh",
    backgroundColor: "#0f291f",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "20px",
  },
  card: {
    background: "rgba(255, 255, 255, 0.05)",
    border: "1px solid rgba(255, 255, 255, 0.2)",
    padding: "40px 30px",
    borderRadius: "20px",
    textAlign: "center",
    backdropFilter: "blur(10px)",
    boxShadow: "0 8px 20px rgba(0, 0, 0, 0.2)",
    color: "#fff",
    maxWidth: "400px",
    width: "100%",
  },
  title: {
    fontSize: "26px",
    marginBottom: "12px",
    fontWeight: "bold",
  },
  message: {
    fontSize: "16px",
    marginBottom: "8px",
  },
  redirect: {
    fontSize: "14px",
    opacity: 0.8,
  },
};

export default EmailVerified;
