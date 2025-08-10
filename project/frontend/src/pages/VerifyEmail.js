import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig"; // ✅ Use shared axios config

function VerifyEmail() {
  const location = useLocation();
  const navigate = useNavigate();
  const [message, setMessage] = useState("");

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    console.log("🌐 URL:", window.location.href);
    console.log("🔍 Extracted token:", token);

    if (token) {
      axiosInstance
        .get(`/users/verify?token=${token}`) // ✅ Base URL handled in axiosConfig.js
        .then((res) => {
          setMessage(res.data.message);
          navigate("/email-verified"); // 🔁 Redirect to success screen
        })
        .catch((err) => {
          console.error("❌ Verification failed:", err);
          setMessage(
            err.response?.data?.message ||
              "Verification failed. Invalid or expired token."
          );
        });
    } else {
      setMessage("Token not found in URL.");
    }
  }, [location, navigate]);

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h2>Email Verification</h2>
      <p>{message}</p>
    </div>
  );
}

export default VerifyEmail;
