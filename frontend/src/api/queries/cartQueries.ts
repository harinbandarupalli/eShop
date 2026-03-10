import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { ProductBag } from './productQueries';

export interface CartItem {
  id: string;
  cartId: string;
  bag: ProductBag;
  quantity: number;
}

export interface Cart {
  id: string;
  sessionId?: string;
  email?: string;
  status: string;
  items: CartItem[];
}

/** Compute cart total client-side from bag displayPrice × quantity */
export const computeCartTotal = (items: CartItem[]): number => {
  return items.reduce((sum, item) => {
    const price = item.bag?.displayPrice ?? 0;
    return sum + price * item.quantity;
  }, 0);
};

export const useCart = () => {
  return useQuery({
    queryKey: ['cart'],
    queryFn: async () => {
      const { data } = await apiClient.get<Cart>('/api/carts/my-cart');
      return data;
    }
  });
};

export const useAddToCart = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ bagId, quantity }: { bagId: string; quantity: number }) => {
      const { data } = await apiClient.post<Cart>('/api/carts/items', null, {
        params: { bagId, quantity }
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    }
  });
};

export const useUpdateCartItem = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ bagId, quantity }: { bagId: string; quantity: number }) => {
      if (quantity <= 0) {
        await apiClient.delete(`/api/carts/items/${bagId}`);
      } else {
        const { data } = await apiClient.put<Cart>(`/api/carts/items/${bagId}`, null, {
          params: { quantity }
        });
        return data;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    }
  });
};
