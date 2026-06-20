import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { sendBookingConfirmation } from "../services/Api";
import { motion } from "framer-motion";
import { Banknote, Loader2, CheckCircle2 } from "lucide-react";

const PaymentPage = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    try {
      setLoading(true);

      // Simulate processing delay
      await new Promise((resolve) => setTimeout(resolve, 1500));

      // Send confirmation email
      await sendBookingConfirmation(bookingId);

      navigate("/payment-success");
    } catch (error) {
      console.error("❌ Confirmation failed:", error);
      alert("Something went wrong during confirmation. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#060a0f] flex items-center justify-center font-inter p-6 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute inset-0 z-0 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[400px] h-[400px] bg-emerald-500/10 blur-[120px] rounded-full" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[400px] h-[400px] bg-violet-600/10 blur-[120px] rounded-full" />
      </div>

      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="w-full max-w-md bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-8 shadow-2xl relative z-10 text-center"
      >
        <div className="mx-auto w-16 h-16 bg-emerald-500/20 rounded-full flex items-center justify-center mb-6">
          <Banknote size={32} className="text-emerald-400" />
        </div>

        <h2 className="text-2xl font-black text-white mb-2">Booking Confirmation</h2>
        <p className="text-slate-400 mb-6 font-mono text-sm">ID: {bookingId}</p>

        <div className="bg-[#0d1520] border border-white/5 rounded-2xl p-6 mb-8 text-left">
          <div className="flex items-center gap-3 mb-4">
            <CheckCircle2 size={20} className="text-emerald-400" />
            <h3 className="font-bold text-white">Payment Method</h3>
          </div>
          <p className="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400 text-center py-2">
            Only Cash on Spot Available
          </p>
          <p className="text-sm text-slate-400 text-center mt-2">
            Please pay at the arena counter before your game starts.
          </p>
        </div>

        <button
          onClick={handlePayment}
          disabled={loading}
          className="w-full py-4 bg-emerald-500 hover:bg-emerald-400 disabled:bg-emerald-500/50 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_30px_rgba(52,211,153,0.5)] transition-all flex items-center justify-center gap-2"
        >
          {loading ? (
            <><Loader2 className="animate-spin" size={20} /> Confirming...</>
          ) : (
            "Confirm Booking"
          )}
        </button>
      </motion.div>
    </div>
  );
};

export default PaymentPage;
