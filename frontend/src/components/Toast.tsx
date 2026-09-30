import { useEffect } from "react";

interface ToastProps {
  message: string;
  type: "success" | "error";
  onDismiss: () => void;
  duration?: number;
}

/** Displays a dismissible success or error notification. */
export default function Toast({
  message,
  type,
  onDismiss,
  duration = 5000,
}: ToastProps) {
  // Automatically dismisses the notification after its configured duration.
  useEffect(() => {
    const id = setTimeout(onDismiss, duration);
    return () => clearTimeout(id);
  }, [onDismiss, duration]);

  const styles =
    type === "success"
      ? "bg-green-50 text-green-800 border-green-200"
      : "bg-red-50 text-red-800 border-red-200";

  // Selects the visual treatment that matches the notification type.
  return (
    <div
      className={`flex items-center justify-between rounded-lg border px-4 py-3 text-sm ${styles}`}
      role="alert"
    >
      <span>{message}</span>
      <button
        onClick={onDismiss}
        className="ml-4 text-current opacity-60 hover:opacity-100"
        aria-label="Dismiss"
      >
        ✕
      </button>
    </div>
  );
}
