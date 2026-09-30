import { http } from "./authService";
import type {
  QuestionDifficulty,
  QuestionResponse,
  QuestionSelectionRequest,
  QuestionTrack,
  QuestionType,
} from "@/types/questionBank";

export async function findQuestions(
  track: QuestionTrack,
  difficulty: QuestionDifficulty,
  options?: { topic?: string; type?: QuestionType },
): Promise<QuestionResponse[]> {
  const response = await http.get<QuestionResponse[]>("/questions", {
    params: { track, difficulty, ...options },
  });
  return response.data;
}

export async function selectQuestion(
  request: QuestionSelectionRequest,
): Promise<QuestionResponse> {
  const response = await http.post<QuestionResponse>("/questions/select", request);
  return response.data;
}
