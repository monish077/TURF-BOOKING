import React, { useState, useEffect, useCallback } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";
import "../assets/styles/bookingform.css";

const generateSlots = () => {
  const slots = [];
  for (let i = 0; i < 24; i++) {
    const from = i.toString().padStart(2, "0") + ":00";
    const to = ((i + 1) % 24).toString().padStart(2, "0") + ":00";
    slots.push(`${from} - ${to}`);
  }
  return slots;
};

const BookingForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    userName: "",
    date: "",
    slot: ""
  });

  const [bookedSlots, setBookedSlots] = useState([]);
  const [disabledDates, setDisabledDates] = useState([]);
  const [turfName, setTurfName] = useState("");
  const [turfPrice, setTurfPrice] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loadingSlots, setLoadingSlots] = useState(false);

  const getToday = () => new Date().toISOString().split("T")[0];

  const getTokenConfig = () => ({
    headers: {
      Authorization: `Bearer ${sessionStorage.getItem("token")}`
    }
  });

  // fetchBookings only depends on id and date (from formData)
  const fetchBookings = useCallback(async () => {
    try {
      const res = await axiosInstance.get(`/bookings/turf/${id}`, getTokenConfig());
      const turfBookings = res.data;

      // Count bookings per date
      const dateCounts = turfBookings.reduce((acc, booking) => {
        acc[booking.date] = (acc[booking.date] || 0) + 1;
        return acc;
      }, {});

      // Fully booked dates (24 slots max assumed)
      const fullDates = Object.keys(dateCounts).filter(date => dateCounts[date] >= 24);
      setDisabledDates(fullDates);

      // Update booked slots for selected date
      if (formData.date) {
        const bookedForDate = turfBookings
          .filter(booking => booking.date === formData.date)
          .map(booking => booking.slot);
        setBookedSlots(bookedForDate);
      } else {
        setBookedSlots([]);
      }
    } catch (err) {
      console.error("Failed to fetch bookings:", err);
      alert("Error fetching booking data.");
    }
  }, [id, formData.date]);

  // Fetch turf details once on mount
  useEffect(() => {
    const fetchTurfDetails = async () => {
      try {
        const res = await axiosInstance.get(`/turfs/${id}`, getTokenConfig());
        setTurfName(res.data.name);
        setTurfPrice(res.data.pricePerHour);
      } catch (err) {
        console.error("Failed to fetch turf details:", err);
        alert("Error fetching turf details.");
      }
    };

    fetchTurfDetails();
  }, [id]);

  // Fetch bookings on mount and whenever fetchBookings changes
  useEffect(() => {
    fetchBookings();
  }, [fetchBookings]);

  // Refresh booked slots when date changes
  useEffect(() => {
    if (formData.date) {
      setLoadingSlots(true);
      fetchBookings().finally(() => setLoadingSlots(false));
    }
  }, [formData.date, fetchBookings]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "date" && disabledDates.includes(value)) {
      alert("This date is fully booked. Please choose another date.");
      setFormData(prev => ({ ...prev, date: "", slot: "" }));
      setBookedSlots([]);
      return;
    }

    if (name === "date") {
      setBookedSlots([]);
      setFormData(prev => ({ ...prev, date: value, slot: "" }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const userEmail = sessionStorage.getItem("email");
      if (!userEmail) {
        alert("User not logged in.");
        setIsSubmitting(false);
        return;
      }

      const bookingData = {
        ...formData,
        userEmail,
        turfId: id,
        turfName,
        price: turfPrice
      };

      const res = await axiosInstance.post(`/bookings`, bookingData, getTokenConfig());
      navigate(`/payment/${res.data.id}`);
    } catch (err) {
      console.error("Booking failed ❌", err);
      alert(err.response?.data?.error || "Failed to create booking. Try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="booking-page">
      <Link to={`/turfs/${id}`} className="back-link">← Back to Turf</Link>
      <div className="booking-card">
        <h2>Book {turfName}</h2>
        <p className="price">Price: ₹{turfPrice}</p>

        <form onSubmit={handleSubmit}>
          <label>Name:</label>
          <input
            type="text"
            name="userName"
            value={formData.userName}
            onChange={handleChange}
            required
          />

          <label>Select Date:</label>
          <input
            type="date"
            name="date"
            min={getToday()}
            value={formData.date}
            onChange={handleChange}
            required
          />

          {formData.date && (
            <>
              <label>Time Slot:</label>
              {loadingSlots ? (
                <p>Loading available slots...</p>
              ) : (
                <select
                  name="slot"
                  value={formData.slot}
                  onChange={handleChange}
                  required
                >
                  <option value="">-- Select Slot --</option>
                  {generateSlots().map((slot, index) => (
                    <option
                      key={index}
                      value={slot}
                      disabled={bookedSlots.includes(slot)}
                    >
                      {slot} {bookedSlots.includes(slot) ? " (Booked)" : ""}
                    </option>
                  ))}
                </select>
              )}
            </>
          )}

          <button type="submit" className="confirm-btn" disabled={isSubmitting}>
            {isSubmitting ? "Booking..." : "Confirm Booking"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default BookingForm;
