import { useState, useCallback } from "react";
import Editor from "@monaco-editor/react";
import Button from "@/components/Button";
import SubmissionResult from "@/components/SubmissionResult";
import Toast from "@/components/Toast";
import { submitCode } from "@/services/submissionService";
import { getApiErrorMessage } from "@/utils/apiError";
import type { QuestionResponse } from "@/types/questionBank";
import type { SubmissionResponse, SupportedLanguage } from "@/types/submission";

interface CodingWorkspaceProps {
  sessionId: string;
  question: QuestionResponse;
  questionNumber: number;
  /** Called after the user navigates away (next question / finish session). */
  onNext?: () => void;
  onFinish?: () => void;
  isLastQuestion?: boolean;
}

const LANGUAGE_OPTIONS: { value: SupportedLanguage; label: string; monacoLang: string }[] = [
  { value: "java",   label: "Java",   monacoLang: "java" },
  { value: "python", label: "Python", monacoLang: "python" },
  { value: "cpp",    label: "C++",    monacoLang: "cpp" },
];

const DEFAULT_STARTERS: Record<SupportedLanguage, string> = {
  java:   `public class Solution {\n    public static void main(String[] args) {\n        // Write your solution here\n    }\n}`,
  python: `# Write your solution here\n`,
  cpp:    `#include <bits/stdc++.h>\nusing namespace std;\n\nint main() {\n    // Write your solution here\n    return 0;\n}`,
};

/**
 * Full-screen coding workspace for a single coding question.
 *
 * Architecture notes:
 * - Monaco Editor is loaded lazily (code-split by @monaco-editor/react).
 * - Language selection resets code to starter code only if the editor is empty
 *   or still at the default; existing user edits are preserved.
 * - Submission goes to the backend only — Judge0 is never contacted from here.
 * - Slots for future panels (hints, AI review) are marked with TODO comments.
 */
export default function CodingWorkspace({
  sessionId,
  question,
  questionNumber,
  onNext,
  onFinish,
  isLastQuestion = false,
}: CodingWorkspaceProps) {
  const [language, setLanguage] = useState<SupportedLanguage>("java");
  const [code, setCode]         = useState<string>(
    question.starterCode ?? DEFAULT_STARTERS.java
  );
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult]         = useState<SubmissionResponse | null>(null);
  const [error, setError]           = useState("");

  const selectedLangMeta = LANGUAGE_OPTIONS.find((l) => l.value === language)!;

  const handleLanguageChange = useCallback(
    (newLang: SupportedLanguage) => {
      setLanguage(newLang);
      // Reset to starter/default only if user hasn't typed anything meaningful
      const currentDefault = question.starterCode ?? DEFAULT_STARTERS[language];
      if (code.trim() === "" || code === currentDefault) {
        setCode(question.starterCode ?? DEFAULT_STARTERS[newLang]);
      }
      setResult(null);
      setError("");
    },
    [code, language, question.starterCode]
  );

  const handleSubmit = async () => {
    if (!code.trim()) {
      setError("Source code must not be empty.");
      return;
    }
    setSubmitting(true);
    setResult(null);
    setError("");
    try {
      const submission = await submitCode({
        sessionId,
        questionId: question.id,
        sourceCode: code,
        language,
      });
      setResult(submission);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col gap-4">
      {/* ── Question header ──────────────────────────────────────────────── */}
      <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
        <p className="text-sm font-medium text-gray-400">
          Question {questionNumber} · {question.track.replace("_", " ")} · {question.difficulty}
        </p>
        <h2 className="mt-2 text-xl font-bold text-gray-900">{question.topic}</h2>
        <p className="mt-3 whitespace-pre-wrap text-gray-700 leading-relaxed">{question.prompt}</p>

        {/* TODO: add Hints panel slot here (Phase 2) */}
      </div>

      {/* ── Editor card ──────────────────────────────────────────────────── */}
      <div className="rounded-xl border border-gray-200 bg-white shadow-sm overflow-hidden">
        {/* Toolbar */}
        <div className="flex items-center justify-between border-b border-gray-200 px-4 py-2 bg-gray-50">
          <div className="flex items-center gap-2">
            <label className="text-xs font-semibold text-gray-500 uppercase tracking-wide">
              Language
            </label>
            <select
              className="rounded-md border border-gray-300 bg-white px-3 py-1 text-sm font-medium text-gray-800 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              value={language}
              onChange={(e) => handleLanguageChange(e.target.value as SupportedLanguage)}
              disabled={submitting}
            >
              {LANGUAGE_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>

          <div className="flex gap-2">
            {/* TODO: AI Code Review button slot (Phase 2) */}
            <Button
              onClick={handleSubmit}
              isLoading={submitting}
              loadingText="Running…"
              className="px-5 py-1.5 text-sm"
            >
              Submit
            </Button>
          </div>
        </div>

        {/* Monaco Editor */}
        <Editor
          height="420px"
          language={selectedLangMeta.monacoLang}
          value={code}
          onChange={(val) => setCode(val ?? "")}
          theme="vs-dark"
          options={{
            fontSize: 14,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 4,
            wordWrap: "on",
            readOnly: submitting,
          }}
        />
      </div>

      {/* ── Error toast ──────────────────────────────────────────────────── */}
      {error && (
        <Toast type="error" message={error} onDismiss={() => setError("")} />
      )}

      {/* ── Execution result ─────────────────────────────────────────────── */}
      {result && <SubmissionResult result={result} />}

      {/* TODO: AI Code Review panel slot (Phase 2) */}

      {/* ── Navigation actions ───────────────────────────────────────────── */}
      <div className="flex gap-3">
        {onNext && (
          <Button
            className="flex-1"
            disabled={submitting}
            onClick={onNext}
          >
            {isLastQuestion ? "Last Question" : "Next Question"}
          </Button>
        )}
        {onFinish && (
          <Button
            className="flex-1 bg-gray-700 hover:bg-gray-800"
            disabled={submitting}
            onClick={onFinish}
          >
            Finish Session
          </Button>
        )}
      </div>
    </div>
  );
}
