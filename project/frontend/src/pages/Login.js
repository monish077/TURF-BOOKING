import React, { useState } from "react";
import { loginUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link, useNavigate } from "react-router-dom";
import turfImg from "../assets/images/turffield.jpg";

function Login() {
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
      const response = await loginUser({
        email: email.trim(),
        password,
      });

      if (response.status === 200 && response.data) {
        const { token, role, email: userEmail } = response.data;

        // ✅ Store user info
        sessionStorage.setItem("token", token);
        sessionStorage.setItem("email", userEmail);
        sessionStorage.setItem("role", role);

        console.log("✅ Logged in:", { userEmail, role, token });

        // ✅ Redirect based on role
        if (role === "ADMIN") {
          navigate("/admin/dashboard");
        } else if (role === "USER") {
          navigate("/slot");
        } else {
          setError("Unknown role");
        }
      } else {
        setError("Invalid email or password ❌");
      }
    } catch (err) {
      console.error("❌ Login error:", err);
      setError(err.response?.data?.error || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="left-section">
        <h1>MARS ARENA</h1>
        <h2>Welcome back!</h2>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email*</label>
            <input
              type="email"
              placeholder="Enter your email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
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
              autoComplete="current-password"
            />
          </div>

          <div className="form-group" style={{ marginBottom: "10px" }}>
            <Link
              to="/forgot-password"
              style={{ fontSize: "14px", color: "#3498db", textDecoration: "none" }}
            >
              Forgot Password?
            </Link>
          </div>

          {error && <p style={{ color: "red", marginTop: "10px" }}>{error}</p>}

          <button
            type="submit"
            className="login-btn"
            disabled={loading}
            style={{
              backgroundColor: loading ? "#aaa" : undefined,
              cursor: loading ? "not-allowed" : "pointer",
            }}
          >
            {loading ? "Logging in..." : "Log In"}
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

export default Login;
