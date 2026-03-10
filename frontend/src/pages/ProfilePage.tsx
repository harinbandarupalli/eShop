import { useState } from 'react';
import { Package, MapPin, User, LogOut, Truck, Star, Trash2 } from 'lucide-react';
import { useAuth0 } from '@auth0/auth0-react';
import { useQueryClient } from '@tanstack/react-query';
import { Button } from '../components/ui/Button';
import { useOrderStore } from '../store/useOrderStore';
import { Link } from 'react-router-dom';

export const ProfilePage = () => {
  const [activeTab, setActiveTab] = useState<'orders' | 'addresses' | 'details'>('orders');
  const { user, logout, isAuthenticated } = useAuth0();
  const queryClient = useQueryClient();
  const { orders, addresses, removeAddress, setDefaultAddress } = useOrderStore();

  const handleSignOut = () => {
    queryClient.removeQueries({ queryKey: ['cart'] });
    localStorage.removeItem('guest_session_id');
    logout({ logoutParams: { returnTo: window.location.origin } });
  };

  if (!isAuthenticated) {
    return (
      <div className="py-20 lg:py-32 flex flex-col items-center justify-center text-center px-4">
        <User className="w-16 h-16 mb-6" style={{ color: 'var(--text-muted)' }} />
        <h1 className="text-3xl font-black uppercase tracking-tight mb-4" style={{ color: 'var(--text-primary)' }}>Please Sign In</h1>
        <p className="mb-8 max-w-sm" style={{ color: 'var(--text-muted)' }}>Sign in to view your orders, manage your addresses, and more.</p>
        <Button onClick={() => logout()}>Sign In / Register</Button>
      </div>
    );
  }

  const tabClass = (tab: string) =>
    `w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-colors ${
      activeTab === tab ? 'bg-primary text-white' : ''
    }`;

  const tabInactiveStyle = { color: 'var(--text-secondary)' };

  const formatDate = (iso: string) => {
    return new Date(iso).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const statusColor = (status: string) => {
    switch (status) {
      case 'Processing': return 'text-amber-500 bg-amber-500/10';
      case 'Shipped': return 'text-blue-500 bg-blue-500/10';
      case 'Delivered': return 'text-green-500 bg-green-500/10';
      default: return 'text-gray-500 bg-gray-500/10';
    }
  };

  return (
    <div className="py-8 lg:py-16 max-w-6xl mx-auto">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between mb-10 gap-4">
        <div className="flex items-center gap-4">
          {user?.picture && (
            <img src={user.picture} alt="" className="w-14 h-14 rounded-full border-2 border-primary/20" />
          )}
          <div>
            <h1 className="text-3xl md:text-4xl font-black uppercase tracking-tight mb-1" style={{ color: 'var(--text-primary)' }}>My Account</h1>
            <p style={{ color: 'var(--text-muted)' }}>Welcome back, {user?.name || user?.email}</p>
          </div>
        </div>
        <Button variant="outline" onClick={handleSignOut} className="gap-2">
          <LogOut className="w-4 h-4" />
          Sign Out
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 lg:gap-12">
        {/* Sidebar */}
        <div className="lg:col-span-1 space-y-2">
          <button onClick={() => setActiveTab('orders')} className={tabClass('orders')} style={activeTab !== 'orders' ? tabInactiveStyle : undefined}>
            <Package className="w-5 h-5" /> Orders
            {orders.length > 0 && (
              <span className="ml-auto text-xs font-bold bg-white/20 rounded-full px-2 py-0.5">
                {orders.length}
              </span>
            )}
          </button>
          <button onClick={() => setActiveTab('addresses')} className={tabClass('addresses')} style={activeTab !== 'addresses' ? tabInactiveStyle : undefined}>
            <MapPin className="w-5 h-5" /> Addresses
          </button>
          <button onClick={() => setActiveTab('details')} className={tabClass('details')} style={activeTab !== 'details' ? tabInactiveStyle : undefined}>
            <User className="w-5 h-5" /> Account Details
          </button>
        </div>

        {/* Content */}
        <div className="lg:col-span-3 min-h-[400px]">
          
          {/* ORDERS TAB */}
          {activeTab === 'orders' && (
            <div>
              <h2 className="text-2xl font-bold mb-6" style={{ color: 'var(--text-primary)' }}>Order History</h2>
              {orders.length === 0 ? (
                <div className="border rounded-2xl p-8 text-center" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)', color: 'var(--text-muted)' }}>
                  <Package className="w-12 h-12 mx-auto mb-4 opacity-20" />
                  <p>You haven't placed any orders yet.</p>
                  <Link to="/category/all">
                    <Button className="mt-4" size="sm">Start Shopping</Button>
                  </Link>
                </div>
              ) : (
                <div className="space-y-4">
                  {orders.map((order) => (
                    <div
                      key={order.id}
                      className="border rounded-2xl overflow-hidden"
                      style={{ backgroundColor: 'var(--surface)', borderColor: 'var(--border-color)' }}
                    >
                      {/* Order Header */}
                      <div className="flex flex-wrap items-center justify-between gap-4 p-5 border-b" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)' }}>
                        <div className="flex flex-wrap items-center gap-6">
                          <div>
                            <p className="text-xs font-medium uppercase tracking-wider" style={{ color: 'var(--text-muted)' }}>Order</p>
                            <p className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>{order.id}</p>
                          </div>
                          <div>
                            <p className="text-xs font-medium uppercase tracking-wider" style={{ color: 'var(--text-muted)' }}>Placed</p>
                            <p className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>{formatDate(order.date)}</p>
                          </div>
                          <div>
                            <p className="text-xs font-medium uppercase tracking-wider" style={{ color: 'var(--text-muted)' }}>Total</p>
                            <p className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>${order.total.toFixed(2)}</p>
                          </div>
                        </div>
                        <span className={`text-xs font-bold px-3 py-1 rounded-full ${statusColor(order.status)}`}>
                          {order.status}
                        </span>
                      </div>

                      {/* Order Items */}
                      <div className="p-5 space-y-3">
                        {order.items.map((item, idx) => (
                          <div key={idx} className="flex items-center justify-between text-sm">
                            <div className="flex items-center gap-3">
                              <div className="w-10 h-10 rounded-lg overflow-hidden shrink-0 border" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-subtle)' }}>
                                <img src="https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=80" alt="" className="w-full h-full object-cover" />
                              </div>
                              <div>
                                <p className="font-medium" style={{ color: 'var(--text-primary)' }}>{item.name}</p>
                                <p className="text-xs" style={{ color: 'var(--text-muted)' }}>Qty: {item.quantity}</p>
                              </div>
                            </div>
                            <p className="font-bold" style={{ color: 'var(--text-primary)' }}>${(item.price * item.quantity).toFixed(2)}</p>
                          </div>
                        ))}
                      </div>

                      {/* Order Footer */}
                      <div className="flex flex-wrap items-center gap-4 px-5 py-3 border-t text-xs" style={{ borderColor: 'var(--border-color)', color: 'var(--text-muted)' }}>
                        <div className="flex items-center gap-1.5">
                          <Truck className="w-3.5 h-3.5" />
                          <span>{order.shippingMethod}</span>
                        </div>
                        <span>•</span>
                        <span>{order.address.city}, {order.address.postalCode}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* ADDRESSES TAB */}
          {activeTab === 'addresses' && (
            <div>
              <h2 className="text-2xl font-bold mb-6" style={{ color: 'var(--text-primary)' }}>Saved Addresses</h2>
              {addresses.length === 0 ? (
                <div className="border rounded-2xl p-8 text-center" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)', color: 'var(--text-muted)' }}>
                  <MapPin className="w-12 h-12 mx-auto mb-4 opacity-20" />
                  <p>No saved addresses yet. Addresses are saved automatically when you place an order.</p>
                  <Link to="/category/all">
                    <Button className="mt-4" size="sm">Start Shopping</Button>
                  </Link>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {addresses.map((addr) => (
                    <div
                      key={addr.id}
                      className={`border-2 rounded-2xl p-5 relative transition-all ${addr.isDefault ? 'border-primary' : ''}`}
                      style={!addr.isDefault ? { borderColor: 'var(--border-color)', backgroundColor: 'var(--surface)' } : { backgroundColor: 'var(--surface)' }}
                    >
                      {addr.isDefault && (
                        <span className="absolute top-3 right-3 text-xs bg-primary/10 text-primary px-2.5 py-1 rounded-full font-bold flex items-center gap-1">
                          <Star className="w-3 h-3" /> Default
                        </span>
                      )}
                      <p className="font-bold mb-1" style={{ color: 'var(--text-primary)' }}>
                        {addr.firstName} {addr.lastName}
                      </p>
                      <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>{addr.address}</p>
                      <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>
                        {addr.city}, {addr.postalCode}
                      </p>
                      <p className="text-sm" style={{ color: 'var(--text-muted)' }}>{addr.country}</p>

                      <div className="flex gap-3 mt-4">
                        {!addr.isDefault && (
                          <button
                            onClick={() => setDefaultAddress(addr.id)}
                            className="text-xs font-bold text-primary hover:underline"
                          >
                            Set as Default
                          </button>
                        )}
                        <button
                          onClick={() => removeAddress(addr.id)}
                          className="text-xs font-bold text-red-500 hover:underline flex items-center gap-1"
                        >
                          <Trash2 className="w-3 h-3" /> Remove
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* ACCOUNT DETAILS TAB */}
          {activeTab === 'details' && (
            <div>
              <h2 className="text-2xl font-bold mb-6" style={{ color: 'var(--text-primary)' }}>Account Details</h2>
              <div className="space-y-6 max-w-md">
                <div className="flex items-center gap-4 mb-6">
                  {user?.picture && (
                    <img src={user.picture} alt="" className="w-20 h-20 rounded-full border-2 border-primary/20" />
                  )}
                  <div>
                    <p className="font-bold text-lg" style={{ color: 'var(--text-primary)' }}>{user?.name || 'Not provided'}</p>
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Member since {new Date().getFullYear()}</p>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1" style={{ color: 'var(--text-secondary)' }}>Name</label>
                  <div className="px-4 py-3 border rounded-lg" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>
                    {user?.name || 'Not provided'}
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1" style={{ color: 'var(--text-secondary)' }}>Email</label>
                  <div className="px-4 py-3 border rounded-lg" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>
                    {user?.email || 'Not provided'}
                  </div>
                </div>
                <div className="pt-4 border-t" style={{ borderColor: 'var(--border-color)' }}>
                  <div className="flex items-center gap-4 text-sm" style={{ color: 'var(--text-muted)' }}>
                    <span>{orders.length} order{orders.length !== 1 ? 's' : ''} placed</span>
                    <span>•</span>
                    <span>{addresses.length} saved address{addresses.length !== 1 ? 'es' : ''}</span>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
