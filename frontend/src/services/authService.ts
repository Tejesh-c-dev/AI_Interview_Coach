import axios from "axios";
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  UserResponse,
} from "@/types/auth";

export const AUTH_TOKEN_KEY = "ai_coach_access_token";

export const http = axios.create({
  // Uses the Vite proxy so browser requests share the backend API prefix.
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(AUTH_TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(AUTH_TOKEN_KEY);
    }
    return Promise.reject(error);
  },
);

/**
 * Register a new user.
 * @returns The success message from the backend.
 * @throws An {@link AxiosError} whose response body is the backend's {@link ApiError}.
 */
export async function register(data: RegisterRequest): Promise<RegisterResponse> {
  const response = await http.post<RegisterResponse>("/auth/register", data);
  return response.data;
}

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await http.post<LoginResponse>("/auth/login", data);
  localStorage.setItem(AUTH_TOKEN_KEY, response.data.token);
  return response.data;
}

export async function getCurrentUser(): Promise<UserResponse> {
  const response = await http.get<UserResponse>("/auth/me");
  return response.data;
}

export function logout(): void {
  localStorage.removeItem(AUTH_TOKEN_KEY);
}
