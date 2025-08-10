import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig"; // Axios instance with baseURL

function VerifyEmail() {
  const location = useLocation();
  const navigate = useNavigate();
  const [message, setMessage] = useState("Verifying your email...");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Parse the token from query params
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    console.log("🌐 Current URL:", window.location.href);
    console.log("🔍 Extracted token:", token);

    if (token) {
      // Call backend API to verify email with the token
      axiosInstance
        .get(`/users/verify?token=${token}`)
        .then((res) => {
          setMessage(res.data.message || "Email verified successfully!");
          setLoading(false);

          // Redirect to a confirmation page or home after 1.5 seconds
          setTimeout(() => {
            navigate("/email-verified", { replace: true });
          }, 1500);
        })
        .catch((err) => {
          console.error("❌ Verification failed:", err);
          setMessage(
            err.response?.data?.error ||
              "Verification failed. Invalid or expired token."
          );
          setLoading(false);
        });
    } else {
      setMessage("Verification token not found in the URL.");
      setLoading(false);
    }
  }, [location.search, navigate]);

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h2>Email Verification</h2>
      <p>{loading ? "Verifying your email..." : message}</p>
    </div>
  );
}

export default VerifyEmail;
