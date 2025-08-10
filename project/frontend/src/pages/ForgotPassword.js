import React, { useState } from "react";
import axiosInstance from "../services/axiosInstance"; // ✅ Use shared config
import "../assets/styles/home.css";
import turfImg from "../assets/images/turffield.jpg";
import { Link } from "react-router-dom";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    setLoading(true);

    try {
      await axiosInstance.post("/users/forgot-password", { email: email.trim() });
      setMessage("✅ Reset link sent! Please check your email.");
    } catch (err) {
      console.error("❌ Forgot password error:", err);
      setError(err.response?.data?.error || "❌ Error sending reset link. Try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Reset Your Password</h2>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email Address*</label>
            <input
              type="email"
              name="email"
              placeholder="Enter your registered email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          {message && <p style={{ color: "lightgreen", marginTop: "10px" }}>{message}</p>}
          {error && <p style={{ color: "red", marginTop: "10px" }}>{error}</p>}

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? "Sending..." : "Send Reset Link"}
          </button>
        </form>

        <p className="signup-link" style={{ marginTop: "20px" }}>
          <Link to="/login">⬅ Back to Login</Link>
        </p>
      </div>

      <div className="right-section">
        <img src={turfImg} alt="Turf Field" className="right-img" />
      </div>
    </div>
  );
}

export default ForgotPassword;
