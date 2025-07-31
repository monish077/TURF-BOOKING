import React, { useEffect, useState } from "react";
import { getAdminBookings, deleteBooking } from "../services/Api";
import "../assets/styles/viewbookings.css";

const AdminViewBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  // ✅ Admin email from sessionStorage
  const adminEmail = sessionStorage.getItem("email");

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const res = await getAdminBookings(adminEmail); // ✅ use email as param
      setBookings(res.data);
    } catch (err) {
      console.error("❌ Failed to fetch admin bookings:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (window.confirm("Are you sure you want to cancel this booking?")) {
      try {
        await deleteBooking(id);
        fetchBookings(); // refresh list
      } catch (err) {
        console.error("❌ Error cancelling booking:", err);
      }
    }
  };

  useEffect(() => {
    if (adminEmail) {
      fetchBookings();
    } else {
      console.warn("⚠️ Admin email not found in sessionStorage.");
      setLoading(false);
    }
  }, [adminEmail]);

  return (
    <div className="view-bookings-page">
      <h2>📅 My Turf Bookings (Admin View)</h2>
      {loading ? (
        <p>Loading bookings...</p>
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
                <td>{b.date}</td>
                <td>{b.slot || "N/A"}</td>
                <td>₹{b.price}</td>
                <td>
                  <button
                    className="cancel-btn"
                    onClick={() => handleCancel(b.id)}
                  >
                    Cancel
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
