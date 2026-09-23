import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL || (import.meta.env.DEV ? "/api" : "http://localhost:8080/api");
export const api = axios.create({ baseURL, timeout: 15000, headers: { "Content-Type": "application/json" } });
api.interceptors.request.use(config => { const token = sessionStorage.getItem("solar_token"); if (token) config.headers.Authorization = `Bearer ${token}`; return config; });
api.interceptors.response.use(response => response, error => {
  if (error.response?.status === 401) {
    sessionStorage.removeItem("solar_token");
    sessionStorage.removeItem("solar_user");
    if (location.pathname.startsWith("/dashboard") || location.pathname.startsWith("/admin")) location.assign("/login");
  }
  return Promise.reject(error);
});
const data = response => response.data;
export const authService = { login: payload => api.post("/auth/login", payload).then(data), register: payload => api.post("/auth/register", payload).then(data) };
export const calculatorService = { estimate: payload => api.post("/calculator", payload).then(data) };
export const quoteService = { create: payload => api.post("/quotes", payload).then(data), summary: () => api.get("/quotes/admin/summary").then(data) };
export const commerceService = { orders: () => api.get("/orders").then(data), addresses: () => api.get("/addresses").then(data), notifications: () => api.get("/notifications").then(data), cart: () => api.get("/cart").then(data) };
export const publicService = { services: () => api.get("/services").then(data), service: slug => api.get(`/services/${encodeURIComponent(slug)}`).then(data), settings: () => api.get("/settings").then(data) };