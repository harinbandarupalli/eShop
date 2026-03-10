import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Shield, Truck, RotateCcw } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { useCartStore } from '../store/useCartStore';
import { useProductBag } from '../api/queries/productQueries';
import { useAddToCart } from '../api/queries/cartQueries';

export const ProductDetailPage = () => {
  const { productId } = useParams<{ productId: string }>();
  const { openCart } = useCartStore();
  
  const { data: bag, isLoading, error } = useProductBag(productId || '');
  const { mutate: addToCart, isPending: isAdding } = useAddToCart();

  const [selectedProductId, setSelectedProductId] = useState<string | null>(null);

  if (bag && !selectedProductId && bag.products && bag.products.length > 0) {
    setSelectedProductId(bag.products[0].id);
  }

  const selectedProduct = bag?.products?.find((p: any) => p.id === selectedProductId) || bag?.products?.[0];

  const handleAddToCart = () => {
    if (bag) {
      addToCart({ bagId: bag.id, quantity: 1 }, {
        onSuccess: () => {
          openCart();
        }
      });
    }
  };

  if (isLoading) {
    return (
      <div className="py-20 flex justify-center">
        <div className="animate-spin w-12 h-12 border-4 border-t-primary rounded-full" style={{ borderColor: 'var(--border-color)', borderTopColor: 'var(--color-primary)' }} />
      </div>
    );
  }

  if (error || !bag) {
    return (
      <div className="py-20 text-center text-red-500">
        <h2 className="text-2xl font-bold">Failed to load product.</h2>
      </div>
    );
  }

  const features = [
    'Weather-resistant 1000D Nylon',
    'Padded sleeve fits up to 16" laptop',
    'YKK AquaGuard® zippers',
    'Luggage pass-through',
    'Fleece-lined sunglasses pocket'
  ];

  const imgUrl = selectedProduct?.images?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&q=80&w=1200';

  return (
    <div className="py-8 lg:py-16">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-20">
        {/* Images */}
        <div className="space-y-4">
          <div className="aspect-[4/5] lg:aspect-square rounded-3xl overflow-hidden relative" style={{ backgroundColor: 'var(--surface-2)' }}>
            <img 
              src={imgUrl} 
              alt={bag.name} 
              className="w-full h-full object-cover"
            />
          </div>
        </div>

        {/* Details */}
        <div className="flex flex-col">
          <h1 className="text-4xl lg:text-5xl font-black uppercase tracking-tight mb-4" style={{ color: 'var(--text-primary)' }}>{bag.name}</h1>
          <p className="text-2xl font-bold mb-6" style={{ color: 'var(--text-secondary)' }}>
            ${bag.displayPrice?.toFixed(2) || '0.00'}
          </p>
          
          <p className="font-medium leading-relaxed mb-8" style={{ color: 'var(--text-secondary)' }}>
            {bag.description}
          </p>

          <div className="mb-8">
            <h3 className="text-sm font-bold uppercase tracking-wider mb-4" style={{ color: 'var(--text-primary)' }}>
              Variant: <span style={{ color: 'var(--text-muted)' }} className="capitalize">{selectedProduct?.name}</span>
            </h3>
            <div className="flex gap-4 flex-wrap">
              {bag.products?.map((p: any) => (
                <button
                  key={p.id}
                  onClick={() => setSelectedProductId(p.id)}
                  className={`px-4 py-2 font-semibold text-sm rounded-lg border-2 transition-all flex items-center justify-center ${
                    selectedProductId === p.id 
                      ? 'border-primary bg-primary/5 text-primary' 
                      : 'hover:border-primary/50'
                  }`}
                  style={selectedProductId !== p.id ? { borderColor: 'var(--border-color)', color: 'var(--text-secondary)' } : undefined}
                >
                  {p.name}
                </button>
              ))}
            </div>
          </div>

          <Button 
            size="lg" 
            className="w-full mb-8 relative" 
            onClick={handleAddToCart}
            disabled={isAdding || !selectedProduct}
          >
            {isAdding ? 'Adding...' : `Add To Bag — $${bag.displayPrice?.toFixed(2) || '0.00'}`}
          </Button>

          {/* Value Props */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 py-8 border-y mb-8" style={{ borderColor: 'var(--border-color)' }}>
            <div className="flex flex-col items-center text-center gap-2">
              <Truck className="w-6 h-6 text-primary" />
              <span className="text-xs font-bold uppercase" style={{ color: 'var(--text-primary)' }}>Free Shipping</span>
            </div>
            <div className="flex flex-col items-center text-center gap-2">
              <Shield className="w-6 h-6 text-primary" />
              <span className="text-xs font-bold uppercase" style={{ color: 'var(--text-primary)' }}>Lifetime Warranty</span>
            </div>
            <div className="flex flex-col items-center text-center gap-2">
              <RotateCcw className="w-6 h-6 text-primary" />
              <span className="text-xs font-bold uppercase" style={{ color: 'var(--text-primary)' }}>30-Day Returns</span>
            </div>
          </div>

          {/* Features */}
          <div>
            <h3 className="text-lg font-bold uppercase tracking-wide mb-4" style={{ color: 'var(--text-primary)' }}>Specs & Features</h3>
            <ul className="space-y-3">
              {features.map((feature, idx) => (
                <li key={idx} className="flex flex-row items-center gap-3 font-medium pb-3 border-b last:border-0" 
                    style={{ color: 'var(--text-secondary)', borderColor: 'var(--border-subtle)' }}>
                  <span className="w-2 h-2 rounded-full bg-primary shrink-0" />
                  {feature}
                </li>
              ))}
            </ul>
          </div>

        </div>
      </div>
    </div>
  );
};
