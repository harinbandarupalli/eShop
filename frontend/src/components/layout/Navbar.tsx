import { Link } from 'react-router-dom';
import { ShoppingCart, Search, ShoppingBag } from 'lucide-react';
import { useAuth0 } from '@auth0/auth0-react';
import { useCartStore } from '../../store/useCartStore';
import { useCart } from '../../api/queries/cartQueries';

export const Navbar = () => {
  const { isAuthenticated, loginWithRedirect, user } = useAuth0();
  const { toggleCart } = useCartStore();
  const { data: cart } = useCart();
  
  const cartItemsCount = cart?.items?.reduce((total, item) => total + item.quantity, 0) || 0;

  return (
    <header className="sticky top-0 z-40 backdrop-blur-md border-b px-6 md:px-12 lg:px-16 py-4" style={{ backgroundColor: 'var(--nav-bg)', borderColor: 'var(--border-color)' }}>
      <div className="flex items-center justify-between">
        
        <div className="flex items-center gap-8">
          <Link to="/" className="flex items-center gap-2 text-primary">
            <ShoppingBag className="w-8 h-8 font-bold" />
            <h2 className="text-2xl font-extrabold tracking-tighter">BilboBag.in</h2>
          </Link>
          <nav className="hidden lg:flex items-center gap-6">
            <Link to="/category/all" className="font-semibold transition-colors hover:text-primary" style={{ color: 'var(--text-secondary)' }}>Shop All</Link>
            <Link to="/category/new" className="font-semibold transition-colors hover:text-primary" style={{ color: 'var(--text-secondary)' }}>New Drops</Link>
            <Link to="/category/best-sellers" className="font-semibold transition-colors hover:text-primary" style={{ color: 'var(--text-secondary)' }}>Best Sellers</Link>
          </nav>
        </div>

        <div className="flex items-center gap-4">
          <div className="hidden md:flex relative">
            <input 
              type="text" 
              placeholder="Search bags..." 
              className="pl-10 pr-4 py-2 border-none rounded-full focus:ring-2 focus:ring-primary w-64 text-sm" 
              style={{ backgroundColor: 'var(--surface-2)', color: 'var(--text-primary)' }}
            />
            <Search className="absolute left-3 top-2.5 w-4 h-4" style={{ color: 'var(--text-muted)' }} />
          </div>

          <button 
            onClick={toggleCart}
            className="p-2 hover:bg-primary/10 rounded-full relative transition-colors"
            style={{ color: 'var(--text-secondary)' }}
          >
            <ShoppingCart className="w-6 h-6" />
            {cartItemsCount > 0 && (
              <span className="absolute top-0 right-0 bg-primary text-white text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
                {cartItemsCount}
              </span>
            )}
          </button>

          {isAuthenticated ? (
            <Link 
              to="/profile"
              className="flex items-center gap-2 p-1 pr-3 rounded-full transition-all"
              style={{ backgroundColor: 'var(--surface-2)' }}
            >
              <img src={user?.picture} alt={user?.name} className="w-8 h-8 rounded-full bg-primary" />
              <span className="text-sm font-bold hidden sm:inline" style={{ color: 'var(--text-primary)' }}>Profile</span>
            </Link>
          ) : (
            <>
              <button 
                onClick={() => loginWithRedirect()}
                className="text-sm font-bold hover:text-primary transition-colors px-3 py-2"
                style={{ color: 'var(--text-secondary)' }}
              >
                Log In
              </button>
              <button 
                onClick={() => loginWithRedirect({ authorizationParams: { screen_hint: 'signup' } })}
                className="flex items-center gap-2 px-4 py-2 bg-primary hover:bg-primary/90 text-white rounded-full transition-all text-sm font-bold"
              >
                Sign Up
              </button>
            </>
          )}

        </div>
      </div>
    </header>
  );
};
