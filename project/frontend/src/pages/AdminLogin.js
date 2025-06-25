import React, { useState } from "react";
import "../assets/styles/home.css";
import { Link, useNavigate } from "react-router-dom";
import turfImg from "../assets/images/turffield.jpg";
import { loginAdmin } from "../services/Api";

function AdminLogin() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await loginAdmin({ email, password });
      console.log("Admin login successful ✅", response.data);
      // Store admin info in localStorage
      localStorage.setItem("loggedInAdmin", JSON.stringify(response.data));
      // Navigate to admin dashboard page (update route as per your router setup)
      navigate("/admin-dashboard");
    } catch (err) {
      setError("Invalid email or password");
      console.error("Admin login failed ❌", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Welcome Admin!</h2>

        <button className="google-btn" disabled>
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
              disabled={loading}
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
              disabled={loading}
            />
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? "Logging in..." : "Log In"}
          </button>
        </form>

        <p className="signup-link">
          Don’t have an account?{" "}
          <Link to="/admin-register">Create account</Link>
        </p>
      </div>

      <div className="right-section">
        <img src={turfImg} alt="Turf Field" className="right-img" />
      </div>
    </div>
  );
}

export default AdminLogin;
