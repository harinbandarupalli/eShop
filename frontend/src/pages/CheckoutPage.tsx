import { useState, useEffect } from 'react';
import { useCart, computeCartTotal } from '../api/queries/cartQueries';
import type { CartItem } from '../api/queries/cartQueries';
import { useShippingTypes } from '../api/queries/shippingQueries';
import { useQueryClient } from '@tanstack/react-query';
import { useAuth0 } from '@auth0/auth0-react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { AddressAutocomplete } from '../components/ui/AddressAutocomplete';
import { useOrderStore } from '../store/useOrderStore';
import { ShoppingBag, Lock, Truck, Check, User } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);

interface FormErrors {
  email?: string;
  firstName?: string;
  lastName?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  payment?: string;
}

const CheckoutForm = () => {
  const { data: cart, isLoading } = useCart();
  const { data: shippingTypes, isLoading: isLoadingShipping } = useShippingTypes();
  const { isAuthenticated, user } = useAuth0();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const stripe = useStripe();
  const elements = useElements();
  const addOrder = useOrderStore((s) => s.addOrder);
  const savedAddresses = useOrderStore((s) => s.addresses);
  const [isProcessing, setIsProcessing] = useState(false);
  const [selectedShippingId, setSelectedShippingId] = useState<string | null>(null);
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitted, setSubmitted] = useState(false);
  const [cardComplete, setCardComplete] = useState(false);
  const [useSavedAddress, setUseSavedAddress] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    email: '',
    firstName: '',
    lastName: '',
    address: '',
    city: '',
    postalCode: '',
    country: 'US',
  });

  // Auto-fill email for logged-in users
  useEffect(() => {
    if (isAuthenticated && user?.email) {
      setFormData(prev => ({ ...prev, email: user.email || '' }));
    }
  }, [isAuthenticated, user]);

  // Auto-fill from default saved address
  useEffect(() => {
    const defaultAddr = savedAddresses.find(a => a.isDefault);
    if (defaultAddr && !formData.address) {
      setFormData(prev => ({
        ...prev,
        firstName: defaultAddr.firstName || prev.firstName,
        lastName: defaultAddr.lastName || prev.lastName,
        address: defaultAddr.address,
        city: defaultAddr.city,
        postalCode: defaultAddr.postalCode,
        country: defaultAddr.country || prev.country,
      }));
      setUseSavedAddress(defaultAddr.id);
    }
  }, [savedAddresses]);

  if (shippingTypes && shippingTypes.length > 0 && !selectedShippingId) {
    setSelectedShippingId(shippingTypes[0].id);
  }

  const selectedShipping = shippingTypes?.find(s => s.id === selectedShippingId);
  const shippingCost = selectedShipping?.cost ?? 0;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (submitted && errors[name as keyof FormErrors]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const handleSelectSavedAddress = (addrId: string) => {
    const addr = savedAddresses.find(a => a.id === addrId);
    if (addr) {
      setFormData(prev => ({
        ...prev,
        firstName: addr.firstName,
        lastName: addr.lastName,
        address: addr.address,
        city: addr.city,
        postalCode: addr.postalCode,
        country: addr.country || prev.country,
      }));
      setUseSavedAddress(addrId);
    }
  };

  const validate = (): FormErrors => {
    const errs: FormErrors = {};

    if (!formData.email.trim()) {
      errs.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errs.email = 'Enter a valid email address';
    }

    if (!formData.firstName.trim()) errs.firstName = 'First name is required';
    if (!formData.lastName.trim()) errs.lastName = 'Last name is required';
    if (!formData.address.trim()) errs.address = 'Address is required';
    if (!formData.city.trim()) errs.city = 'City is required';
    if (!formData.postalCode.trim()) {
      errs.postalCode = 'Postal code is required';
    } else if (!/^[A-Za-z0-9\s-]{3,10}$/.test(formData.postalCode)) {
      errs.postalCode = 'Enter a valid postal code';
    }

    if (!cardComplete) {
      errs.payment = 'Please enter valid card details';
    }

    return errs;
  };

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);

    const validationErrors = validate();
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      const firstErrorField = document.querySelector('[data-error="true"]');
      firstErrorField?.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }

    if (!stripe || !elements) return;

    setIsProcessing(true);

    const cardElement = elements.getElement(CardElement);
    if (!cardElement) {
      setIsProcessing(false);
      return;
    }

    const { error, paymentMethod } = await stripe.createPaymentMethod({
      type: 'card',
      card: cardElement,
      billing_details: {
        name: `${formData.firstName} ${formData.lastName}`,
        email: formData.email,
        address: {
          line1: formData.address,
          city: formData.city,
          postal_code: formData.postalCode,
          country: formData.country,
        },
      },
    });

    if (error) {
      setErrors(prev => ({ ...prev, payment: error.message }));
      setIsProcessing(false);
      return;
    }

    console.log('Payment method created:', paymentMethod.id);

    // Save order to store
    const hasItems = cart && cart.items && cart.items.length > 0;
    const subtotal = hasItems ? computeCartTotal(cart.items) : 0;
    const total = subtotal + shippingCost;

    const orderItems = cart?.items?.map((item: CartItem) => ({
      name: item.bag?.name || 'Item',
      quantity: item.quantity,
      price: item.bag?.displayPrice ?? 0,
    })) || [];

    const order = {
      id: `ORD-${Date.now().toString(36).toUpperCase()}`,
      date: new Date().toISOString(),
      items: orderItems,
      subtotal,
      shippingCost,
      shippingMethod: selectedShipping?.name || 'Standard',
      total,
      status: 'Processing' as const,
      address: {
        firstName: formData.firstName,
        lastName: formData.lastName,
        address: formData.address,
        city: formData.city,
        postalCode: formData.postalCode,
        country: formData.country,
      },
      email: formData.email,
    };

    addOrder(order);

    // Clear cart
    setTimeout(() => {
      setIsProcessing(false);
      queryClient.removeQueries({ queryKey: ['cart'] });
      localStorage.removeItem('guest_session_id');
      navigate('/checkout/success');
    }, 1500);
  };

  const handleAddressSelect = (address: { address: string; city: string; postalCode: string; country: string }) => {
    setFormData(prev => ({
      ...prev,
      address: address.address,
      city: address.city,
      postalCode: address.postalCode,
      country: address.country || prev.country,
    }));
    setUseSavedAddress(null);
    setErrors(prev => ({ ...prev, address: undefined, city: undefined, postalCode: undefined }));
  };

  const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        fontFamily: '"Plus Jakarta Sans", sans-serif',
        fontWeight: '500',
        color: isDark ? '#f1f5f9' : '#0f172a',
        '::placeholder': {
          color: isDark ? '#64748b' : '#94a3b8',
        },
        iconColor: '#ff6d00',
      },
      invalid: {
        color: '#ef4444',
        iconColor: '#ef4444',
      },
    },
    hidePostalCode: true,
  };

  if (isLoading || isLoadingShipping) {
    return (
      <div className="py-20 flex justify-center">
        <div className="animate-spin w-12 h-12 border-4 border-t-primary rounded-full" style={{ borderColor: 'var(--border-color)', borderTopColor: 'var(--color-primary)' }} />
      </div>
    );
  }

  const hasItems = cart && cart.items && cart.items.length > 0;
  const subtotal = hasItems ? computeCartTotal(cart.items) : 0;
  const total = subtotal + shippingCost;

  if (!hasItems) {
    return (
      <div className="py-20 flex flex-col items-center">
        <ShoppingBag className="w-20 h-20 opacity-20 mb-6" style={{ color: 'var(--text-muted)' }} />
        <h2 className="text-3xl font-black uppercase" style={{ color: 'var(--text-primary)' }}>Your bag is empty</h2>
        <p className="mt-4 mb-8" style={{ color: 'var(--text-muted)' }}>Add some gear before you proceed to checkout.</p>
        <Link to="/category/all">
          <Button>Continue Shopping</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="py-8 lg:py-16">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
        {/* Checkout Form */}
        <div className="lg:col-span-7 xl:col-span-8 flex flex-col gap-10">
          <div>
            <h1 className="text-3xl md:text-4xl font-black uppercase tracking-tight mb-8" style={{ color: 'var(--text-primary)' }}>Checkout</h1>
            
            <form onSubmit={handleCheckout} noValidate className="space-y-10">
              
              {/* Contact Info */}
              <section>
                <h2 className="text-xl font-bold mb-4 border-b pb-2" style={{ color: 'var(--text-primary)', borderColor: 'var(--border-color)' }}>Contact Information</h2>
                {isAuthenticated && user?.email ? (
                  <div className="flex items-center gap-3 p-4 rounded-xl" style={{ backgroundColor: 'var(--surface-2)' }}>
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                      {user.picture ? (
                        <img src={user.picture} alt="" className="w-10 h-10 rounded-full" />
                      ) : (
                        <User className="w-5 h-5 text-primary" />
                      )}
                    </div>
                    <div>
                      <p className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>{user.name}</p>
                      <p className="text-sm" style={{ color: 'var(--text-muted)' }}>{user.email}</p>
                    </div>
                  </div>
                ) : (
                  <div data-error={!!errors.email}>
                    <Input 
                      label="Email Address" 
                      name="email" 
                      type="email" 
                      placeholder="you@example.com"
                      required 
                      value={formData.email} 
                      onChange={handleChange}
                      error={submitted ? errors.email : undefined}
                    />
                  </div>
                )}
              </section>

              {/* Shipping Address */}
              <section>
                <h2 className="text-xl font-bold mb-4 border-b pb-2" style={{ color: 'var(--text-primary)', borderColor: 'var(--border-color)' }}>Shipping Address</h2>
                
                {/* Saved addresses selector */}
                {savedAddresses.length > 0 && (
                  <div className="mb-4">
                    <label className="text-sm font-bold mb-2 block" style={{ color: 'var(--text-secondary)' }}>Saved Addresses</label>
                    <div className="space-y-2">
                      {savedAddresses.map((addr) => (
                        <label
                          key={addr.id}
                          className={`flex items-center gap-3 p-3 rounded-xl border-2 cursor-pointer transition-all text-sm ${
                            useSavedAddress === addr.id ? 'border-primary bg-primary/5' : 'hover:border-primary/30'
                          }`}
                          style={useSavedAddress !== addr.id ? { borderColor: 'var(--border-color)' } : undefined}
                        >
                          <input
                            type="radio"
                            name="savedAddress"
                            checked={useSavedAddress === addr.id}
                            onChange={() => handleSelectSavedAddress(addr.id)}
                            className="sr-only"
                          />
                          <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center shrink-0 ${
                            useSavedAddress === addr.id ? 'border-primary bg-primary' : ''
                          }`}
                          style={useSavedAddress !== addr.id ? { borderColor: 'var(--text-muted)' } : undefined}
                          >
                            {useSavedAddress === addr.id && <Check className="w-2.5 h-2.5 text-white" />}
                          </div>
                          <span style={{ color: 'var(--text-primary)' }}>
                            {addr.firstName} {addr.lastName} — {addr.address}, {addr.city} {addr.postalCode}
                          </span>
                          {addr.isDefault && (
                            <span className="text-xs bg-primary/10 text-primary px-2 py-0.5 rounded-full font-bold ml-auto">Default</span>
                          )}
                        </label>
                      ))}
                      <button
                        type="button"
                        onClick={() => {
                          setUseSavedAddress(null);
                          setFormData(prev => ({ ...prev, address: '', city: '', postalCode: '' }));
                        }}
                        className="text-sm font-bold text-primary hover:underline mt-1"
                      >
                        + Use a new address
                      </button>
                    </div>
                  </div>
                )}

                <div className="grid grid-cols-2 gap-4">
                  <div data-error={!!errors.firstName}>
                    <Input label="First Name" name="firstName" placeholder="John" required value={formData.firstName} onChange={handleChange} error={submitted ? errors.firstName : undefined} />
                  </div>
                  <div data-error={!!errors.lastName}>
                    <Input label="Last Name" name="lastName" placeholder="Doe" required value={formData.lastName} onChange={handleChange} error={submitted ? errors.lastName : undefined} />
                  </div>
                </div>
                <div className="mt-4" data-error={!!errors.address}>
                  <AddressAutocomplete
                    label="Street Address"
                    value={formData.address}
                    onChange={(val) => {
                      setFormData(prev => ({ ...prev, address: val }));
                      setUseSavedAddress(null);
                      if (submitted && errors.address) setErrors(prev => ({ ...prev, address: undefined }));
                    }}
                    onSelect={handleAddressSelect}
                    error={submitted ? errors.address : undefined}
                    placeholder="Start typing your address..."
                  />
                </div>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mt-4">
                  <div className="md:col-span-2" data-error={!!errors.city}>
                    <Input label="City" name="city" placeholder="New York" required value={formData.city} onChange={handleChange} error={submitted ? errors.city : undefined} />
                  </div>
                  <div data-error={!!errors.postalCode}>
                    <Input label="Postal Code" name="postalCode" placeholder="10001" required value={formData.postalCode} onChange={handleChange} error={submitted ? errors.postalCode : undefined} />
                  </div>
                </div>
              </section>

              {/* Shipping Method */}
              <section>
                <h2 className="flex items-center gap-2 text-xl font-bold mb-4 border-b pb-2" style={{ color: 'var(--text-primary)', borderColor: 'var(--border-color)' }}>
                  <Truck className="w-5 h-5" style={{ color: 'var(--text-muted)' }} />
                  Shipping Method
                </h2>
                <div className="space-y-3">
                  {shippingTypes && shippingTypes.length > 0 ? (
                    shippingTypes.map((option) => (
                      <label
                        key={option.id}
                        className={`flex items-center gap-4 p-4 rounded-2xl border-2 cursor-pointer transition-all ${
                          selectedShippingId === option.id
                            ? 'border-primary bg-primary/5'
                            : 'hover:border-primary/30'
                        }`}
                        style={selectedShippingId !== option.id ? { borderColor: 'var(--border-color)' } : undefined}
                      >
                        <input type="radio" name="shippingType" value={option.id} checked={selectedShippingId === option.id} onChange={() => setSelectedShippingId(option.id)} className="sr-only" />
                        <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center shrink-0 ${selectedShippingId === option.id ? 'border-primary bg-primary' : ''}`}
                          style={selectedShippingId !== option.id ? { borderColor: 'var(--text-muted)' } : undefined}>
                          {selectedShippingId === option.id && <Check className="w-3 h-3 text-white" />}
                        </div>
                        <div className="flex-1">
                          <div className="font-bold" style={{ color: 'var(--text-primary)' }}>{option.name}</div>
                          {option.description && <p className="text-sm mt-0.5" style={{ color: 'var(--text-muted)' }}>{option.description}</p>}
                        </div>
                        <div className="font-bold" style={{ color: 'var(--text-primary)' }}>
                          {option.cost === 0 ? <span className="text-primary">Free</span> : `$${option.cost.toFixed(2)}`}
                        </div>
                      </label>
                    ))
                  ) : (
                    <div className="text-sm p-4 rounded-xl" style={{ backgroundColor: 'var(--surface-2)', color: 'var(--text-muted)' }}>No shipping options available.</div>
                  )}
                </div>
              </section>

              {/* Payment - Stripe Elements */}
              <section>
                <h2 className="flex items-center gap-2 text-xl font-bold mb-4 border-b pb-2" style={{ color: 'var(--text-primary)', borderColor: 'var(--border-color)' }}>
                  <Lock className="w-5 h-5" style={{ color: 'var(--text-muted)' }} />
                  Payment Details
                </h2>
                <div className="p-6 rounded-2xl border" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-color)' }} data-error={!!errors.payment}>
                  <label className="text-sm font-bold mb-3 block" style={{ color: 'var(--text-secondary)' }}>Card Information</label>
                  <div className="p-4 rounded-lg border transition-all" style={{ backgroundColor: 'var(--input-bg)', borderColor: errors.payment ? '#ef4444' : 'var(--border-color)' }}>
                    <CardElement
                      options={cardElementOptions}
                      onChange={(e) => {
                        setCardComplete(e.complete);
                        if (e.error) {
                          setErrors(prev => ({ ...prev, payment: e.error?.message }));
                        } else {
                          setErrors(prev => ({ ...prev, payment: undefined }));
                        }
                      }}
                    />
                  </div>
                  {submitted && errors.payment && <p className="text-xs text-red-500 font-medium mt-2">{errors.payment}</p>}
                  <div className="flex items-center gap-2 mt-3">
                    <Lock className="w-3 h-3" style={{ color: 'var(--text-muted)' }} />
                    <span className="text-xs" style={{ color: 'var(--text-muted)' }}>Secured by Stripe. Your card details are never stored.</span>
                  </div>
                </div>
              </section>

              {submitted && Object.keys(errors).length > 0 && (
                <div className="p-4 rounded-xl border border-red-500/30 bg-red-500/5 text-red-500 text-sm font-medium">
                  Please fix the {Object.keys(errors).length} error{Object.keys(errors).length > 1 ? 's' : ''} above before proceeding.
                </div>
              )}

              <Button type="submit" size="lg" className="w-full text-lg h-14" disabled={isProcessing || !stripe}>
                {isProcessing ? 'Processing Securely...' : `Pay $${total.toFixed(2)}`}
              </Button>

              <p className="text-center text-xs" style={{ color: 'var(--text-muted)' }}>
                🧪 Test mode — use card <strong>4242 4242 4242 4242</strong>, any future date, any CVC
              </p>
            </form>
          </div>
        </div>

        {/* Order Summary */}
        <div className="lg:col-span-5 xl:col-span-4">
          <div className="rounded-3xl p-6 lg:p-8 border sticky top-32" style={{ backgroundColor: 'var(--surface)', borderColor: 'var(--border-color)' }}>
            <h2 className="text-xl font-bold mb-6" style={{ color: 'var(--text-primary)' }}>Order Summary</h2>
            
            <div className="space-y-4 mb-6 max-h-[40vh] overflow-y-auto pr-2">
              {cart.items.map((item: CartItem) => {
                const bagName = item.bag?.name || 'Item';
                const bagPrice = item.bag?.displayPrice ?? 0;
                return (
                  <div key={item.id} className="flex gap-4">
                    <div className="w-20 h-20 rounded-xl overflow-hidden shrink-0 border" style={{ backgroundColor: 'var(--surface-2)', borderColor: 'var(--border-subtle)' }}>
                      <img src={'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=200'} alt={bagName} className="w-full h-full object-cover" />
                    </div>
                    <div className="flex-1 flex flex-col justify-center">
                      <h3 className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>{bagName}</h3>
                      <p className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>Qty: {item.quantity}</p>
                      <p className="font-bold mt-auto" style={{ color: 'var(--text-primary)' }}>${(bagPrice * item.quantity).toFixed(2)}</p>
                    </div>
                  </div>
                );
              })}
            </div>

            <div className="border-t pt-6 space-y-4" style={{ borderColor: 'var(--border-color)' }}>
              <div className="flex justify-between">
                <span style={{ color: 'var(--text-secondary)' }}>Subtotal</span>
                <span className="font-medium" style={{ color: 'var(--text-primary)' }}>${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span style={{ color: 'var(--text-secondary)' }}>Shipping</span>
                <span className={`font-medium ${shippingCost === 0 ? 'text-primary' : ''}`} style={shippingCost !== 0 ? { color: 'var(--text-primary)' } : undefined}>
                  {shippingCost === 0 ? 'Free' : `$${shippingCost.toFixed(2)}`}
                </span>
              </div>
              {selectedShipping && <div className="text-xs" style={{ color: 'var(--text-muted)' }}>{selectedShipping.name}</div>}
              <div className="flex justify-between">
                <span style={{ color: 'var(--text-secondary)' }}>Taxes</span>
                <span className="font-medium" style={{ color: 'var(--text-primary)' }}>$0.00</span>
              </div>
              <div className="flex justify-between text-xl font-black pt-4 border-t" style={{ color: 'var(--text-primary)', borderColor: 'var(--border-color)' }}>
                <span>Total</span>
                <span>${total.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export const CheckoutPage = () => {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutForm />
    </Elements>
  );
};
