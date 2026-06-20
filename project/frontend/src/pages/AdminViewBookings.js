import React, { useEffect, useState, useCallback } from "react";
import { getAdminBookings, deleteBooking } from "../services/Api";
import { motion, AnimatePresence } from "framer-motion";
import { Trash2, Calendar, Clock, MapPin, User, Loader2 } from "lucide-react";

const AdminViewBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  const adminEmail = sessionStorage.getItem("email");

  const fetchBookings = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAdminBookings(adminEmail);
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

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "short", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  useEffect(() => {
    if (adminEmail) {
      fetchBookings();
      const interval = setInterval(fetchBookings, 60000);
      return () => clearInterval(interval);
    } else {
      setLoading(false);
    }
  }, [adminEmail, fetchBookings]);

  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-300 font-inter p-6 md:p-12 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute inset-0 z-0 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[500px] h-[500px] bg-emerald-500/10 blur-[150px] rounded-full" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[500px] h-[500px] bg-violet-600/10 blur-[150px] rounded-full" />
      </div>

      <div className="max-w-6xl mx-auto relative z-10">
        <div className="mb-10 text-center md:text-left">
          <h2 className="text-3xl md:text-4xl font-black text-white mb-2 tracking-tight">
            Manage Bookings
          </h2>
          <p className="text-slate-400">View and manage all reservations for your turfs</p>
        </div>

        {loading ? (
          <div className="flex flex-col items-center justify-center h-64 bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl">
            <Loader2 className="w-10 h-10 text-emerald-400 animate-spin mb-4" />
            <p className="text-slate-400 font-medium">Loading bookings...</p>
          </div>
        ) : bookings.length > 0 ? (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <AnimatePresence>
              {bookings.map((b) => (
                <motion.div
                  key={b.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.9 }}
                  className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 hover:border-emerald-500/30 transition-all group"
                >
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                        <MapPin size={18} className="text-emerald-400" />
                        {b.turfName}
                      </h3>
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                        Confirmed
                      </span>
                    </div>
                    <div className="text-right">
                      <p className="text-2xl font-black text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">
                        ₹{b.price}
                      </p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 mb-6">
                    <div className="flex items-center gap-3 bg-[#0d1520] p-3 rounded-xl border border-white/5">
                      <Calendar className="text-slate-400" size={18} />
                      <div>
                        <p className="text-xs text-slate-500 uppercase tracking-wider font-bold">Date</p>
                        <p className="text-sm font-medium text-slate-300">{formatDate(b.date)}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-3 bg-[#0d1520] p-3 rounded-xl border border-white/5">
                      <Clock className="text-slate-400" size={18} />
                      <div>
                        <p className="text-xs text-slate-500 uppercase tracking-wider font-bold">Slot</p>
                        <p className="text-sm font-medium text-slate-300">{b.slot || "N/A"}</p>
                      </div>
                    </div>
                    <div className="col-span-2 flex items-center gap-3 bg-[#0d1520] p-3 rounded-xl border border-white/5">
                      <User className="text-slate-400" size={18} />
                      <div className="flex flex-col md:flex-row md:items-center md:gap-2">
                        <p className="text-sm font-medium text-slate-300">{b.userName}</p>
                        <span className="hidden md:inline text-slate-600">•</span>
                        <p className="text-sm text-slate-500">{b.userEmail}</p>
                      </div>
                    </div>
                  </div>

                  <button
                    onClick={() => handleCancel(b.id)}
                    disabled={deletingId === b.id}
                    className="w-full flex items-center justify-center gap-2 py-3 bg-red-500/10 hover:bg-red-500/20 text-red-400 hover:text-red-300 border border-red-500/20 hover:border-red-500/40 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {deletingId === b.id ? (
                      <><Loader2 className="animate-spin" size={18} /> Cancelling...</>
                    ) : (
                      <><Trash2 size={18} /> Cancel Booking</>
                    )}
                  </button>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-64 bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl text-center p-6">
            <div className="w-16 h-16 bg-white/5 rounded-full flex items-center justify-center mb-4">
              <Calendar className="w-8 h-8 text-slate-500" />
            </div>
            <h3 className="text-xl font-bold text-white mb-2">No Bookings Found</h3>
            <p className="text-slate-400">There are currently no reservations for your turfs.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminViewBookings;
