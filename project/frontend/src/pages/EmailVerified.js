import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const EmailVerified = () => {
  const navigate = useNavigate();
  const [secondsLeft, setSecondsLeft] = useState(5);

  useEffect(() => {
    // Countdown timer
    const countdown = setInterval(() => {
      setSecondsLeft((prev) => prev - 1);
    }, 1000);

    // Redirect after 5 seconds
    const redirectTimer = setTimeout(() => {
      navigate("/login");
    }, 5000);

    return () => {
      clearInterval(countdown);
      clearTimeout(redirectTimer);
    };
  }, [navigate]);

  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        <h2 style={styles.title}>✅ Email Verified!</h2>
        <p style={styles.message}>Your email has been successfully verified.</p>
        <p style={styles.redirect}>
          Redirecting to login page in <strong>{secondsLeft}</strong> seconds...
        </p>
        <button style={styles.button} onClick={() => navigate("/login")}>
          Go to Login Now
        </button>
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
    background: "rgba(255, 255, 255, 0.08)",
    border: "1px solid rgba(255, 255, 255, 0.2)",
    padding: "40px 30px",
    borderRadius: "20px",
    textAlign: "center",
    backdropFilter: "blur(10px)",
    boxShadow: "0 8px 20px rgba(0, 0, 0, 0.3)",
    color: "#fff",
    maxWidth: "400px",
    width: "100%",
    animation: "fadeIn 0.5s ease-in-out",
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
    opacity: 0.85,
    marginBottom: "20px",
  },
  button: {
    backgroundColor: "#1abc9c",
    color: "#fff",
    border: "none",
    padding: "10px 20px",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "14px",
    transition: "background 0.3s ease",
  },
};

export default EmailVerified;
