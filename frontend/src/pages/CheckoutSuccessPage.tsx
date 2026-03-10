import { CheckCircle2, PackageSearch } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { Link } from 'react-router-dom';

export const CheckoutSuccessPage = () => {
  return (
    <div className="py-20 lg:py-32 flex flex-col items-center justify-center text-center px-4">
      <div className="w-24 h-24 bg-green-500/15 text-green-500 rounded-full flex items-center justify-center mb-8">
        <CheckCircle2 className="w-12 h-12" />
      </div>
      
      <h1 className="text-4xl md:text-5xl font-black uppercase tracking-tight mb-4" style={{ color: 'var(--text-primary)' }}>
        Order Confirmed
      </h1>
      
      <p className="text-lg mb-2" style={{ color: 'var(--text-secondary)' }}>
        Thank you for your purchase. We've received your order.
      </p>
      <p className="mb-10" style={{ color: 'var(--text-muted)' }}>
        We'll email you an order confirmation with details and tracking info.
      </p>

      <div className="border rounded-3xl p-8 w-full max-w-md mb-10" style={{ backgroundColor: 'var(--surface)', borderColor: 'var(--border-color)' }}>
        <h2 className="font-bold text-lg mb-4 flex items-center justify-center gap-2" style={{ color: 'var(--text-primary)' }}>
          <PackageSearch className="w-5 h-5 text-primary" />
          Order #ESHOP-10928
        </h2>
        <div className="text-sm space-y-2 text-left" style={{ color: 'var(--text-muted)' }}>
          <div className="flex justify-between">
            <span>Status</span>
            <span className="font-semibold text-green-500">Processing</span>
          </div>
          <div className="flex justify-between">
            <span>Estimated Delivery</span>
            <span className="font-semibold" style={{ color: 'var(--text-primary)' }}>3-5 Business Days</span>
          </div>
        </div>
      </div>

      <div className="flex gap-4 flex-col sm:flex-row w-full sm:w-auto">
        <Link to="/profile/orders" className="w-full sm:w-auto">
          <Button variant="outline" className="w-full">View Orders</Button>
        </Link>
        <Link to="/category/all" className="w-full sm:w-auto">
          <Button className="w-full">Continue Shopping</Button>
        </Link>
      </div>
    </div>
  );
};
