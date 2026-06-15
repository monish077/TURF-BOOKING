import React, { useState } from "react";
import { loginUser } from "../services/Api";
import "../assets/styles/home.css";
import { Link, useNavigate } from "react-router-dom";

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

        // Store user info
        sessionStorage.setItem("token", token);
        sessionStorage.setItem("email", userEmail);
        sessionStorage.setItem("role", role);

        // Redirect based on role
        if (role === "ADMIN") {
          navigate("/admin/dashboard");
        } else if (role === "USER") {
          navigate("/slot");
        } else {
          setError("Unknown role. Please contact support.");
        }
      } else {
        setError("Invalid email or password.");
      }
    } catch (err) {
      setError(err.response?.data?.error || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        {/* Brand */}
        <div className="auth-logo">MARS ARENA</div>
        <div className="auth-tagline">Turf Booking Platform</div>

        <div className="auth-divider" />

        <div className="auth-heading">Welcome back</div>
        <div className="auth-subheading">Sign in to book your arena</div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="login-email">Email Address</label>
            <input
              id="login-email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
            />
          </div>

          <div className="form-group">
            <label htmlFor="login-password">Password</label>
            <input
              id="login-password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>

          <div className="form-group" style={{ marginBottom: "8px" }}>
            <Link to="/forgot-password" className="auth-link-small">
              Forgot your password?
            </Link>
          </div>

          {error && (
            <div className="auth-error">
              <span>⚠️</span> {error}
            </div>
          )}

          <button
            type="submit"
            id="login-submit-btn"
            className="login-btn"
            disabled={loading}
          >
            {loading ? "Signing in..." : "Sign In →"}
          </button>
        </form>

        <p className="signup-link">
          New to Mars Arena? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;
