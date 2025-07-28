import React, { useState } from "react";
import { registerUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link } from "react-router-dom";
import turfImg from "../assets/images/turffield.jpg"; // Ensure this file exists

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
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await registerUser(form);
      if (response.status === 200 || response.status === 201) {
        setShowPopup(true);
      } else {
        setError("Registration failed ❌");
      }
    } catch (err) {
      console.error("Registration error:", err);
      const errorMsg =
        err.response?.data?.error || "Registration failed. Please try again.";
      setError(errorMsg);
    }
  };

  return (
    <div className="container">
      {/* ✅ Success Popup */}
      {showPopup && (
        <div style={popupStyles.overlay}>
          <div style={popupStyles.popup}>
            <h2 style={{ color: "#2ecc71" }}>✅ Registration Successful</h2>
            <p>
              Please check your email and click the verification link to activate your account.
            </p>
            <button style={popupStyles.button} onClick={() => setShowPopup(false)}>
              OK
            </button>
          </div>
        </div>
      )}

      {/* ✅ Left Section */}
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Create an Account</h2>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name*</label>
            <input
              type="text"
              name="name"
              placeholder="Enter your full name"
              value={form.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Email*</label>
            <input
              type="email"
              name="email"
              placeholder="Enter your email address"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Password*</label>
            <input
              type="password"
              name="password"
              placeholder="Create a password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Role*</label>
            <select
              name="role"
              value={form.role}
              onChange={handleChange}
              className="custom-select"
              required
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          {error && <p style={{ color: "red", marginTop: "10px" }}>{error}</p>}

          <button type="submit" className="login-btn">
            Sign Up
          </button>
        </form>

        <p className="signup-link">
          Already have an account? <Link to="/login">Log In</Link>
        </p>
      </div>

      {/* ✅ Right Section */}
      <div className="right-section">
        <img
          src={turfImg}
          alt="Turf Field"
          className="right-img"
          onError={(e) => {
            e.target.style.display = "none";
            e.target.parentNode.style.backgroundImage =
              "url('https://images.unsplash.com/photo-1600880292089-90a7e086ee0a?auto=format&fit=crop&w=987&q=80')";
            e.target.parentNode.style.backgroundSize = "cover";
            e.target.parentNode.style.backgroundPosition = "center";
          }}
        />
      </div>
    </div>
  );
}

const popupStyles = {
  overlay: {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100vw",
    height: "100vh",
    backgroundColor: "rgba(0, 0, 0, 0.6)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 9999,
  },
  popup: {
    backgroundColor: "#1e1e1e",
    padding: "30px",
    borderRadius: "12px",
    color: "#fff",
    boxShadow: "0 0 20px rgba(0,0,0,0.5)",
    textAlign: "center",
    maxWidth: "400px",
    width: "90%",
  },
  button: {
    marginTop: "20px",
    padding: "10px 24px",
    backgroundColor: "#2ecc71",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "16px",
  },
};

export default Register;
