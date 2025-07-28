import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "../assets/styles/home.css"; // Reuse shared styles
import turfImg from "../assets/images/turffield.jpg";

function ResetPassword() {
  const location = useLocation();
  const navigate = useNavigate();
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const tokenParam = queryParams.get("token");
    setToken(tokenParam || "");
  }, [location]);

  const handleReset = async (e) => {
    e.preventDefault();
    try {
      await axios.post("http://localhost:8080/api/users/reset-password", {
        token,
        newPassword,
      });
      setMessage("✅ Password reset successful! Redirecting to login...");
      setTimeout(() => navigate("/login"), 3000);
    } catch (err) {
      console.error(err);
      setMessage("❌ Failed to reset password. Token may be invalid or expired.");
    }
  };

  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Reset Your Password</h2>

        <form className="login-form" onSubmit={handleReset}>
          <div className="form-group">
            <label>New Password*</label>
            <input
              type="password"
              placeholder="Enter new password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="login-btn">
            Reset Password
          </button>

          {message && (
            <p style={{ marginTop: "15px", color: message.includes("✅") ? "lightgreen" : "red" }}>
              {message}
            </p>
          )}
        </form>
      </div>

      <div className="right-section">
        <img src={turfImg} alt="Turf Field" className="right-img" />
      </div>
    </div>
  );
}

export default ResetPassword;
