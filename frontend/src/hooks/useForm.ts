import { useState, useCallback } from "react";
import { getApiErrorMessage } from "@/utils/apiError";

export interface UseFormReturn<T extends object> {
  values: T;
  errors: Record<string, string | undefined>;
  serverError: string | null;
  successMessage: string | null;
  isSubmitting: boolean;

  handleChange: (name: keyof T) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => void;

  handleSubmit: (
    validate: (values: T) => Record<string, string | undefined>,
    submitFn: (values: T) => Promise<string | undefined>
  ) => (e: React.FormEvent) => void;

  clearServerError: () => void;
}

/**
 * Generic form hook that owns:
 * - typed values
 * - per-field validation errors
 * - a top-level server / success message
 * - submit lifecycle (isSubmitting)
 */
export function useForm<T extends object>(
  initialValues: T
): UseFormReturn<T> {
  // Stores the current form values and submission state.
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Record<string, string | undefined>>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = useCallback(
    (name: keyof T) => (e: React.ChangeEvent<HTMLInputElement>) => {
      // Updates one field and clears errors caused by its previous value.
      setValues((prev) => ({ ...prev, [name]: e.target.value }));
      setErrors((prev) => ({ ...prev, [name]: undefined }));
      setServerError(null);
    },
    []
  );

  const handleSubmit = useCallback(
    (
      validate: (values: T) => Record<string, string | undefined>,
      submitFn: (values: T) => Promise<string | undefined>
    ) => {
      return async (e: React.FormEvent) => {
        // Validates before submission and exposes API failures to the form.
        e.preventDefault();
        setServerError(null);
        setSuccessMessage(null);

        const validationErrors = validate(values);
        setErrors(validationErrors);

        const hasErrors = Object.values(validationErrors).some(Boolean);
        if (hasErrors) return;

        setIsSubmitting(true);
        try {
          const successMsg = await submitFn(values);
          if (successMsg) setSuccessMessage(successMsg);
        } catch (err) {
          setServerError(getApiErrorMessage(err));
        } finally {
          setIsSubmitting(false);
        }
      };
    },
    [values]
  );

  const clearServerError = useCallback(() => {
    setServerError(null);
  }, []);

  return {
    values,
    errors,
    serverError,
    successMessage,
    isSubmitting,
    handleChange,
    handleSubmit,
    clearServerError,
  };
}
