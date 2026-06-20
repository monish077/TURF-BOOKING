import React, { useState, useEffect } from "react";
import { getPublicTurfs } from "../services/Api";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { MapPin, Search, ArrowRight, Loader2, IndianRupee } from "lucide-react";

const Slot = () => {
  const [allTurfs, setAllTurfs] = useState([]);
  const [filteredTurfs, setFilteredTurfs] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/");
  };

  useEffect(() => {
    const fetchTurfs = async () => {
      try {
        const response = await getPublicTurfs();
        const backendTurfs = response.data || [];

        const turfsWithImages = backendTurfs.map((turf) => ({
          id: turf.id,
          name: turf.name || "Unnamed Turf",
          location: turf.location || "Location not available",
          price: turf.pricePerHour || 0,
          image: turf.imageUrls?.[0] && turf.imageUrls[0].trim() !== "" ? turf.imageUrls[0] : "/default-turf.jpg",
        }));

        setAllTurfs(turfsWithImages);
        setFilteredTurfs(turfsWithImages);
      } catch (error) {
        console.error("Failed to load turfs:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTurfs();
  }, []);

  useEffect(() => {
    if (searchQuery.trim() === "") {
      setFilteredTurfs(allTurfs);
    } else {
      setFilteredTurfs(
        allTurfs.filter(
          (t) =>
            t.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            t.location.toLowerCase().includes(searchQuery.toLowerCase())
        )
      );
    }
  }, [searchQuery, allTurfs]);

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.1 },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 },
  };

  return (
    <div className="min-h-screen bg-[#060a0f] text-slate-100 font-inter pb-20 relative overflow-hidden">
      
      {/* Background Decor */}
      <div className="fixed inset-0 z-0 pointer-events-none">
        <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-emerald-500/5 blur-[150px] rounded-full" />
        <div className="absolute bottom-0 left-0 w-[500px] h-[500px] bg-violet-600/5 blur-[150px] rounded-full" />
      </div>

      {/* Navbar */}
      <nav className="relative z-10 flex items-center justify-between px-6 py-6 max-w-7xl mx-auto border-b border-white/5">
        <Link to="/" className="text-xl font-black tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-emerald-200">
          MARS ARENA
        </Link>
        <div className="flex items-center gap-6">
          <Link to="/" className="text-sm font-medium text-slate-400 hover:text-emerald-400 transition-colors">Home</Link>
          <Link to="/view-bookings" className="text-sm font-medium text-slate-400 hover:text-emerald-400 transition-colors">My Bookings</Link>
          <button 
            onClick={handleLogout}
            className="px-4 py-2 text-sm font-medium text-slate-300 border border-white/10 rounded-full hover:bg-white/5 hover:text-white transition-all"
          >
            Logout
          </button>
        </div>
      </nav>

      <main className="relative z-10 max-w-7xl mx-auto px-6 pt-12">
        {/* Header */}
        <motion.div 
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center max-w-2xl mx-auto mb-16"
        >
          <h1 className="text-4xl md:text-5xl font-black tracking-tight mb-4">
            Find Your <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-cyan-400">Perfect Arena</span>
          </h1>
          <p className="text-slate-400">
            Explore premium indoor & outdoor turfs with seamless booking and top-notch facilities.
          </p>
          
          {/* Search Bar */}
          <div className="mt-8 relative max-w-md mx-auto group">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-emerald-400 transition-colors" size={20} />
            <input 
              type="text" 
              placeholder="Search by name or location..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-white/5 border border-white/10 rounded-full py-3 pl-12 pr-4 text-slate-200 placeholder:text-slate-500 focus:outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all"
            />
          </div>
        </motion.div>

        {/* Turf Grid */}
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 text-emerald-400">
            <Loader2 className="animate-spin mb-4" size={40} />
            <p className="text-slate-400 font-medium">Loading premium arenas...</p>
          </div>
        ) : filteredTurfs.length > 0 ? (
          <motion.div 
            variants={containerVariants}
            initial="hidden"
            animate="visible"
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8"
          >
            {filteredTurfs.map((turf) => (
              <motion.div 
                key={turf.id}
                variants={itemVariants}
                className="group relative bg-[#0d1520] border border-white/5 rounded-[24px] overflow-hidden hover:border-emerald-500/30 hover:shadow-[0_0_30px_rgba(0,255,157,0.1)] transition-all duration-300"
              >
                {/* Image Section */}
                <div className="relative aspect-[4/3] overflow-hidden bg-slate-800">
                  <div className="absolute inset-0 bg-gradient-to-t from-[#0d1520] via-transparent to-transparent z-10 opacity-80" />
                  <img 
                    src={turf.image} 
                    alt={turf.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    onError={(e) => { e.target.src = "/default-turf.jpg"; }}
                  />
                  <div className="absolute top-4 right-4 z-20 bg-black/50 backdrop-blur-md border border-white/10 px-3 py-1.5 rounded-full flex items-center gap-1">
                    <IndianRupee size={14} className="text-emerald-400" />
                    <span className="font-bold text-sm">{turf.price}</span>
                    <span className="text-xs text-slate-400">/hr</span>
                  </div>
                </div>

                {/* Content Section */}
                <div className="p-6 relative z-20">
                  <h3 className="text-xl font-bold mb-2 group-hover:text-emerald-400 transition-colors line-clamp-1">{turf.name}</h3>
                  <div className="flex items-start gap-2 text-slate-400 text-sm mb-6 h-10">
                    <MapPin size={16} className="shrink-0 mt-0.5 text-slate-500" />
                    <span className="line-clamp-2">{turf.location}</span>
                  </div>

                  <Link to={`/turfs/${turf.id}`} className="block">
                    <button className="w-full py-3 bg-white/5 hover:bg-emerald-500 hover:text-black text-white font-semibold rounded-xl border border-white/10 hover:border-emerald-400 transition-all flex items-center justify-center gap-2 group/btn">
                      Book Now
                      <ArrowRight size={18} className="group-hover/btn:translate-x-1 transition-transform" />
                    </button>
                  </Link>
                </div>
              </motion.div>
            ))}
          </motion.div>
        ) : (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-20"
          >
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-white/5 mb-4">
              <Search className="text-slate-500" size={24} />
            </div>
            <h3 className="text-xl font-bold mb-2">No turfs found</h3>
            <p className="text-slate-400">Try adjusting your search query.</p>
          </motion.div>
        )}
      </main>
    </div>
  );
};

export default Slot;
