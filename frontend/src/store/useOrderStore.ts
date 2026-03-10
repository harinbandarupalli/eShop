import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface OrderItem {
  name: string;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  date: string;
  items: OrderItem[];
  subtotal: number;
  shippingCost: number;
  shippingMethod: string;
  total: number;
  status: 'Processing' | 'Shipped' | 'Delivered';
  address: {
    firstName: string;
    lastName: string;
    address: string;
    city: string;
    postalCode: string;
    country: string;
  };
  email: string;
}

export interface SavedAddress {
  id: string;
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;
  isDefault?: boolean;
}

interface OrderState {
  orders: Order[];
  addresses: SavedAddress[];
  addOrder: (order: Order) => void;
  addAddress: (address: SavedAddress) => void;
  removeAddress: (id: string) => void;
  setDefaultAddress: (id: string) => void;
  clearOrders: () => void;
}

export const useOrderStore = create<OrderState>()(
  persist(
    (set) => ({
      orders: [],
      addresses: [],
      addOrder: (order) =>
        set((state) => {
          // Also save the address if it's new
          const existingAddress = state.addresses.find(
            (a) => a.address === order.address.address && a.city === order.address.city
          );
          const newAddresses = existingAddress
            ? state.addresses
            : [
                ...state.addresses,
                {
                  id: `addr_${Date.now()}`,
                  ...order.address,
                  isDefault: state.addresses.length === 0,
                },
              ];
          return {
            orders: [order, ...state.orders],
            addresses: newAddresses,
          };
        }),
      addAddress: (address) =>
        set((state) => ({
          addresses: [...state.addresses, address],
        })),
      removeAddress: (id) =>
        set((state) => ({
          addresses: state.addresses.filter((a) => a.id !== id),
        })),
      setDefaultAddress: (id) =>
        set((state) => ({
          addresses: state.addresses.map((a) => ({
            ...a,
            isDefault: a.id === id,
          })),
        })),
      clearOrders: () => set({ orders: [], addresses: [] }),
    }),
    {
      name: 'bilbobag-orders',
    }
  )
);
