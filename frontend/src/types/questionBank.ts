export type QuestionTrack = "DSA" | "BEHAVIORAL" | "SYSTEM_DESIGN" | "OOP" | "JAVA";
export type QuestionDifficulty = "EASY" | "MEDIUM" | "HARD";
export type QuestionType = "CODING" | "NON_CODING";

export interface QuestionResponse {
  id: string;
  track: QuestionTrack;
  difficulty: QuestionDifficulty;
  topic: string;
  prompt: string;
  questionType: QuestionType;
  codingLanguage: string | null;
  starterCode: string | null;
  functionSignature: string | null;
}

export interface QuestionSelectionRequest {
  track: QuestionTrack;
  difficulty: QuestionDifficulty;
  topic?: string;
  selectionKey?: string;
}
