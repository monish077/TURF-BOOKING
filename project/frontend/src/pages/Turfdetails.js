import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axiosInstance from "../api/axiosConfig"; 
import { motion, AnimatePresence } from "framer-motion";
import { ArrowLeft, MapPin, IndianRupee, Clock, CheckCircle2, ChevronLeft, ChevronRight } from "lucide-react";

const TurfDetails = () => {
  const { id } = useParams();
  const [turf, setTurf] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentImageIdx, setCurrentImageIdx] = useState(0);

  useEffect(() => {
    const fetchTurf = async () => {
      try {
        const token = sessionStorage.getItem("token");
        const response = await axiosInstance.get(`/turfs/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setTurf(response.data);
      } catch (error) {
        console.error("Error fetching turf:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchTurf();
  }, [id]);

  const nextImage = () => {
    if (turf?.imageUrls?.length > 1) {
      setCurrentImageIdx((prev) => (prev === turf.imageUrls.length - 1 ? 0 : prev + 1));
    }
  };

  const prevImage = () => {
    if (turf?.imageUrls?.length > 1) {
      setCurrentImageIdx((prev) => (prev === 0 ? turf.imageUrls.length - 1 : prev - 1));
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#060a0f] flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-emerald-500"></div>
      </div>
    );
  }

  if (!turf) {
    return (
      <div className="min-h-screen bg-[#060a0f] text-white flex flex-col items-center justify-center">
        <h2 className="text-3xl font-bold mb-4">Turf Not Found</h2>
        <Link to="/slot" className="text-emerald-400 hover:underline flex items-center gap-2">
          <ArrowLeft size={16} /> Back to Arenas
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-100 font-inter pb-20 pt-8 relative overflow-hidden">
      {/* Background Decor */}
      <div className="fixed inset-0 z-0 pointer-events-none">
        <div className="absolute top-[-20%] right-[-10%] w-[600px] h-[600px] bg-emerald-500/5 blur-[150px] rounded-full" />
        <div className="absolute bottom-[-10%] left-[-10%] w-[500px] h-[500px] bg-violet-600/5 blur-[150px] rounded-full" />
      </div>

      <div className="max-w-6xl mx-auto px-6 relative z-10">
        <Link to="/slot" className="inline-flex items-center gap-2 text-slate-400 hover:text-emerald-400 transition-colors mb-8">
          <ArrowLeft size={18} />
          <span>Back to Arenas</span>
        </Link>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Left: Image Carousel */}
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="relative bg-slate-900 rounded-3xl overflow-hidden border border-white/10 aspect-[4/3] shadow-2xl"
          >
            {turf.imageUrls && turf.imageUrls.length > 0 ? (
              <>
                <AnimatePresence mode="wait">
                  <motion.img
                    key={currentImageIdx}
                    src={turf.imageUrls[currentImageIdx]}
                    alt={turf.name}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.3 }}
                    className="w-full h-full object-cover"
                  />
                </AnimatePresence>
                
                {turf.imageUrls.length > 1 && (
                  <>
                    <button 
                      onClick={prevImage}
                      className="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-black/40 backdrop-blur-md border border-white/10 flex items-center justify-center text-white hover:bg-black/60 transition-all"
                    >
                      <ChevronLeft size={24} />
                    </button>
                    <button 
                      onClick={nextImage}
                      className="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-black/40 backdrop-blur-md border border-white/10 flex items-center justify-center text-white hover:bg-black/60 transition-all"
                    >
                      <ChevronRight size={24} />
                    </button>
                    <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
                      {turf.imageUrls.map((_, idx) => (
                        <div 
                          key={idx} 
                          className={`w-2 h-2 rounded-full transition-all ${idx === currentImageIdx ? "bg-emerald-400 w-6" : "bg-white/40"}`}
                        />
                      ))}
                    </div>
                  </>
                )}
              </>
            ) : (
              <div className="w-full h-full flex items-center justify-center text-slate-500">
                No images available
              </div>
            )}
          </motion.div>

          {/* Right: Details & Action */}
          <motion.div 
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            className="flex flex-col"
          >
            <h1 className="text-4xl md:text-5xl font-black tracking-tight mb-4">{turf.name}</h1>
            
            <div className="flex items-center gap-2 text-slate-400 mb-6">
              <MapPin size={18} className="text-emerald-400" />
              <span>{turf.location}</span>
            </div>

            <div className="flex items-center gap-3 p-4 rounded-2xl bg-white/5 border border-white/10 w-fit mb-8">
              <div className="w-10 h-10 rounded-full bg-emerald-500/20 flex items-center justify-center">
                <IndianRupee size={20} className="text-emerald-400" />
              </div>
              <div>
                <div className="text-xs text-slate-400 font-medium uppercase tracking-wider">Price per hour</div>
                <div className="text-2xl font-bold text-white">₹{turf.pricePerHour}</div>
              </div>
            </div>

            <div className="mb-8">
              <h3 className="text-lg font-bold mb-3 flex items-center gap-2">
                <span className="w-8 h-[2px] bg-emerald-500 rounded-full"></span>
                About this Arena
              </h3>
              <p className="text-slate-300 leading-relaxed">{turf.description || "No description provided."}</p>
            </div>

            <div className="grid grid-cols-2 gap-8 mb-10">
              <div>
                <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                  <Clock size={18} className="text-emerald-400" />
                  Available Slots
                </h3>
                <ul className="space-y-2">
                  {turf.availableSlots?.split(",").map((slot, idx) => (
                    <li key={idx} className="flex items-center gap-2 text-slate-300 text-sm">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-500/50" />
                      {slot.trim()}
                    </li>
                  ))}
                </ul>
              </div>
              
              <div>
                <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                  <CheckCircle2 size={18} className="text-violet-400" />
                  Facilities
                </h3>
                <ul className="space-y-2">
                  {turf.facilities?.split(",").map((facility, idx) => (
                    <li key={idx} className="flex items-center gap-2 text-slate-300 text-sm">
                      <span className="w-1.5 h-1.5 rounded-full bg-violet-500/50" />
                      {facility.trim()}
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div className="mt-auto">
              <Link to={`/book/${turf.id}`}>
                <button className="w-full py-4 bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-lg rounded-full shadow-[0_0_20px_rgba(52,211,153,0.3)] hover:shadow-[0_0_30px_rgba(52,211,153,0.5)] transition-all transform hover:-translate-y-1">
                  Book This Arena Now
                </button>
              </Link>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default TurfDetails;
