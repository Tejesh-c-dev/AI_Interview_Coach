import type { ApiError } from "@/types/auth";

/**
 * Extracts a human-readable message from an unknown error value.
 *
 * Prefers the backend's {@link ApiError} body returned by the GlobalExceptionHandler,
 * then the error's own message property, then a caller-supplied fallback.
 */
export function getApiErrorMessage(
  error: unknown,
  fallback = "Something went wrong. Please try again.",
): string {
  const axiosError = error as { response?: { data?: ApiError } };
  const backendMessage = axiosError.response?.data?.message;
  if (backendMessage) return backendMessage;

  if (error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
}
