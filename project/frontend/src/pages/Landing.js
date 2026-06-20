import React from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { CalendarCheck, Shield, Zap, ArrowRight } from "lucide-react";
import turfImage from "../assets/images/hero_turf.png";

const Landing = () => {
  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-100 overflow-hidden font-inter selection:bg-emerald-500/30 selection:text-emerald-400">
      
      {/* Dynamic Background with Glows */}
      <div className="absolute inset-0 z-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] rounded-full bg-emerald-500/10 blur-[120px]" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] rounded-full bg-violet-600/10 blur-[120px]" />
        <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20 mix-blend-overlay" />
      </div>

      {/* Navbar */}
      <nav className="relative z-10 flex items-center justify-between px-6 py-6 max-w-7xl mx-auto">
        <motion.div 
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-2xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-emerald-200"
        >
          MARS ARENA
        </motion.div>
        <motion.div 
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="flex gap-4"
        >
          <Link to="/login" className="px-5 py-2.5 text-sm font-medium text-slate-300 hover:text-white transition-colors">
            Sign In
          </Link>
          <Link to="/register" className="px-5 py-2.5 text-sm font-semibold text-black bg-emerald-400 rounded-full hover:bg-emerald-300 transition-all shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_25px_rgba(52,211,153,0.5)]">
            Get Started
          </Link>
        </motion.div>
      </nav>

      {/* Hero Section */}
      <main className="relative z-10 max-w-7xl mx-auto px-6 pt-20 pb-32 flex flex-col lg:flex-row items-center gap-16">
        
        {/* Left Content */}
        <div className="flex-1 text-center lg:text-left">
          <motion.div 
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm font-medium mb-8"
          >
            <Zap size={16} className="fill-emerald-400" />
            <span>Premium Turf Booking Experience</span>
          </motion.div>

          <motion.h1 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="text-5xl lg:text-7xl font-black tracking-tight leading-[1.1] mb-6"
          >
            Your Game. <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">Your Time.</span> <br />
            Your Arena.
          </motion.h1>

          <motion.p 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="text-lg text-slate-400 mb-10 max-w-xl mx-auto lg:mx-0 leading-relaxed"
          >
            Effortlessly book your favorite turf and enjoy a seamless play experience. 
            From football to cricket — we've got your game covered with top-notch facilities.
          </motion.p>

          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="flex flex-col sm:flex-row items-center gap-4 justify-center lg:justify-start"
          >
            <Link to="/login" className="group w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-4 bg-emerald-500 text-black font-bold rounded-full hover:bg-emerald-400 transition-all">
              Book a Turf Now
              <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
            </Link>
            <Link to="/register" className="w-full sm:w-auto px-8 py-4 bg-white/5 border border-white/10 text-white font-semibold rounded-full hover:bg-white/10 transition-all text-center">
              Create an Account
            </Link>
          </motion.div>

          {/* Stats */}
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.6 }}
            className="mt-16 pt-8 border-t border-white/5 grid grid-cols-3 gap-6"
          >
            <div>
              <div className="text-3xl font-black text-white">20+</div>
              <div className="text-sm text-slate-400 mt-1">Premium Turfs</div>
            </div>
            <div>
              <div className="text-3xl font-black text-white">5K+</div>
              <div className="text-sm text-slate-400 mt-1">Happy Players</div>
            </div>
            <div>
              <div className="text-3xl font-black text-white">24/7</div>
              <div className="text-sm text-slate-400 mt-1">Booking Support</div>
            </div>
          </motion.div>
        </div>

        {/* Right Image/Visuals */}
        <motion.div 
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
          className="flex-1 relative w-full max-w-lg lg:max-w-none aspect-[4/5] lg:aspect-square"
        >
          {/* Glass Card Floating 1 */}
          <motion.div 
            animate={{ y: [0, -10, 0] }}
            transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
            className="absolute top-10 -left-10 z-20 p-4 bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl flex items-center gap-4"
          >
            <div className="w-12 h-12 rounded-full bg-emerald-500/20 flex items-center justify-center">
              <CalendarCheck className="text-emerald-400" />
            </div>
            <div>
              <div className="text-sm font-bold text-white">Instant Booking</div>
              <div className="text-xs text-slate-400">Zero wait time</div>
            </div>
          </motion.div>

          {/* Glass Card Floating 2 */}
          <motion.div 
            animate={{ y: [0, 15, 0] }}
            transition={{ duration: 5, repeat: Infinity, ease: "easeInOut", delay: 1 }}
            className="absolute bottom-20 -right-4 z-20 p-4 bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl flex items-center gap-4"
          >
            <div className="w-12 h-12 rounded-full bg-violet-500/20 flex items-center justify-center">
              <Shield className="text-violet-400" />
            </div>
            <div>
              <div className="text-sm font-bold text-white">Secure Payments</div>
              <div className="text-xs text-slate-400">100% safe & protected</div>
            </div>
          </motion.div>

          {/* Main Image */}
          <div className="relative w-full h-full rounded-[2rem] overflow-hidden border border-white/10 shadow-2xl group">
            <div className="absolute inset-0 bg-gradient-to-t from-[#060a0f] via-transparent to-transparent z-10" />
            <img 
              src={turfImage} 
              alt="Premium Turf Field" 
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700"
            />
          </div>
        </motion.div>

      </main>
    </div>
  );
};

export default Landing;
