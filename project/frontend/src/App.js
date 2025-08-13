import React from "react";
import AppRouter from "./router/AppRouter";
import "./App.css"; // ✅ Import global styles

function App() {
  return (
    <div className="app-container">
      <AppRouter />
    </div>
  );
}

export default App;
