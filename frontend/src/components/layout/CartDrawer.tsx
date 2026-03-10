import { Link } from 'react-router-dom';
import { X, ShoppingBag, Plus, Minus, Trash2 } from 'lucide-react';
import { useCartStore } from '../../store/useCartStore';
import { Button } from '../ui/Button';
import { useCart, useUpdateCartItem, computeCartTotal } from '../../api/queries/cartQueries';
import type { CartItem } from '../../api/queries/cartQueries';

export const CartDrawer = () => {
  const { isCartOpen, closeCart } = useCartStore();
  const { data: cart, isLoading } = useCart();
  const { mutate: updateQuantity } = useUpdateCartItem();

  const handleUpdate = (bagId: string, quantity: number) => {
    updateQuantity({ bagId, quantity });
  };

  const hasItems = cart && cart.items && cart.items.length > 0;
  const subtotal = hasItems ? computeCartTotal(cart.items) : 0;

  return (
    <>
      {/* Backdrop */}
      {isCartOpen && (
        <div 
          className="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 transition-opacity"
          onClick={closeCart}
        />
      )}

      {/* Drawer */}
      <div 
        className={`fixed top-0 right-0 h-full w-full sm:w-[500px] shadow-2xl z-50 transform transition-transform duration-300 ease-in-out flex flex-col ${
          isCartOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
        style={{ backgroundColor: 'var(--surface)' }}
      >
        <div className="px-6 py-4 flex items-center justify-between border-b" style={{ borderColor: 'var(--border-color)' }}>
          <div className="flex items-center gap-2 text-primary">
            <ShoppingBag className="w-6 h-6" />
            <h2 className="text-xl font-black uppercase" style={{ color: 'var(--text-primary)' }}>Your Bag {hasItems && `(${cart.items.length})`}</h2>
          </div>
          <button 
            onClick={closeCart}
            className="p-2 hover:bg-primary/10 rounded-full transition-colors"
            style={{ color: 'var(--text-muted)' }}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 flex flex-col">
          {isLoading ? (
            <div className="flex-1 flex justify-center items-center">
              <div className="animate-spin w-8 h-8 border-4 border-t-primary rounded-full" style={{ borderColor: 'var(--border-color)', borderTopColor: 'var(--color-primary)' }} />
            </div>
          ) : !hasItems ? (
            <div className="flex-1 flex flex-col items-center justify-center">
              <ShoppingBag className="w-16 h-16 opacity-20 mb-4" style={{ color: 'var(--text-muted)' }} />
              <p className="font-semibold text-lg" style={{ color: 'var(--text-primary)' }}>Your bag is empty.</p>
              <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Looks like you haven't added anything yet.</p>
              <Button onClick={closeCart} className="mt-8">Start Shopping</Button>
            </div>
          ) : (
            <div className="space-y-6">
              {cart.items.map((item: CartItem) => {
                const bagName = item.bag?.name || 'Unknown Item';
                const bagPrice = item.bag?.displayPrice ?? 0;
                const bagId = item.bag?.id || item.id;

                return (
                  <div key={item.id} className="flex gap-4">
                    <div className="w-24 h-24 rounded-xl overflow-hidden shrink-0" style={{ backgroundColor: 'var(--surface-2)' }}>
                      <img 
                        src={'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=200'}
                        alt={bagName} 
                        className="w-full h-full object-cover" 
                      />
                    </div>
                    <div className="flex-1 flex flex-col">
                      <div className="flex justify-between items-start mb-1">
                        <h3 className="font-bold" style={{ color: 'var(--text-primary)' }}>{bagName}</h3>
                        <p className="font-bold" style={{ color: 'var(--text-primary)' }}>${(bagPrice * item.quantity).toFixed(2)}</p>
                      </div>
                      <p className="text-sm mb-auto" style={{ color: 'var(--text-muted)' }}>Qty: {item.quantity}</p>
                      
                      <div className="flex items-center justify-between mt-4">
                        <div className="flex items-center border rounded-lg" style={{ borderColor: 'var(--border-color)' }}>
                          <button 
                            onClick={() => handleUpdate(bagId, item.quantity - 1)}
                            className="p-1 transition-colors hover:text-primary"
                            style={{ color: 'var(--text-muted)' }}
                          >
                            <Minus className="w-4 h-4" />
                          </button>
                          <span className="w-8 text-center text-sm font-semibold" style={{ color: 'var(--text-primary)' }}>{item.quantity}</span>
                          <button 
                            onClick={() => handleUpdate(bagId, item.quantity + 1)}
                            className="p-1 transition-colors hover:text-primary"
                            style={{ color: 'var(--text-muted)' }}
                          >
                            <Plus className="w-4 h-4" />
                          </button>
                        </div>
                        
                        <button 
                          onClick={() => handleUpdate(bagId, 0)}
                          className="p-2 text-red-400 hover:text-red-500 hover:bg-red-500/10 rounded-lg transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <div className="border-t p-6" style={{ borderColor: 'var(--border-color)', backgroundColor: 'var(--surface-2)' }}>
          <div className="flex justify-between text-lg font-bold mb-4" style={{ color: 'var(--text-primary)' }}>
            <span>Subtotal</span>
            <span>${subtotal.toFixed(2)}</span>
          </div>
          <p className="text-xs mb-6" style={{ color: 'var(--text-muted)' }}>Shipping and taxes calculated at checkout.</p>
          {hasItems ? (
            <Link to="/checkout" onClick={closeCart} className="w-full">
              <Button className="w-full">Checkout</Button>
            </Link>
          ) : (
            <Button className="w-full" disabled>Checkout</Button>
          )}
        </div>
      </div>
    </>
  );
};
