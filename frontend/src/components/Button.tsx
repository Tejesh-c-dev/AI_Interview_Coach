interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** Shows a disabled loading state while an action is running. */
  isLoading?: boolean;
  /** Text displayed instead of the button label during loading. */
  loadingText?: string;
}

export default function Button({
  children,
  isLoading = false,
  loadingText,
  disabled,
  className = "",
  ...props
}: ButtonProps) {
  // Renders a reusable button with consistent loading and disabled behavior.
  return (
    <button
      disabled={isLoading || disabled}
      className={`w-full rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50 ${className}`}
      {...props}
    >
      {isLoading ? loadingText || "Please wait..." : children}
    </button>
  );
}
