import { http } from "@/services/authService";
import type {
  NextQuestionResponse,
  SessionResponse,
  StartSessionRequest,
} from "@/types/interviewSession";

export async function startSession(data: StartSessionRequest): Promise<SessionResponse> {
  const response = await http.post<SessionResponse>("/sessions", data);
  return response.data;
}

export async function getNextQuestion(sessionId: string): Promise<NextQuestionResponse> {
  const response = await http.get<NextQuestionResponse>(`/sessions/${sessionId}/next-question`);
  return response.data;
}

export async function finishSession(sessionId: string): Promise<SessionResponse> {
  const response = await http.post<SessionResponse>(`/sessions/${sessionId}/finish`);
  return response.data;
}
