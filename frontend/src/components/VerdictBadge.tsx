import type { Verdict } from "@/types/submission";

interface VerdictBadgeProps {
  verdict: Verdict;
}

const VERDICT_STYLES: Record<Verdict, { bg: string; text: string; label: string }> = {
  ACCEPTED:              { bg: "bg-green-100",  text: "text-green-800",  label: "Accepted" },
  WRONG_ANSWER:          { bg: "bg-red-100",    text: "text-red-800",    label: "Wrong Answer" },
  COMPILE_ERROR:         { bg: "bg-orange-100", text: "text-orange-800", label: "Compile Error" },
  RUNTIME_ERROR:         { bg: "bg-red-100",    text: "text-red-800",    label: "Runtime Error" },
  TIME_LIMIT_EXCEEDED:   { bg: "bg-yellow-100", text: "text-yellow-800", label: "Time Limit Exceeded" },
  MEMORY_LIMIT_EXCEEDED: { bg: "bg-yellow-100", text: "text-yellow-800", label: "Memory Limit Exceeded" },
  PROCESSING:            { bg: "bg-blue-100",   text: "text-blue-800",   label: "Processing…" },
  UNKNOWN:               { bg: "bg-gray-100",   text: "text-gray-700",   label: "Unknown" },
};

export default function VerdictBadge({ verdict }: VerdictBadgeProps) {
  const { bg, text, label } = VERDICT_STYLES[verdict] ?? VERDICT_STYLES.UNKNOWN;
  return (
    <span className={`inline-flex items-center rounded-full px-3 py-1 text-sm font-semibold ${bg} ${text}`}>
      {label}
    </span>
  );
}
