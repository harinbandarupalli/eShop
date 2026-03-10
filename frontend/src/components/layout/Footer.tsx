import { ShoppingBag, Mail, Phone, MapPin, Twitter, Instagram, Youtube } from 'lucide-react';

export const Footer = () => {
  return (
    <footer className="bg-slate-900 text-white py-16 px-4 md:px-10 border-t-8 border-primary relative z-10 w-full">
      <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-12">
        <div className="col-span-1 md:col-span-1">
          <div className="flex items-center gap-2 text-primary mb-6">
            <ShoppingBag className="w-8 h-8 font-bold" />
            <h2 className="text-2xl font-extrabold tracking-tighter">BilboBag.in</h2>
          </div>
          <p className="text-slate-400 mb-8 font-medium">Equipping the modern nomad with bold, high-performance carry gear since 2024.</p>
          <div className="flex gap-4">
            <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-primary transition-colors">
              <Twitter className="w-4 h-4" />
            </a>
            <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-primary transition-colors">
              <Instagram className="w-4 h-4" />
            </a>
            <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-primary transition-colors">
              <Youtube className="w-4 h-4" />
            </a>
          </div>
        </div>

        <div>
          <h4 className="text-lg font-black uppercase mb-6 italic">Support</h4>
          <ul className="space-y-4 text-slate-400 font-semibold">
            <li><a href="#" className="hover:text-primary transition-colors">Track Order</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Shipping Policy</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Returns & Exchanges</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Warranty</a></li>
          </ul>
        </div>

        <div>
          <h4 className="text-lg font-black uppercase mb-6 italic">Company</h4>
          <ul className="space-y-4 text-slate-400 font-semibold">
            <li><a href="#" className="hover:text-primary transition-colors">Our Story</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Sustainability</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Careers</a></li>
            <li><a href="#" className="hover:text-primary transition-colors">Press</a></li>
          </ul>
        </div>

        <div>
          <h4 className="text-lg font-black uppercase mb-6 italic">Contact Us</h4>
          <div className="space-y-4 text-slate-400 font-semibold">
            <p className="flex items-center gap-3">
              <Mail className="w-5 h-5 text-primary" />
              hello@bilbobag.in
            </p>
            <p className="flex items-center gap-3">
              <Phone className="w-5 h-5 text-primary" />
              +1 (800) BAG-BOLD
            </p>
            <p className="flex items-start gap-3">
              <MapPin className="w-5 h-5 text-primary shrink-0" />
              123 Adventure Way<br />Mountain View, CA 94043
            </p>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto border-t border-slate-800 mt-16 pt-8 flex flex-col md:flex-row justify-between items-center gap-4 text-slate-500 text-sm font-bold">
        <p>© 2024 BILBOBAG.IN ALL RIGHTS RESERVED.</p>
        <div className="flex gap-8">
          <a href="#" className="hover:text-white">Privacy Policy</a>
          <a href="#" className="hover:text-white">Terms of Service</a>
        </div>
      </div>
    </footer>
  );
};
