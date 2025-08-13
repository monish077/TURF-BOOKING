import React from "react";
import AppRouter from "./router/AppRouter";
import "./App.css";

function App() {
  return (
    <div className="page-container">
      <AppRouter />
      <footer className="footer-bar">
        © {new Date().getFullYear()} Mars Arena | All Rights Reserved
      </footer>
    </div>
  );
}

export default App;
