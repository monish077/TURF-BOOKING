import React, { useState } from "react";
import axiosInstance from "../services/axiosInstance";
import { Link } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { Mail, ArrowLeft, ArrowRight, CheckCircle2, AlertCircle, Loader2 } from "lucide-react";
import turfImage from "../assets/images/field.jpg";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    setLoading(true);

    try {
      await axiosInstance.post("/users/forgot-password", { email: email.trim() });
      setMessage("Reset link sent! Please check your email inbox.");
      setSent(true);
    } catch (err) {
      console.error("❌ Forgot password error:", err);
      setError(err.response?.data?.error || "Error sending reset link. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#060a0f] flex font-inter">

      {/* Left Side - Image & Branding (60%) */}
      <div className="hidden lg:flex lg:w-[60%] relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-[#060a0f]/80 via-[#060a0f]/30 to-transparent z-10" />
        <div className="absolute inset-0 bg-gradient-to-t from-[#060a0f] via-transparent to-transparent z-10" />
        <img src={turfImage} alt="Premium Turf" className="w-full h-full object-cover" />

        {/* Top Logo */}
        <div className="absolute top-10 left-12 z-20">
          <Link to="/" className="text-2xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">
            MARS ARENA
          </Link>
        </div>

        {/* Bottom Tagline */}
        <div className="absolute bottom-16 left-12 z-20 max-w-lg">
          <h2 className="text-4xl font-black text-white mb-4 leading-tight">
            Book Premium Turf<br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">
              Grounds Instantly
            </span>
          </h2>
          <p className="text-slate-400 text-lg">
            Play. Compete. Win. — Your arena awaits.
          </p>
        </div>
      </div>

      {/* Right Side - Form (40%) */}
      <div className="w-full lg:w-[40%] flex items-center justify-center p-6 sm:p-12 relative overflow-hidden">
        {/* Mobile Glow */}
        <div className="lg:hidden absolute top-[-20%] right-[-10%] w-[500px] h-[500px] bg-emerald-500/10 blur-[120px] rounded-full pointer-events-none" />
        <div className="absolute bottom-[-20%] left-[-10%] w-[400px] h-[400px] bg-violet-600/5 blur-[120px] rounded-full pointer-events-none" />

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="w-full max-w-md relative z-10"
        >
          {/* Mobile Logo */}
          <div className="lg:hidden mb-10 text-center">
            <Link to="/" className="text-3xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">
              MARS ARENA
            </Link>
            <p className="text-slate-500 text-sm mt-1">Play. Compete. Win.</p>
          </div>

          {/* Header */}
          <div className="mb-10 text-center lg:text-left">
            <h1 className="text-3xl font-black text-white mb-2 tracking-tight">Forgot Password?</h1>
            <p className="text-slate-400">
              No worries — enter your email and we'll send you a reset link.
            </p>
          </div>

          {/* Success State */}
          <AnimatePresence>
            {sent ? (
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-emerald-500/10 border border-emerald-500/30 rounded-2xl p-8 text-center"
              >
                <div className="mx-auto w-16 h-16 bg-emerald-500/20 rounded-full flex items-center justify-center mb-4">
                  <CheckCircle2 size={32} className="text-emerald-400" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Check your inbox!</h3>
                <p className="text-slate-400 mb-6 text-sm">{message}</p>
                <Link
                  to="/login"
                  className="inline-flex items-center gap-2 text-emerald-400 hover:text-emerald-300 font-medium transition-colors"
                >
                  <ArrowLeft size={16} /> Back to Login
                </Link>
              </motion.div>
            ) : (
              <motion.div initial={{ opacity: 1 }} exit={{ opacity: 0 }}>
                {/* Error */}
                {error && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: "auto" }}
                    className="bg-red-500/10 border border-red-500/50 text-red-400 px-4 py-3 rounded-xl flex items-start gap-3 mb-6"
                  >
                    <AlertCircle size={20} className="shrink-0 mt-0.5" />
                    <p className="text-sm">{error}</p>
                  </motion.div>
                )}

                {/* Form */}
                <form onSubmit={handleSubmit} className="space-y-5">
                  <div>
                    <label className="block text-sm font-medium text-slate-300 mb-2">
                      Email Address
                    </label>
                    <div className="relative group">
                      <Mail
                        className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors"
                        size={20}
                      />
                      <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        autoComplete="email"
                        placeholder="Enter your registered email"
                        className="w-full bg-white/5 border border-white/10 rounded-xl py-3.5 pl-12 pr-4 text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-3.5 mt-2 bg-emerald-500 hover:bg-emerald-400 disabled:bg-emerald-500/50 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_30px_rgba(52,211,153,0.5)] transition-all flex items-center justify-center gap-2 group"
                  >
                    {loading ? (
                      <><Loader2 className="animate-spin" size={20} /> Sending Link...</>
                    ) : (
                      <>Send Reset Link <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" /></>
                    )}
                  </button>
                </form>

                <p className="mt-8 text-center text-slate-400">
                  Remembered your password?{" "}
                  <Link to="/login" className="text-emerald-400 font-medium hover:underline">
                    Back to Login
                  </Link>
                </p>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>
      </div>
    </div>
  );
}

export default ForgotPassword;
