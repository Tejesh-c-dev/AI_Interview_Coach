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

/** Error body returned by the backend's GlobalExceptionHandler */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
