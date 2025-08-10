import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";

const EmailVerified = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const [secondsLeft, setSecondsLeft] = useState(5);
  const [status, setStatus] = useState("verifying"); // "verifying", "success", "error"
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!token) {
      setStatus("error");
      setErrorMessage("Verification token is missing.");
      return;
    }

    // Call backend API to verify token
    axios
      .get(`https://turf-booking-pp67.onrender.com/api/users/verify?token=${token}`)
      .then((response) => {
        if (response.data?.message) {
          setStatus("success");
        } else {
          setStatus("error");
          setErrorMessage("Unexpected response from server.");
        }
      })
      .catch((error) => {
        setStatus("error");
        if (error.response?.data?.error) {
          setErrorMessage(error.response.data.error);
        } else {
          setErrorMessage("Verification failed. Please try again.");
        }
      });
  }, [token]);

  useEffect(() => {
    if (status === "success") {
      const countdown = setInterval(() => {
        setSecondsLeft((prev) => {
          if (prev <= 1) {
            clearInterval(countdown);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);

      const redirectTimer = setTimeout(() => {
        navigate("/login", { replace: true });
      }, 5000);

      return () => {
        clearInterval(countdown);
        clearTimeout(redirectTimer);
      };
    }
  }, [status, navigate]);

  // Render UI based on verification status
  if (status === "verifying") {
    return (
      <div style={styles.wrapper}>
        <div style={styles.card}>
          <h2 style={styles.title}>⏳ Verifying your email...</h2>
          <p style={styles.message}>Please wait while we confirm your email verification.</p>
        </div>
      </div>
    );
  }

  if (status === "error") {
    return (
      <div style={styles.wrapper}>
        <div style={styles.card}>
          <h2 style={styles.title}>❌ Verification Failed</h2>
          <p style={styles.message}>{errorMessage}</p>
          <button
            style={styles.button}
            onClick={() => navigate("/register", { replace: true })}
          >
            Go to Register
          </button>
        </div>
      </div>
    );
  }

  // status === "success"
  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        <h2 style={styles.title}>✅ Email Verified!</h2>
        <p style={styles.message}>Your email has been successfully verified.</p>
        <p style={styles.redirect}>
          Redirecting to login page in <strong>{secondsLeft}</strong> second
          {secondsLeft !== 1 ? "s" : ""}...
        </p>
        <button
          style={styles.button}
          onClick={() => navigate("/login", { replace: true })}
        >
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
