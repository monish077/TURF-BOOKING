import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig"; // Your Axios instance

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

    console.log("🌐 Current URL:", window.location.href);
    console.log("🔍 Extracted token:", token);

    if (!token) {
      setStatus({
        loading: false,
        success: false,
        message: "❌ Verification token not found in the URL.",
      });
      return;
    }

    axiosInstance
      .get(`/users/verify?token=${token}`)
      .then((res) => {
        setStatus({
          loading: false,
          success: true,
          message: res.data.message || "✅ Email verified successfully!",
        });

        // Optional: Redirect after a short delay
        setTimeout(() => {
          navigate("/login", { replace: true });
        }, 2000);
      })
      .catch((err) => {
        console.error("❌ Verification failed:", err);
        setStatus({
          loading: false,
          success: false,
          message:
            err.response?.data?.error ||
            "❌ Verification failed. Invalid or expired token.",
        });
      });
  }, [location.search, navigate]);

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h2>Email Verification</h2>
      {status.loading ? (
        <p>⏳ Please wait while we verify your email...</p>
      ) : (
        <p style={{ color: status.success ? "green" : "red" }}>
          {status.message}
        </p>
      )}
    </div>
  );
}

export default VerifyEmail;
