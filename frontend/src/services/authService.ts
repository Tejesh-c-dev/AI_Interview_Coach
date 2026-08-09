import axios from "axios";
import type { RegisterRequest, RegisterResponse } from "@/types/auth";

const http = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Register a new user.
 * @returns The success message from the backend.
 * @throws An {@link AxiosError} whose response body is the backend's {@link ApiError}.
 */
export async function register(data: RegisterRequest): Promise<RegisterResponse> {
  const response = await http.post<RegisterResponse>("/auth/register", data);
  return response.data;
}
