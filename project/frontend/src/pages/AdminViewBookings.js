import React, { useEffect, useState, useCallback } from "react";
import { getAdminBookings, deleteBooking } from "../services/Api";
import "../assets/styles/viewbookings.css";

const AdminViewBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  // Get admin email from sessionStorage
  const adminEmail = sessionStorage.getItem("email");

  // Fetch bookings for this admin
  const fetchBookings = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAdminBookings(adminEmail);

      // Sort bookings by date ascending
      const sortedBookings = [...res.data].sort(
        (a, b) => new Date(a.date) - new Date(b.date)
      );
      setBookings(sortedBookings);
    } catch (err) {
      console.error("❌ Failed to fetch admin bookings:", err);
    } finally {
      setLoading(false);
    }
  }, [adminEmail]);

  // Cancel a booking by ID
  const handleCancel = async (id) => {
    if (!window.confirm("Are you sure you want to cancel this booking?")) return;

    try {
      setDeletingId(id);
      await deleteBooking(id);
      await fetchBookings();
    } catch (err) {
      console.error("❌ Error cancelling booking:", err);
      alert("Failed to cancel booking. Please try again.");
    } finally {
      setDeletingId(null);
    }
  };

  // Format date for display
  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "short", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // Fetch bookings on mount and set auto-refresh every 60s
  useEffect(() => {
    if (adminEmail) {
      fetchBookings();
      const interval = setInterval(fetchBookings, 60000); // Refresh every 60 sec
      return () => clearInterval(interval);
    } else {
      console.warn("⚠️ Admin email not found in sessionStorage.");
      setLoading(false);
    }
  }, [adminEmail, fetchBookings]);

  return (
    <div className="view-bookings-page">
      <h2>📅 My Turf Bookings (Admin View)</h2>

      {loading ? (
        <div className="loading-spinner">Loading bookings...</div>
      ) : bookings.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User Name</th>
              <th>User Email</th>
              <th>Turf</th>
              <th>Date</th>
              <th>Slot</th>
              <th>Price</th>
              <th>Cancel</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.userName}</td>
                <td>{b.userEmail}</td>
                <td>{b.turfName}</td>
                <td>{formatDate(b.date)}</td>
                <td>{b.slot || "N/A"}</td>
                <td>₹{b.price}</td>
                <td>
                  <button
                    className="cancel-btn"
                    onClick={() => handleCancel(b.id)}
                    disabled={deletingId === b.id}
                  >
                    {deletingId === b.id ? "Cancelling..." : "Cancel"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No bookings found for your turfs.</p>
      )}
    </div>
  );
};

export default AdminViewBookings;
