import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import UserLogin from "../pages/home";
import Register from "../pages/Register";
import Landing from "../pages/Landing"; // ✅ Import the new page
import Slot from "../pages/Slot";
import Turfdetail from "../pages/Turfdetails";
import BookingForm from "../pages/BookingForm";
import BookingSuccess from "../pages/BookingSuccess";
import AdminViewBookings from "../pages/AdminViewBookings";
import PaymentPage from "../pages/PaymentPage";
import AdminLogin from "../pages/AdminLogin";
import AdminRegister from "../pages/AdminRegister";
import AdminDashboard from "../pages/AdminDashboard";
import ViewBookings from "../pages/ViewBooking";

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Landing />} /> {/* Landing Page */}
        <Route path="/login" element={<UserLogin />} />
        {/* Your login page */}
        <Route path="/register" element={<Register />} />
        <Route path="/slot" element={<Slot />} />
        <Route path="/turfs/:id" element={<Turfdetail />} />
        <Route path="/book/:id" element={<BookingForm />} />
        <Route path="/booking-success" element={<BookingSuccess />} />
        <Route path="/admin-bookings" element={<AdminViewBookings />} />
        <Route path="/payment/:bookingId" element={<PaymentPage />} />
        <Route path="/admin-login" element={<AdminLogin />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />
        <Route path="/admin-register" element={<AdminRegister />} />
        <Route path="/view-bookings" element={<ViewBookings />} />
      </Routes>
    </Router>
  );
};

export default AppRouter;
