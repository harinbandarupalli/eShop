import { ArrowRight, Star, Shield, Zap, Globe } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { Link } from 'react-router-dom';

export const HomePage = () => {
  return (
    <div className="flex flex-col gap-24 py-8 pb-32">
      {/* Hero Section */}
      <section className="relative rounded-[2rem] bg-slate-900 overflow-hidden min-h-[75vh] flex items-center shadow-2xl">
        {/* Background Image */}
        <div className="absolute inset-0 z-0">
          <img 
            src="https://images.unsplash.com/photo-1469041797191-50ace28483c3?q=80&w=2000&auto=format&fit=crop" 
            alt="Hero Background" 
            className="w-full h-full object-cover object-center opacity-50 block mix-blend-overlay"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-slate-950 via-slate-900/80 to-transparent" />
        </div>
        
        <div className="relative z-10 w-full max-w-7xl mx-auto px-8 md:px-12 flex flex-col items-start gap-8">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 backdrop-blur-md border border-white/20 text-white font-medium text-sm tracking-wide shadow-sm">
            <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
            New Spring Collection
          </div>
          
          <h1 className="text-6xl md:text-7xl lg:text-8xl font-black text-white leading-[1.05] tracking-tighter">
            CARRY <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-orange-400">
              WITH PURPOSE.
            </span>
          </h1>
          
          <p className="text-lg md:text-2xl text-slate-300 max-w-xl font-medium leading-relaxed">
            Engineered for the modern nomad. Waterproof, durable, and unapologetically bold. Built to outlast your journey.
          </p>
          
          <div className="flex flex-wrap items-center gap-4 mt-6">
            <Link to="/category/daypacks">
              <Button size="lg" className="h-14 px-8 text-base shadow-xl shadow-primary/20 gap-3 hover:scale-105 transition-transform">
                Shop New Arrivals
                <ArrowRight className="w-5 h-5" />
              </Button>
            </Link>
            <Button variant="outline" size="lg" className="h-14 px-8 text-base border-white/30 text-white hover:bg-white hover:text-slate-900 backdrop-blur-sm transition-all hover:scale-105">
              Watch The Film
            </Button>
          </div>
        </div>
      </section>

      {/* Featured Categories */}
      <section className="flex flex-col gap-10">
        <div className="flex items-end justify-between px-2">
          <div>
            <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tight" style={{ color: 'var(--text-primary)' }}>Explore Gear</h2>
            <p className="mt-2 font-medium text-lg md:text-xl" style={{ color: 'var(--text-muted)' }}>Find the perfect companion for your next adventure.</p>
          </div>
          <Link to="/category/daypacks" className="hidden md:flex items-center gap-2 text-primary font-bold hover:text-primary/80 transition-colors text-lg">
            View All <ArrowRight className="w-6 h-6" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            { 
              title: "Daypacks", 
              desc: "For the daily grind.", 
              img: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=800&auto=format&fit=crop"
            },
            { 
              title: "Travel", 
              desc: "Built for across the world.", 
               img: "https://images.unsplash.com/photo-1504280613271-e7f0b58e72ef?q=80&w=800&auto=format&fit=crop"
            },
            { 
              title: "Accessories", 
              desc: "The essential additions.", 
               img: "https://images.unsplash.com/photo-1628126235206-526019f74cbc?q=80&w=800&auto=format&fit=crop"
            }
          ].map((cat, i) => (
            <Link 
              to={`/category/${cat.title.toLowerCase()}`} 
              key={i} 
              className="group relative rounded-[2rem] overflow-hidden aspect-[4/5] bg-slate-900 block shadow-sm hover:shadow-2xl hover:shadow-primary/20 transition-all duration-500"
            >
              <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-900/20 to-slate-900/10 z-10 transition-opacity duration-500 group-hover:opacity-100 opacity-80" />
              <img 
                src={cat.img} 
                alt={cat.title} 
                className="absolute inset-0 w-full h-full object-cover transition-transform duration-1000 group-hover:scale-110 opacity-80 group-hover:opacity-100" 
              />
              <div className="absolute bottom-0 left-0 p-8 z-20 w-full flex flex-col justify-end h-full">
                <div className="transform transition-transform duration-500 translate-y-6 group-hover:translate-y-0">
                  <h3 className="text-3xl font-black text-white uppercase tracking-tight mb-2">
                    {cat.title}
                  </h3>
                  <p className="text-slate-300 font-medium text-lg mb-6 opacity-0 group-hover:opacity-100 transition-opacity duration-500 delay-100">
                    {cat.desc}
                  </p>
                  <div className="inline-flex items-center gap-2 text-white font-bold bg-white/10 backdrop-blur-md px-6 py-3 rounded-full hover:bg-primary border border-white/20 transition-all duration-300 transform opacity-0 group-hover:opacity-100 shadow-lg">
                    Shop Now <ArrowRight className="w-5 h-5" />
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </section>

      {/* Trust Fall Section */}
      <section className="overflow-hidden rounded-[3rem] p-12 md:p-16 lg:p-24 border shadow-sm relative" style={{ backgroundColor: 'var(--surface)', borderColor: 'var(--border-color)' }}>
        <div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />
        <div className="absolute bottom-0 left-0 w-64 h-64 bg-primary/5 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2" />
        
        <div className="relative z-10 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12">
          {[
            { icon: Shield, title: "Lifetime Warranty", desc: "Built to last. We stand behind our gear forever." },
            { icon: Globe, title: "Global Shipping", desc: "Free worldwide shipping on original orders over $150." },
            { icon: Zap, title: "Next-Day Delivery", desc: "Select options available for ultra-fast gear deployment." },
            { icon: Star, title: "Premium Materials", desc: "Weatherproof fabrics and aerospace-grade structural hardware." }
          ].map((feature, i) => (
            <div key={i} className="flex flex-col items-start gap-5">
              <div className="w-16 h-16 rounded-2xl shadow-sm border flex items-center justify-center text-primary transition-transform" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)' }}>
                <feature.icon className="w-8 h-8" />
              </div>
              <h4 className="text-2xl font-black uppercase tracking-tight" style={{ color: 'var(--text-primary)' }}>{feature.title}</h4>
              <p className="font-medium text-lg leading-relaxed" style={{ color: 'var(--text-muted)' }}>{feature.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA Section */}
      <section className="rounded-[3rem] bg-slate-900 p-12 md:p-20 text-center flex flex-col items-center justify-center relative overflow-hidden">
        <div className="absolute inset-0 z-0 opacity-20 bg-[url('https://images.unsplash.com/photo-1622560026749-93113149bf04?q=80&w=2000&auto=format&fit=crop')] bg-cover bg-center mix-blend-luminosity" />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/90 to-slate-900/80" />
        
        <div className="relative z-10 flex flex-col items-center max-w-2xl">
          <h2 className="text-4xl md:text-6xl font-black text-white uppercase tracking-tighter mb-6">Join The Club</h2>
          <p className="text-xl text-slate-300 font-medium mb-10">Get 10% off your first order, access to exclusive drops, and more.</p>
          <div className="relative w-full max-w-md flex items-center">
            <input 
              type="email" 
              placeholder="Enter your email" 
              className="w-full h-14 pl-6 pr-32 rounded-full bg-white/10 border border-white/20 text-white placeholder:text-slate-400 focus:outline-none focus:border-primary focus:bg-white/20 transition-all font-medium"
            />
            <Button className="absolute right-1 top-1 bottom-1 rounded-full px-6">Subscribe</Button>
          </div>
        </div>
      </section>
    </div>
  );
};
