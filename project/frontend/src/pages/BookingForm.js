import React, { useState, useEffect, useCallback } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";
import { motion } from "framer-motion";
import { ArrowLeft, Calendar, User, CheckCircle2, AlertCircle, Loader2 } from "lucide-react";

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

  const [formData, setFormData] = useState({ userName: "", date: "", slot: "" });
  const [bookedSlots, setBookedSlots] = useState([]);
  const [disabledDates, setDisabledDates] = useState([]);
  const [turfName, setTurfName] = useState("");
  const [turfPrice, setTurfPrice] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const getToday = () => new Date().toISOString().split("T")[0];

  const getTokenConfig = () => ({
    headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` }
  });

  const fetchBookings = useCallback(async () => {
    try {
      const res = await axiosInstance.get(`/bookings/turf/${id}`, getTokenConfig());
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
      } else {
        setBookedSlots([]);
      }
    } catch (err) {
      console.error("Failed to fetch bookings:", err);
    }
  }, [id, formData.date]);

  useEffect(() => {
    const fetchTurfDetails = async () => {
      try {
        const res = await axiosInstance.get(`/turfs/${id}`, getTokenConfig());
        setTurfName(res.data.name);
        setTurfPrice(res.data.pricePerHour);
      } catch (err) {
        console.error("Failed to fetch turf details:", err);
      }
    };
    fetchTurfDetails();
  }, [id]);

  useEffect(() => { fetchBookings(); }, [fetchBookings]);

  useEffect(() => {
    if (formData.date) {
      setLoadingSlots(true);
      fetchBookings().finally(() => setLoadingSlots(false));
    }
  }, [formData.date, fetchBookings]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrorMsg("");

    if (name === "date" && disabledDates.includes(value)) {
      setErrorMsg("This date is fully booked. Please choose another date.");
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
    setErrorMsg("");

    try {
      const userEmail = sessionStorage.getItem("email");
      if (!userEmail) {
        setErrorMsg("User not logged in.");
        setIsSubmitting(false);
        return;
      }

      const bookingData = { ...formData, userEmail, turfId: id, turfName, price: turfPrice };
      const res = await axiosInstance.post(`/bookings`, bookingData, getTokenConfig());
      navigate(`/payment/${res.data.id}`);
    } catch (err) {
      console.error("Booking failed ❌", err);
      setErrorMsg(err.response?.data?.error || "Failed to create booking. Try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-100 font-inter py-12 px-6 relative overflow-hidden flex items-center justify-center">
      {/* Background Decor */}
      <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
        <div className="absolute top-1/4 left-1/4 w-[400px] h-[400px] bg-emerald-500/10 blur-[120px] rounded-full" />
        <div className="absolute bottom-1/4 right-1/4 w-[400px] h-[400px] bg-cyan-600/10 blur-[120px] rounded-full" />
      </div>

      <div className="w-full max-w-2xl relative z-10">
        <Link to={`/turfs/${id}`} className="inline-flex items-center gap-2 text-slate-400 hover:text-emerald-400 transition-colors mb-8">
          <ArrowLeft size={18} />
          <span>Back to Arena Details</span>
        </Link>

        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-8 shadow-2xl"
        >
          <div className="text-center mb-8">
            <h2 className="text-3xl font-black mb-2 text-white">Book {turfName}</h2>
            <p className="text-emerald-400 font-bold text-xl">₹{turfPrice} <span className="text-slate-400 text-base font-normal">/ hour</span></p>
          </div>

          {errorMsg && (
            <motion.div 
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              className="bg-red-500/10 border border-red-500/50 text-red-400 px-4 py-3 rounded-xl flex items-start gap-3 mb-6"
            >
              <AlertCircle size={20} className="shrink-0 mt-0.5" />
              <p className="text-sm">{errorMsg}</p>
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Name Input */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Player Name</label>
              <div className="relative group">
                <User className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors" size={20} />
                <input
                  type="text"
                  name="userName"
                  value={formData.userName}
                  onChange={handleChange}
                  required
                  placeholder="Enter your full name"
                  className="w-full bg-[#060a0f]/50 border border-white/10 rounded-xl py-3 pl-12 pr-4 text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all"
                />
              </div>
            </div>

            {/* Date Input */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Play Date</label>
              <div className="relative group">
                <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors" size={20} />
                <input
                  type="date"
                  name="date"
                  min={getToday()}
                  value={formData.date}
                  onChange={handleChange}
                  required
                  className="w-full bg-[#060a0f]/50 border border-white/10 rounded-xl py-3 pl-12 pr-4 text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all [&::-webkit-calendar-picker-indicator]:invert"
                />
              </div>
            </div>

            {/* Time Slot Selection */}
            {formData.date && (
              <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: "auto" }}>
                <label className="block text-sm font-medium text-slate-300 mb-2 flex items-center justify-between">
                  <span>Available Time Slots</span>
                  {loadingSlots && <Loader2 className="animate-spin text-emerald-400" size={16} />}
                </label>
                
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 max-h-60 overflow-y-auto pr-2 custom-scrollbar">
                  {generateSlots().map((slot, index) => {
                    const isBooked = bookedSlots.includes(slot);
                    const isSelected = formData.slot === slot;
                    return (
                      <button
                        key={index}
                        type="button"
                        disabled={isBooked}
                        onClick={() => handleChange({ target: { name: "slot", value: slot } })}
                        className={`py-3 px-2 text-sm font-medium rounded-xl border transition-all flex items-center justify-center gap-1.5
                          ${isBooked 
                            ? "bg-white/5 border-white/5 text-slate-600 cursor-not-allowed" 
                            : isSelected 
                              ? "bg-emerald-500 border-emerald-400 text-black shadow-[0_0_15px_rgba(52,211,153,0.3)]" 
                              : "bg-[#060a0f]/50 border-white/10 text-slate-300 hover:border-emerald-500/50 hover:bg-emerald-500/10 hover:text-emerald-400"
                          }`}
                      >
                        {isSelected && <CheckCircle2 size={14} />}
                        {slot}
                      </button>
                    );
                  })}
                </div>
                {!formData.slot && !loadingSlots && (
                  <p className="text-xs text-amber-400 mt-2">* Please select a time slot to continue</p>
                )}
              </motion.div>
            )}

            {/* Submit Button */}
            <div className="pt-6">
              <button 
                type="submit" 
                disabled={isSubmitting || !formData.slot}
                className="w-full py-4 bg-emerald-500 hover:bg-emerald-400 disabled:bg-emerald-500/50 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_30px_rgba(52,211,153,0.5)] transition-all flex items-center justify-center gap-2"
              >
                {isSubmitting ? (
                  <><Loader2 className="animate-spin" size={20} /> Processing...</>
                ) : (
                  <>Confirm Booking for ₹{turfPrice}</>
                )}
              </button>
            </div>

          </form>
        </motion.div>
      </div>
      
      {/* Custom Scrollbar CSS for the slot list */}
      <style>{`
        .custom-scrollbar::-webkit-scrollbar { width: 6px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 4px; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.2); }
      `}</style>
    </div>
  );
};

export default BookingForm;
