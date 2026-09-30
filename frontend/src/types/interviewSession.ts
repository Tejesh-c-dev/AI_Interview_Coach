import type { QuestionDifficulty, QuestionResponse, QuestionTrack } from "@/types/questionBank";

export interface StartSessionRequest {
  track: QuestionTrack;
  difficulty: QuestionDifficulty;
  configuration?: { companyMode?: string };
}

export interface SessionResponse {
  id: string;
  track: QuestionTrack;
  difficulty: QuestionDifficulty;
  companyMode: string | null;
  startedAt: string;
  endedAt: string | null;
  status: "ACTIVE" | "COMPLETED";
}

export interface NextQuestionResponse {
  sessionId: string;
  questionNumber: number;
  question: QuestionResponse;
}
