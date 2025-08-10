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
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
        <div className="nav-links">
          <Link to="/login" className="book-btn">
            Book Now
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="hero-content">
        <h1>
          Welcome to <span className="highlight">MARS ARENA</span>
        </h1>
        <h2>Your Game, Your Time!</h2>
        <p style={{ maxWidth: "600px", margin: "0 auto", lineHeight: "1.5" }}>
          Effortlessly book your favorite turf and enjoy a seamless play
          experience. From football to cricket – we’ve got your game covered.
        </p>

        <div className="button-group" style={{ marginTop: "20px" }}>
          <Link to="/login">
            <button className="btn-primary">Get Started</button>
          </Link>
          <Link to="/register">
            <button className="btn-secondary" style={{ marginLeft: "10px" }}>
              Create Account
            </button>
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        © {new Date().getFullYear()} MARS ARENA | All rights reserved.
      </footer>
    </div>
  );
};

export default Landing;
