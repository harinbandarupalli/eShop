import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Footer } from './Footer';
import { CartDrawer } from './CartDrawer';

export const MainLayout = () => {
  return (
    <div className="relative flex min-h-screen w-full flex-col">
      {/* Background pattern */}
      <div 
        className="fixed inset-0 pointer-events-none z-[-1]"
        style={{
          backgroundColor: '#ffffff',
          backgroundImage: 'radial-gradient(#FF9E00 0.5px, transparent 0.5px), radial-gradient(#FF9E00 0.5px, #ffffff 0.5px)',
          backgroundSize: '20px 20px',
          backgroundPosition: '0 0, 10px 10px',
          opacity: 0.05
        }}
      />
      
      <CartDrawer />
      <Navbar />
      
      <main className="flex-grow px-6 md:px-12 lg:px-16 py-8 w-full z-10">
        <Outlet />
      </main>
      
      <Footer />
    </div>
  );
};
