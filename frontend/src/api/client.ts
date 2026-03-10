import axios from 'axios';
import type { AxiosInstance } from 'axios';
import { v4 as uuidv4 } from 'uuid';

const getGuestSessionId = (): string => {
  let sessionId = localStorage.getItem('guest_session_id');
  if (!sessionId) {
    sessionId = uuidv4();
    localStorage.setItem('guest_session_id', sessionId);
  }
  return sessionId;
};

// We will export a function to create an Axios instance so we can inject the Auth0 `getAccessTokenSilently` function later,
// or we can set up an interceptor globally if we handle the injection at the React context level.

export const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  async (config) => {
    // If not authenticated (we will set Authorization header globally from a hook if authenticated), 
    // we attach the guest session id.
    if (!config.headers['Authorization']) {
      config.headers['X-Guest-SessionID'] = getGuestSessionId();
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
