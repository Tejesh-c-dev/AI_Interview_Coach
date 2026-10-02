import type { SubmissionResponse } from "@/types/submission";
import VerdictBadge from "@/components/VerdictBadge";

interface SubmissionResultProps {
  result: SubmissionResponse;
}

/**
 * Displays normalized execution result details.
 * Designed to be extensible — future panels (AI review, hints) can sit alongside.
 */
export default function SubmissionResult({ result }: SubmissionResultProps) {
  const hasOutput = result.stdout || result.stderr || result.compileOutput;

  return (
    <div className="mt-6 space-y-4 rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
      {/* Header row */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <VerdictBadge verdict={result.verdict} />
        <div className="flex gap-4 text-sm text-gray-500">
          {result.runtimeMs != null && (
            <span>
              ⏱ <span className="font-medium text-gray-700">{result.runtimeMs.toFixed(0)} ms</span>
            </span>
          )}
          {result.memoryKb != null && (
            <span>
              🧠 <span className="font-medium text-gray-700">{(result.memoryKb / 1024).toFixed(1)} MB</span>
            </span>
          )}
        </div>
      </div>

      {/* Output panels */}
      {hasOutput && (
        <div className="space-y-3">
          {result.compileOutput && (
            <OutputPanel label="Compile Output" content={result.compileOutput} variant="error" />
          )}
          {result.stderr && (
            <OutputPanel label="Standard Error" content={result.stderr} variant="error" />
          )}
          {result.stdout && (
            <OutputPanel label="Standard Output" content={result.stdout} variant="neutral" />
          )}
        </div>
      )}
    </div>
  );
}

interface OutputPanelProps {
  label: string;
  content: string;
  variant: "error" | "neutral";
}

function OutputPanel({ label, content, variant }: OutputPanelProps) {
  const bg    = variant === "error" ? "bg-red-950"  : "bg-gray-900";
  const text  = variant === "error" ? "text-red-200" : "text-green-300";

  return (
    <div>
      <p className="mb-1 text-xs font-semibold uppercase tracking-wide text-gray-500">{label}</p>
      <pre className={`overflow-x-auto rounded-lg ${bg} px-4 py-3 text-sm ${text} whitespace-pre-wrap`}>
        {content}
      </pre>
    </div>
  );
}
