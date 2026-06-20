import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { CheckCircle, ArrowRight } from "lucide-react";

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(5);

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          navigate("/slot");
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [navigate]);

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
        className="w-full max-w-md bg-white/5 backdrop-blur-xl border border-emerald-500/30 rounded-3xl p-8 shadow-[0_0_50px_rgba(0,255,157,0.15)] relative z-10 text-center"
      >
        <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-emerald-400 to-cyan-400" />
        
        <div className="mx-auto w-24 h-24 bg-emerald-500/20 rounded-full flex items-center justify-center mb-6">
          <CheckCircle size={48} className="text-emerald-400" />
        </div>

        <h2 className="text-3xl font-black text-white mb-4">Booking Confirmed!</h2>
        
        <p className="text-slate-300 mb-8 leading-relaxed">
          Your booking is successfully recorded. Please check your email for the detailed receipt.
        </p>

        <div className="bg-[#0d1520] border border-white/5 rounded-2xl py-4 px-6 mb-6">
          <p className="text-sm text-slate-400">
            Redirecting to Arenas in <strong className="text-emerald-400 text-lg">{countdown}</strong> seconds...
          </p>
        </div>

        <button
          onClick={() => navigate("/slot")}
          className="w-full py-3.5 bg-white/5 hover:bg-emerald-500 hover:text-black text-white font-bold rounded-xl transition-all border border-white/10 hover:border-emerald-400 flex items-center justify-center gap-2"
        >
          Go to Arenas Now <ArrowRight size={18} />
        </button>
      </motion.div>
    </div>
  );
};

export default PaymentSuccess;
