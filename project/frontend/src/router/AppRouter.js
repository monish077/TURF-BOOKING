import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Landing from "../pages/Landing";
import Slot from "../pages/Slot";
import Turfdetail from "../pages/Turfdetails";
import BookingForm from "../pages/BookingForm";
import BookingSuccess from "../pages/BookingSuccess";
import AdminViewBookings from "../pages/AdminViewBookings";
import PaymentPage from "../pages/PaymentPage";
import AdminDashboard from "../pages/AdminDashboard";
import ViewBookings from "../pages/ViewBooking";
import EmailVerified from "../pages/EmailVerified";
import VerifyEmail from "../pages/VerifyEmail";
import ForgotPassword from "../pages/ForgotPassword";
import ResetPassword from "../pages/ResetPassword";
import PaymentSuccess from "../pages/PaymentSuccess";

// Role-based private route wrapper
const PrivateRoute = ({ children, allowedRoles }) => {
  const token = sessionStorage.getItem("token");
  const role = sessionStorage.getItem("role");

  if (!token) {
    // Not logged in, redirect to login
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(role)) {
    // Logged in but role not allowed, redirect to home
    return <Navigate to="/" replace />;
  }

  return children;
};

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify-email" element={<VerifyEmail />} />
        <Route path="/email-verified" element={<EmailVerified />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        {/* User routes (require USER role) */}
        <Route
          path="/slot"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <Slot />
            </PrivateRoute>
          }
        />
        <Route
          path="/turfs/:id"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <Turfdetail />
            </PrivateRoute>
          }
        />
        <Route
          path="/book/:id"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <BookingForm />
            </PrivateRoute>
          }
        />
        <Route
          path="/success/:id"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <BookingSuccess />
            </PrivateRoute>
          }
        />
        <Route
          path="/payment/:bookingId"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <PaymentPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/payment-success"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <PaymentSuccess />
            </PrivateRoute>
          }
        />
        <Route
          path="/view-bookings"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              <ViewBookings />
            </PrivateRoute>
          }
        />

        {/* Admin routes (require ADMIN role) */}
        <Route
          path="/admin/dashboard"
          element={
            <PrivateRoute allowedRoles={["ADMIN"]}>
              <AdminDashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/admin-bookings"
          element={
            <PrivateRoute allowedRoles={["ADMIN"]}>
              <AdminViewBookings />
            </PrivateRoute>
          }
        />

        {/* Catch-all redirect to home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
};

export default AppRouter;
