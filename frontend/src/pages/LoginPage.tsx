
import { useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import InputField from "@/components/InputField";
import Button from "@/components/Button";
import Toast from "@/components/Toast";
import { useForm } from "@/hooks/useForm";
import { login } from "@/services/authService";
import { validateLoginForm } from "@/utils/validation/auth";

const initialValues = { email: "", password: "" };

export default function LoginPage() {
  const navigate = useNavigate();
  const {
    values, errors, serverError, successMessage, isSubmitting, handleChange, handleSubmit,
    clearServerError,
  } = useForm(initialValues);

  const onSubmit = handleSubmit(validateLoginForm, async (formValues) => {
    await login(formValues);
    navigate("/interview", { replace: true });
    return "Login successful";
  });
  const dismissError = useCallback(() => clearServerError(), [clearServerError]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md rounded-xl bg-white p-8 shadow-sm">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-gray-900">Welcome Back</h1>
          <p className="mt-1 text-sm text-gray-500">Sign in to continue your coaching journey</p>
        </div>
        {serverError && (
          <Toast type="error" message={serverError} onDismiss={dismissError} duration={6000} />
        )}
        {successMessage && (
          <Toast type="success" message={successMessage} onDismiss={dismissError} duration={6000} />
        )}
        <form onSubmit={onSubmit} className="mt-4 space-y-4">
          <InputField label="Email" name="email" type="email" placeholder="you@example.com"
            value={values.email} error={errors.email} onChange={handleChange("email")} />
          <InputField label="Password" name="password" type="password" placeholder="Your password"
            value={values.password} error={errors.password} onChange={handleChange("password")} />
          <Button type="submit" isLoading={isSubmitting} loadingText="Signing in...">Login</Button>
        </form>
        <p className="mt-6 text-center text-sm text-gray-600">
          Don&apos;t have an account?{" "}
          <Link to="/register" className="font-medium text-blue-600 hover:underline">Register</Link>
        </p>
      </div>
    </div>
  );
}
