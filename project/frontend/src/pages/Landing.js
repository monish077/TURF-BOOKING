import React from "react";
import "../assets/styles/landing.css";
import { Link } from "react-router-dom";
import turfImage from "../assets/images/field.jpg";

const Landing = () => {
  return (
    <div
      className="home"
      style={{
        backgroundImage: `url(${turfImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
      }}
    >
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
      </nav>

      {/* Hero Section */}
      <section className="hero-content">
        <span className="hero-badge">⚽ Premium Turf Booking</span>

        <h1>
          Welcome to <span className="highlight">MARS ARENA</span>
        </h1>

        <h2>Your Game. Your Time. Your Arena.</h2>

        <p>
          Effortlessly book your favorite turf and enjoy a seamless play
          experience. From football to cricket — we've got your game covered
          with top-notch facilities.
        </p>

        <div className="button-group">
          <Link to="/login">
            <button className="btn-primary">Get Started →</button>
          </Link>
          <Link to="/register">
            <button className="btn-secondary">Create Account</button>
          </Link>
        </div>
      </section>

      {/* Stats Bar */}
      <div className="hero-stats">
        <div className="stat-item">
          <div className="stat-number">20+</div>
          <div className="stat-label">Premium Turfs</div>
        </div>
        <div className="stat-item">
          <div className="stat-number">5K+</div>
          <div className="stat-label">Happy Players</div>
        </div>
        <div className="stat-item">
          <div className="stat-number">3</div>
          <div className="stat-label">Sports Covered</div>
        </div>
        <div className="stat-item">
          <div className="stat-number">24/7</div>
          <div className="stat-label">Booking Support</div>
        </div>
      </div>
    </div>
  );
};

export default Landing;
