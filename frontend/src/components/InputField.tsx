interface InputFieldProps {
  label: string;
  name: string;
  type?: string;
  placeholder?: string;
  value: string;
  error?: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void;
}

/** Renders a labeled input and its optional validation message. */
export default function InputField({
  label,
  name,
  type = "text",
  placeholder,
  value,
  error,
  onChange,
  onBlur,
}: InputFieldProps) {
  return (
    <div>
      <label htmlFor={name} className="block text-sm font-medium text-gray-700 mb-1">
        {label}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onBlur={onBlur}
        className={`w-full rounded-lg border px-3 py-2 text-sm outline-none transition ${
          error
            ? "border-red-500 focus:border-red-500 focus:ring-1 focus:ring-red-500"
            : "border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        }`}
      />
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  );
}
