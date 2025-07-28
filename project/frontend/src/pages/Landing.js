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
      <nav className="navbar">
        <div className="logo">MARS ARENA</div>
        <Link to="/login" className="book-btn">
          Book Now
        </Link>
      </nav>

      <div className="hero-content">
        <h1>
          Welcome to <span className="highlight">MARS ARENA</span>
        </h1>
        <h2>Your Game, Your Time!</h2>
        <p>
          Effortlessly book your favorite turf and enjoy a seamless play
          experience. From football to cricket – we’ve got your game covered.
        </p>

        <div className="button-group">
          <Link to="/login">
            <button className="btn-primary">Get Started</button>
          </Link>
        </div>
      </div>

      <footer className="footer">© 2025 MARS ARENA | All rights reserved.</footer>
    </div>
  );
};

export default Landing;
