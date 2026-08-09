import { useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "@/components/InputField";
import Button from "@/components/Button";
import Toast from "@/components/Toast";
import { useForm } from "@/hooks/useForm";
import { validateRegisterForm } from "@/utils/validation/auth";
import { register } from "@/services/authService";
import type { RegisterFormValues } from "@/types/auth";

const initialValues: RegisterFormValues = {
  name: "",
  email: "",
  password: "",
  confirmPassword: "",
};

/** How long the success toast stays visible before redirecting to the Login page. */
const REDIRECT_DELAY_MS = 2000;

export default function RegisterPage() {
  const navigate = useNavigate();

  const {
    values,
    errors,
    serverError,
    successMessage,
    isSubmitting,
    handleChange,
    handleSubmit,
    clearServerError,
  } = useForm<RegisterFormValues>(initialValues);

  const onSubmit = handleSubmit(validateRegisterForm, async (formValues) => {
    const { name, email, password } = formValues;
    const { message } = await register({ name, email, password });
    return message;
  });

  const dismissSuccess = useCallback(() => navigate("/login", { replace: true }), [navigate]);
  const dismissError = useCallback(() => clearServerError(), [clearServerError]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md rounded-xl bg-white p-8 shadow-sm">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-gray-900">Create Account</h1>
          <p className="mt-1 text-sm text-gray-500">
            Start your AI interview coaching journey
          </p>
        </div>

        {(serverError || successMessage) && (
          <div className="mb-4 space-y-2">
            {serverError && (
              <Toast type="error" message={serverError} onDismiss={dismissError} duration={6000} />
            )}
            {successMessage && (
              <Toast
                type="success"
                message={successMessage}
                onDismiss={dismissSuccess}
                duration={REDIRECT_DELAY_MS}
              />
            )}
          </div>
        )}

        <form onSubmit={onSubmit} className="space-y-4">
          <InputField
            label="Full Name"
            name="name"
            placeholder="John Doe"
            value={values.name}
            error={errors.name}
            onChange={handleChange("name")}
          />
          <InputField
            label="Email"
            name="email"
            type="email"
            placeholder="you@example.com"
            value={values.email}
            error={errors.email}
            onChange={handleChange("email")}
          />
          <InputField
            label="Password"
            name="password"
            type="password"
            placeholder="At least 8 characters"
            value={values.password}
            error={errors.password}
            onChange={handleChange("password")}
          />
          <InputField
            label="Confirm Password"
            name="confirmPassword"
            type="password"
            placeholder="Re-enter your password"
            value={values.confirmPassword}
            error={errors.confirmPassword}
            onChange={handleChange("confirmPassword")}
          />

          <Button type="submit" isLoading={isSubmitting} loadingText="Creating account...">
            Register
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-gray-600">
          Already have an account?{" "}
          <Link to="/login" className="font-medium text-blue-600 hover:underline">
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}
