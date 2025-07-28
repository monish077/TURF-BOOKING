import React, { useState } from "react";
import axios from "axios";
import "../assets/styles/home.css"; // Reuse same CSS used in Login/Register

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.post("http://localhost:8080/api/users/forgot-password", { email });
      setMessage("✅ Reset link sent! Please check your email.");
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.error || "❌ Error sending reset link. Try again.");
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

          {message && <p style={{ color: "lightgreen" }}>{message}</p>}
          {error && <p style={{ color: "red" }}>{error}</p>}

          <button type="submit" className="login-btn">
            Send Reset Link
          </button>
        </form>

        <p className="signup-link" style={{ marginTop: "20px" }}>
          <a href="/login">Back to Login</a>
        </p>
      </div>

      <div className="right-section">
        <img
          src={require("../assets/images/turffield.jpg")}
          alt="Turf Field"
          className="right-img"
        />
      </div>
    </div>
  );
}

export default ForgotPassword;
