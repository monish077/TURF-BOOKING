import React, { useState } from "react";
import { registerUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link, useNavigate } from "react-router-dom";
import turfImg from "../assets/images/turffield.jpg";

function UserRegister() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await registerUser({ name, email, password });

      if (response.status === 200 || response.status === 201) {
        console.log("Registered:", response.data);
        navigate("/login");
      } else {
        setError("Registration failed ❌");
      }
    } catch (err) {
      console.error(err);
      setError("Registration failed ❌");
    }
  };

  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Create an Account</h2>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name*</label>
            <input
              type="text"
              placeholder="Enter your full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Email*</label>
            <input
              type="email"
              placeholder="Enter your email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password*</label>
            <input
              type="password"
              placeholder="Create a password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}

          <button type="submit" className="login-btn">
            Sign Up
          </button>
        </form>

        <p className="signup-link">
          Already have an account? <Link to="/login">Log In</Link>
        </p>
      </div>

      <div className="right-section">
        <img src={turfImg} alt="Turf Field" className="right-img" />
      </div>
    </div>
  );
}

export default UserRegister;
