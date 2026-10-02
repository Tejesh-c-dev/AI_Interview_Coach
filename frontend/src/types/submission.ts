export type SupportedLanguage = "java" | "python" | "cpp";

export interface SubmissionRequest {
  sessionId: string;
  questionId: string;
  sourceCode: string;
  language: SupportedLanguage;
}

export type Verdict =
  | "ACCEPTED"
  | "WRONG_ANSWER"
  | "COMPILE_ERROR"
  | "RUNTIME_ERROR"
  | "TIME_LIMIT_EXCEEDED"
  | "MEMORY_LIMIT_EXCEEDED"
  | "PROCESSING"
  | "UNKNOWN";

export interface SubmissionResponse {
  id: string;
  sessionId: string;
  questionId: string;
  language: SupportedLanguage;
  verdict: Verdict;
  runtimeMs: number | null;
  memoryKb: number | null;
  stdout: string | null;
  stderr: string | null;
  compileOutput: string | null;
  createdAt: string;
}
