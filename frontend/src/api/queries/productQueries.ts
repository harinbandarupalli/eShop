import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';

export interface ProductImage {
  id: string;
  productId: string;
  imageUrl: string;
  altText: string;
  displayOrder: number;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  isActive: boolean;
  isTrending: boolean;
  images: ProductImage[];
}

export interface ProductBag {
  id: string;
  name: string;
  description: string;
  displayPrice: number;
  isActive: boolean;
  products: Product[];
}

export const useProductBags = (categorySlug?: string) => {
  return useQuery({
    queryKey: ['productBags', categorySlug],
    queryFn: async () => {
      // If categories are implemented in backend by slug, we can pass it as a query param
      const url = categorySlug && categorySlug !== 'all' 
        ? `/api/product-bags?category=${categorySlug}` 
        : '/api/product-bags';
      const { data } = await apiClient.get<ProductBag[]>(url);
      return data;
    }
  });
};

export const useProductBag = (bagId: string) => {
  return useQuery({
    queryKey: ['productBag', bagId],
    queryFn: async () => {
      const { data } = await apiClient.get<ProductBag>(`/api/product-bags/${bagId}`);
      return data;
    },
    enabled: !!bagId
  });
};
