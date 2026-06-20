import React, { useState } from "react";
import { loginUser } from "../services/Api";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { Mail, Lock, Eye, EyeOff, ArrowRight, AlertCircle, Loader2, Zap } from "lucide-react";
import turfImage from "../assets/images/auth_turf.png";

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await loginUser({ email: email.trim(), password });

      if (response.status === 200 && response.data) {
        const { token, role, email: userEmail } = response.data;
        sessionStorage.setItem("token", token);
        sessionStorage.setItem("email", userEmail);
        sessionStorage.setItem("role", role);

        if (role === "ADMIN") navigate("/admin/dashboard");
        else if (role === "USER") navigate("/slot");
        else setError("Unknown role. Please contact support.");
      } else {
        setError("Invalid email or password.");
      }
    } catch (err) {
      setError(err.response?.data?.error || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#060a0f] flex font-inter">

      {/* ─── Left Panel – 60% Image + Centered Text ─── */}
      <div className="hidden lg:flex lg:w-[60%] relative overflow-hidden">

        {/* Image */}
        <img
          src={turfImage}
          alt="Premium Turf"
          className="absolute inset-0 w-full h-full object-cover object-center"
        />

        {/* Dark overlays */}
        <div className="absolute inset-0 bg-gradient-to-r from-[#060a0f]/85 via-[#060a0f]/50 to-[#060a0f]/10" />
        <div className="absolute inset-0 bg-gradient-to-t from-[#060a0f]/90 via-transparent to-[#060a0f]/30" />

        {/* ── Centered Branding Content ── */}
        <div className="relative z-20 flex flex-col justify-center px-16 py-12 h-full">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-500/15 border border-emerald-500/30 text-emerald-400 text-sm font-medium mb-6 w-fit">
            <Zap size={14} className="fill-emerald-400" />
            <span>Premium Turf</span>
          </div>

          {/* Brand Name */}
          <Link
            to="/"
            className="text-5xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400 mb-6 block leading-none"
          >
            MARS ARENA
          </Link>

          {/* Main Heading */}
          <h2 className="text-4xl font-black text-white leading-tight mb-4">
            Book Premium Turf<br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-300">
              Grounds Instantly
            </span>
          </h2>

          {/* Tagline */}
          <p className="text-slate-400 text-lg leading-relaxed max-w-sm">
            Welcome back. Your arena awaits.
          </p>

          {/* Divider + Stats */}
          <div className="mt-12 pt-8 border-t border-white/10 grid grid-cols-3 gap-6 max-w-sm">
            <div>
              <div className="text-2xl font-black text-white">20+</div>
              <div className="text-xs text-slate-500 mt-0.5">Premium Turfs</div>
            </div>
            <div>
              <div className="text-2xl font-black text-white">5K+</div>
              <div className="text-xs text-slate-500 mt-0.5">Happy Players</div>
            </div>
            <div>
              <div className="text-2xl font-black text-white">24/7</div>
              <div className="text-xs text-slate-500 mt-0.5">Support</div>
            </div>
          </div>
        </div>
      </div>

      {/* ─── Right Panel – 40% Form ─── */}
      <div className="w-full lg:w-[40%] flex items-center justify-center p-6 sm:p-12 relative overflow-hidden">
        {/* Mobile glows */}
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

          <div className="mb-10">
            <h1 className="text-3xl font-black text-white mb-2 tracking-tight">Welcome back</h1>
            <p className="text-slate-400">Sign in to book your arena</p>
          </div>

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

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Email Address</label>
              <div className="relative group">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors" size={20} />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  autoComplete="email"
                  placeholder="Enter your email"
                  className="w-full bg-white/5 border border-white/10 rounded-xl py-3.5 pl-12 pr-4 text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-sm font-medium text-slate-300">Password</label>
                <Link to="/forgot-password" className="text-sm text-emerald-400 hover:text-emerald-300 hover:underline transition-colors">
                  Forgot password?
                </Link>
              </div>
              <div className="relative group">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors" size={20} />
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  autoComplete="current-password"
                  placeholder="Enter your password"
                  className="w-full bg-white/5 border border-white/10 rounded-xl py-3.5 pl-12 pr-12 text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-emerald-400 transition-colors"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3.5 mt-2 bg-emerald-500 hover:bg-emerald-400 disabled:bg-emerald-500/50 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_30px_rgba(52,211,153,0.5)] transition-all flex items-center justify-center gap-2 group"
            >
              {loading ? (
                <><Loader2 className="animate-spin" size={20} /> Signing in...</>
              ) : (
                <>Sign In <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" /></>
              )}
            </button>
          </form>

          <p className="mt-8 text-center text-slate-400">
            New to Mars Arena?{" "}
            <Link to="/register" className="text-emerald-400 font-medium hover:underline">
              Create an account
            </Link>
          </p>
        </motion.div>
      </div>
    </div>
  );
}

export default Login;
