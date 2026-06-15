import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";
import "../assets/styles/home.css";

function VerifyEmail() {
  const location = useLocation();
  const navigate = useNavigate();
  const [status, setStatus] = useState({
    loading: true,
    success: false,
    message: "Verifying your email...",
  });

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    if (!token) {
      setStatus({
        loading: false,
        success: false,
        message: "Verification token not found in the URL.",
      });
      return;
    }

    axiosInstance
      .get(`/users/verify?token=${token}`)
      .then((res) => {
        setStatus({
          loading: false,
          success: true,
          message: res.data.message || "Email verified successfully!",
        });

        setTimeout(() => {
          navigate("/login", { replace: true });
        }, 2500);
      })
      .catch((err) => {
        setStatus({
          loading: false,
          success: false,
          message:
            err.response?.data?.error ||
            "Verification failed. Invalid or expired token.",
        });
      });
  }, [location.search, navigate]);

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ textAlign: "center", maxWidth: 420 }}>
        <div className="auth-logo">MARS ARENA</div>
        <div className="auth-tagline">Email Verification</div>
        <div className="auth-divider" />

        {status.loading ? (
          <>
            <div className="popup-icon" style={{ fontSize: "52px", marginBottom: "16px" }}>⏳</div>
            <div className="auth-heading">Verifying your email</div>
            <p style={{ color: "#8a9bb0", marginTop: "10px", fontSize: "14px" }}>
              Please wait while we confirm your email address…
            </p>
          </>
        ) : status.success ? (
          <>
            <div className="popup-icon" style={{ fontSize: "52px", marginBottom: "16px" }}>✅</div>
            <div className="auth-heading" style={{ color: "#00ff9d" }}>Verified!</div>
            <p style={{ color: "#8a9bb0", marginTop: "10px", fontSize: "14px" }}>
              {status.message}
            </p>
            <p style={{ color: "#4a5568", marginTop: "12px", fontSize: "13px" }}>
              Redirecting you to login…
            </p>
          </>
        ) : (
          <>
            <div className="popup-icon" style={{ fontSize: "52px", marginBottom: "16px" }}>❌</div>
            <div className="auth-heading" style={{ color: "#ff6b6b" }}>Verification Failed</div>
            <div className="auth-error" style={{ marginTop: "12px" }}>
              <span>⚠️</span> {status.message}
            </div>
            <button
              className="login-btn"
              style={{ marginTop: "20px" }}
              onClick={() => navigate("/login")}
            >
              Back to Login
            </button>
          </>
        )}
      </div>
    </div>
  );
}

export default VerifyEmail;
