import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';

export interface ShippingType {
  id: string;
  name: string;
  cost: number;
  description: string;
  isActive: boolean;
}

export const useShippingTypes = () => {
  return useQuery<ShippingType[]>({
    queryKey: ['shippingTypes'],
    queryFn: async () => {
      const { data } = await apiClient.get<ShippingType[]>('/api/shipping-types');
      return data;
    }
  });
};
