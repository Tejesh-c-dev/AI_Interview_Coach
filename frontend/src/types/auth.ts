/** Payload sent to POST /api/auth/register */
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

/** Values collected by the registration form (superset of {@link RegisterRequest}, adds password confirmation) */
export interface RegisterFormValues {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
}

/** Shape of the response returned on successful registration */
export interface RegisterResponse {
  message: string;
}

/** Payload sent to POST /api/auth/login */
export interface LoginRequest {
  email: string;
  password: string;
}

/** Token response returned after successful login */
export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  createdAt: string;
}

/** Error body returned by the backend's GlobalExceptionHandler */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
