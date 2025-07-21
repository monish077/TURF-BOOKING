import React from "react";
import "../assets/styles/landing.css";
import { Link } from "react-router-dom";
import turfImage from "../assets/images/field.jpg";

const Landing = () => {
  return (
    <div className="home">
      <img src={turfImage} alt="Field" className="right-img" />

      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
        <Link to="/login" className="book-btn">Book Now</Link>
      </nav>

      <div className="hero-content">
        <h1>Book Your Turf</h1>
        <h2>Play AnyTime!</h2>
        <p>
          Step onto the perfect pitch – where every game is seamless, every
          booking is effortless, and every moment is legendary.
        </p>

        <div className="button-group">
          <Link to="/login">
            <button className="btn-primary">User Login</button>
          </Link>
          <Link to="/admin-login">
            <button className="btn-primary">Admin Login</button>
          </Link>
        </div>
      </div>

      <footer className="footer">
        © 2025 MARS ARENA, Inc.
      </footer>
    </div>
  );
};

export default Landing;
