import React, { useState, useEffect, useCallback } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import "../assets/styles/bookingform.css";
import { createBooking, getBookingsByTurfId, getTurfById } from "../services/Api";

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
    whatsappNumber: "",
    date: "",
    slot: ""
  });

  const [bookedSlots, setBookedSlots] = useState([]);
  const [disabledDates, setDisabledDates] = useState([]);
  const [turfName, setTurfName] = useState("");
  const [turfPrice, setTurfPrice] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const getToday = () => {
    const today = new Date();
    return today.toISOString().split("T")[0];
  };

  const updateBookings = useCallback(async () => {
    try {
      const res = await getBookingsByTurfId(id);
      const turfBookings = res.data;

      const dateCounts = turfBookings.reduce((acc, booking) => {
        acc[booking.date] = (acc[booking.date] || 0) + 1;
        return acc;
      }, {});
      const fullDates = Object.keys(dateCounts).filter(date => dateCounts[date] >= 24);
      setDisabledDates(fullDates);

      if (formData.date) {
        const bookedForDate = turfBookings
          .filter(booking => booking.date === formData.date)
          .map(booking => booking.slot);
        setBookedSlots(bookedForDate);
      }
    } catch (err) {
      console.error("Failed to fetch bookings:", err);
    }
  }, [id, formData.date]);

  useEffect(() => {
    updateBookings();

    const fetchTurfDetails = async () => {
      try {
        const res = await getTurfById(id);
        const turf = res.data;
        setTurfName(turf.name);
        setTurfPrice(turf.pricePerHour);
      } catch (err) {
        console.error("Failed to fetch turf details:", err);
      }
    };

    fetchTurfDetails();
  }, [updateBookings, id]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "date" && disabledDates.includes(value)) {
      alert("This date is fully booked. Please choose another date.");
      setFormData(prev => ({ ...prev, date: "" }));
      setBookedSlots([]);
      return;
    }

    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const loggedInUser = JSON.parse(sessionStorage.getItem("loggedInUser"));
      const userEmail = loggedInUser?.email;

      if (!userEmail) {
        alert("User not logged in.");
        return;
      }

      // ✅ Sanitize WhatsApp number: keep only digits
      const sanitizedWhatsApp = formData.whatsappNumber.replace(/\D/g, "");

      const bookingData = {
        ...formData,
        whatsappNumber: sanitizedWhatsApp,
        userEmail: userEmail,
        turfId: id,
        turfName: turfName,
        price: turfPrice
      };

      const res = await createBooking(bookingData);
      const newBookingId = res.data.id;

      navigate(`/payment/${newBookingId}`);
    } catch (err) {
      console.error("Booking failed ❌", err);
      alert("Failed to create booking. Try again.");
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

          <label>WhatsApp Number:</label>
          <input
            type="tel"
            name="whatsappNumber"
            value={formData.whatsappNumber}
            onChange={handleChange}
            placeholder="e.g. +91 8667541251"
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
                    {slot}
                  </option>
                ))}
              </select>
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
