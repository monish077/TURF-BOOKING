import React, { useState } from "react";
import { registerUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link } from "react-router-dom";

function Register() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    role: "USER",
  });
  const [error, setError] = useState("");
  const [showPopup, setShowPopup] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await registerUser(form);

      if (response.status >= 200 && response.status < 300) {
        setShowPopup(true);
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (err) {
      const errorMsg =
        err.response?.data?.error ||
        err.response?.data?.message ||
        "Registration failed. Please try again.";
      setError(errorMsg);
    }
  };

  return (
    <div className="auth-page">
      {/* Success Popup */}
      {showPopup && (
        <div className="popup-overlay">
          <div className="popup-card">
            <div className="popup-icon">✅</div>
            <h2>Registration Successful!</h2>
            <p>
              Please check your email inbox and click the verification link to
              activate your account before logging in.
            </p>
            <button
              id="popup-ok-btn"
              className="popup-btn"
              onClick={() => setShowPopup(false)}
            >
              Got it!
            </button>
          </div>
        </div>
      )}

      <div className="auth-card">
        {/* Brand */}
        <div className="auth-logo">MARS ARENA</div>
        <div className="auth-tagline">Turf Booking Platform</div>

        <div className="auth-divider" />

        <div className="auth-heading">Create your account</div>
        <div className="auth-subheading">Join the arena today</div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="reg-name">Full Name</label>
            <input
              id="reg-name"
              type="text"
              name="name"
              placeholder="Enter your full name"
              value={form.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="reg-email">Email Address</label>
            <input
              id="reg-email"
              type="email"
              name="email"
              placeholder="Enter your email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="reg-password">Password</label>
            <input
              id="reg-password"
              type="password"
              name="password"
              placeholder="Create a strong password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="reg-role">Account Type</label>
            <select
              id="reg-role"
              name="role"
              value={form.role}
              onChange={handleChange}
              className="custom-select"
              required
            >
              <option value="USER">Player (User)</option>
              <option value="ADMIN">Turf Manager (Admin)</option>
            </select>
          </div>

          {error && (
            <div className="auth-error">
              <span>⚠️</span> {error}
            </div>
          )}

          <button
            type="submit"
            id="register-submit-btn"
            className="login-btn"
          >
            Create Account →
          </button>
        </form>

        <p className="signup-link">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
