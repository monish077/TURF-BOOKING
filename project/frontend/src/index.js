import React from 'react';
import ReactDOM from 'react-dom/client';
import './tailwind.css';
import App from './App';

// Create React root and render the app inside StrictMode
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
