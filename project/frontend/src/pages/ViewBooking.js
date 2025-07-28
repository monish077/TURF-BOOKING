import React, { useEffect, useState } from "react";
import { getBookingsByUserEmail, deleteBooking } from "../services/Api";
import "../assets/styles/viewbookings.css";

const ViewBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchBookings = async () => {
    const userEmail = sessionStorage.getItem("email");
    if (!userEmail) {
      console.warn("No email found in sessionStorage");
      return;
    }

    try {
      const res = await getBookingsByUserEmail(userEmail);
      setBookings(res.data);
    } catch (err) {
      console.error("❌ Failed to fetch bookings:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (window.confirm("Are you sure you want to cancel this booking?")) {
      await deleteBooking(id);
      fetchBookings(); // refresh
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  return (
    <div className="view-bookings-page">
      <h2>Your Bookings</h2>

      {loading ? (
        <p>Loading bookings...</p>
      ) : bookings.length > 0 ? (
        <table>
          <thead>
            <tr>
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
                <td>{b.turfName}</td>
                <td>{b.date}</td>
                <td>{b.slot}</td>
                <td>₹{b.price}</td>
                <td>
                  <button className="cancel-btn" onClick={() => handleCancel(b.id)}>
                    Cancel
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No bookings found.</p>
      )}
    </div>
  );
};

export default ViewBookings;
