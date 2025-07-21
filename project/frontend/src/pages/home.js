import React, { useState } from "react";
import { loginUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link, useNavigate } from "react-router-dom";
import turfImg from "../assets/images/turffield.jpg";

function UserLogin() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
  e.preventDefault();

  try {
  const response = await loginUser({ email, password });

  if (response.data) {
    // ✅ Store user object in sessionStorage
    sessionStorage.setItem("loggedInUser", JSON.stringify(response.data));

    // ✅ No need to store email separately — fetch from sessionStorage when needed
    navigate("/slot");
  } else {
    setError("Invalid login credentials ❌");
  }
} catch (err) {
  console.error("Login failed:", err);
  setError("Invalid email or password ❌");
}

};


  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Welcome back !</h2>

        {/* Google button can be wired later */}
        <button className="google-btn">
          <img
            src="https://www.svgrepo.com/show/475656/google-color.svg"
            alt="Google"
            className="google-icon"
          />
          Login with Google
        </button>

        <div className="separator">OR</div>

        <form className="login-form" onSubmit={handleSubmit}>
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
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}

          <button type="submit" className="login-btn">
            Log In
          </button>
        </form>

        <p className="signup-link">
          Don’t have an account? <Link to="/register">Create account</Link>
        </p>
      </div>

      <div className="right-section">
        <img src={turfImg} alt="Turf Field" className="right-img" />
      </div>
    </div>
  );
}

export default UserLogin;
