import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";
import { motion, AnimatePresence } from "framer-motion";
import { ArrowLeft, Calendar, Clock, IndianRupee, Trash2, Loader2, CalendarX2 } from "lucide-react";

const ViewBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);

  const getTokenConfig = () => ({
    headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` }
  });

  const fetchBookings = useCallback(async () => {
    const userEmail = sessionStorage.getItem("email");
    if (!userEmail) {
      setLoading(false);
      return;
    }

    try {
      const res = await axiosInstance.get(`/bookings/user/${userEmail}`, getTokenConfig());
      setBookings(res.data);
    } catch (err) {
      console.error("❌ Failed to fetch bookings:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleCancel = async (id) => {
    if (window.confirm("Are you sure you want to cancel this booking?")) {
      setCancellingId(id);
      try {
        await axiosInstance.delete(`/bookings/${id}`, getTokenConfig());
        setBookings(prev => prev.filter(b => b.id !== id));
      } catch (err) {
        console.error("❌ Failed to cancel booking:", err);
        alert("Failed to cancel booking. Please try again.");
      } finally {
        setCancellingId(null);
      }
    }
  };

  useEffect(() => {
    fetchBookings();
  }, [fetchBookings]);

  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-100 font-inter py-12 px-6 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
        <div className="absolute top-[-10%] right-[-10%] w-[500px] h-[500px] bg-emerald-500/10 blur-[150px] rounded-full" />
      </div>

      <div className="max-w-4xl mx-auto relative z-10">
        <Link to="/slot" className="inline-flex items-center gap-2 text-slate-400 hover:text-emerald-400 transition-colors mb-8">
          <ArrowLeft size={18} />
          <span>Back to Arenas</span>
        </Link>

        <div className="flex items-center justify-between mb-10">
          <div>
            <h1 className="text-3xl md:text-4xl font-black text-white mb-2">My Bookings</h1>
            <p className="text-slate-400">Manage your upcoming turf sessions</p>
          </div>
          <div className="hidden sm:flex items-center gap-2 px-4 py-2 bg-white/5 border border-white/10 rounded-full">
            <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            <span className="text-sm font-medium text-emerald-400">{bookings.length} Active Bookings</span>
          </div>
        </div>

        {loading ? (
          <div className="flex flex-col items-center justify-center py-20">
            <Loader2 className="animate-spin text-emerald-400 mb-4" size={40} />
            <p className="text-slate-400">Loading your schedule...</p>
          </div>
        ) : bookings.length > 0 ? (
          <div className="space-y-4">
            <AnimatePresence>
              {bookings.map((b) => (
                <motion.div
                  key={b.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, x: -50, transition: { duration: 0.2 } }}
                  className="bg-[#0d1520] border border-white/10 rounded-2xl p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 hover:border-emerald-500/30 transition-all hover:shadow-[0_0_30px_rgba(0,255,157,0.05)]"
                >
                  <div className="flex-1 w-full">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="text-xl font-bold text-white">{b.turfName}</h3>
                      <span className="px-2.5 py-1 text-xs font-semibold rounded-md bg-emerald-500/20 text-emerald-400 border border-emerald-500/20">
                        Confirmed
                      </span>
                    </div>
                    
                    <div className="flex flex-wrap items-center gap-x-6 gap-y-2 mt-4 text-sm text-slate-400">
                      <div className="flex items-center gap-2">
                        <Calendar size={16} className="text-slate-500" />
                        <span className="font-medium text-slate-300">{b.date}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Clock size={16} className="text-slate-500" />
                        <span className="font-medium text-slate-300">{b.slot}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <IndianRupee size={16} className="text-slate-500" />
                        <span className="font-medium text-emerald-400">{b.price}</span>
                      </div>
                    </div>
                  </div>

                  <button
                    onClick={() => handleCancel(b.id)}
                    disabled={cancellingId === b.id}
                    className="w-full sm:w-auto px-6 py-3 bg-red-500/10 hover:bg-red-500/20 text-red-400 font-semibold rounded-xl border border-red-500/20 hover:border-red-500/50 transition-all flex items-center justify-center gap-2"
                  >
                    {cancellingId === b.id ? (
                      <><Loader2 className="animate-spin" size={18} /> Cancelling</>
                    ) : (
                      <><Trash2 size={18} /> Cancel</>
                    )}
                  </button>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        ) : (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-24 bg-[#0d1520] border border-white/5 rounded-3xl"
          >
            <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-white/5 mb-6">
              <CalendarX2 className="text-slate-500" size={32} />
            </div>
            <h3 className="text-2xl font-bold text-white mb-2">No active bookings</h3>
            <p className="text-slate-400 mb-8 max-w-md mx-auto">You haven't booked any arenas yet. Ready to get in the game?</p>
            <Link to="/slot" className="inline-flex items-center gap-2 px-8 py-3.5 bg-emerald-500 hover:bg-emerald-400 text-black font-bold rounded-full transition-all shadow-[0_0_20px_rgba(52,211,153,0.3)]">
              Browse Arenas
            </Link>
          </motion.div>
        )}
      </div>
    </div>
  );
};

export default ViewBookings;
