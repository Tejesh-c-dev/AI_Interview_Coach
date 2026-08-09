
import { Link } from "react-router-dom";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md rounded-xl bg-white p-8 text-center shadow-sm">
        <h1 className="text-2xl font-bold text-gray-900">Welcome Back</h1>
        <p className="mt-2 text-sm text-gray-500">
          Login functionality is not implemented yet.
        </p>
        <Link
          to="/register"
          className="mt-6 inline-block text-sm font-medium text-blue-600 hover:underline"
        >
          Don&apos;t have an account? Register
        </Link>
      </div>
    </div>
  );
}
