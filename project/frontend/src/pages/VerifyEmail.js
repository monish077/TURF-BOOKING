import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig"; // Axios instance with baseURL

function VerifyEmail() {
  const location = useLocation();
  const navigate = useNavigate();
  const [message, setMessage] = useState("Verifying your email...");

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    console.log("🌐 Current URL:", window.location.href);
    console.log("🔍 Extracted token:", token);

    if (token) {
      axiosInstance
        .get(`/users/verify?token=${token}`)
        .then((res) => {
          setMessage(res.data.message || "Email verified successfully!");
          // Redirect to a success page after short delay (optional)
          setTimeout(() => {
            navigate("/email-verified", { replace: true });
          }, 1500);
        })
        .catch((err) => {
          console.error("❌ Verification failed:", err);
          setMessage(
            err.response?.data?.message ||
              "Verification failed. Invalid or expired token."
          );
        });
    } else {
      setMessage("Verification token not found in the URL.");
    }
  }, [location.search, navigate]);

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h2>Email Verification</h2>
      <p>{message}</p>
    </div>
  );
}

export default VerifyEmail;
