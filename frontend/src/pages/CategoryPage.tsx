import { useParams, Link } from 'react-router-dom';
import { Filter, ChevronDown, ArrowRight } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { useProductBags } from '../api/queries/productQueries';

export const CategoryPage = () => {
  const { categorySlug } = useParams();
  const { data: productBags, isLoading, error } = useProductBags(categorySlug);

  return (
    <div className="py-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h1 className="text-4xl md:text-5xl font-black uppercase tracking-tight mb-4" style={{ color: 'var(--text-primary)' }}>
            {categorySlug === 'all' || !categorySlug ? 'Shop All' : categorySlug}
          </h1>
          <p className="font-medium" style={{ color: 'var(--text-muted)' }}>
            Showing {productBags?.length || 0} products designed for performance.
          </p>
        </div>
        
        <div className="flex gap-4">
          <Button variant="outline" className="gap-2" style={{ borderColor: 'var(--border-color)', color: 'var(--text-secondary)' }}>
            <Filter className="w-4 h-4" />
            Filters
          </Button>
          <Button variant="outline" className="gap-2" style={{ borderColor: 'var(--border-color)', color: 'var(--text-secondary)' }}>
            Sort By
            <ChevronDown className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Grid */}
      {isLoading ? (
        <div className="text-center py-20">
          <div className="animate-spin w-12 h-12 border-4 border-t-primary rounded-full mx-auto mb-4" style={{ borderColor: 'var(--border-color)', borderTopColor: 'var(--color-primary)' }} />
          <h2 className="text-xl font-bold" style={{ color: 'var(--text-muted)' }}>Loading gear...</h2>
        </div>
      ) : error ? (
        <div className="text-center py-20 text-red-500">
          <h2 className="text-2xl font-bold mb-2">Error loading products</h2>
          <p>Please try again later.</p>
        </div>
      ) : (!productBags || productBags.length === 0) ? (
        <div className="text-center py-20">
          <h2 className="text-2xl font-bold mb-2" style={{ color: 'var(--text-primary)' }}>No products found</h2>
          <p style={{ color: 'var(--text-muted)' }}>We couldn't find any bags in this category.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {productBags.map((bag: any) => (
            <Link to={`/product/${bag.id}`} key={bag.id} className="group flex flex-col gap-4">
              <div className="relative aspect-[4/5] rounded-2xl overflow-hidden mb-2" style={{ backgroundColor: 'var(--surface-2)' }}>
                <img 
                  src={bag.products?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&q=80&w=800'} 
                  alt={bag.name} 
                  className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
                />
                <div className="absolute inset-x-0 bottom-0 p-4 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                  <Button className="w-full bg-white/90 text-primary hover:bg-white backdrop-blur-sm">
                    View Details
                  </Button>
                </div>
              </div>
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-bold text-lg leading-tight group-hover:text-primary transition-colors" style={{ color: 'var(--text-primary)' }}>{bag.name}</h3>
                  <p className="text-sm line-clamp-1" style={{ color: 'var(--text-muted)' }}>{bag.description}</p>
                </div>
                <span className="font-black text-lg" style={{ color: 'var(--text-primary)' }}>
                  ${bag.displayPrice?.toFixed(2) || '0.00'}
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};
