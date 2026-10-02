import { http } from "@/services/authService";
import type { InterviewHistory, ProgressDashboard, TopicProgress } from "@/types/progress";

export async function getProgress(userId: string): Promise<TopicProgress[]> {
  const response = await http.get<TopicProgress[]>(`/progress/${userId}`);
  return response.data;
}

export async function getProgressDashboard(userId: string): Promise<ProgressDashboard> {
  const response = await http.get<ProgressDashboard>(`/progress/${userId}/dashboard`);
  return response.data;
}

export async function getInterviewHistory(userId: string): Promise<InterviewHistory[]> {
  const response = await http.get<InterviewHistory[]>(`/progress/${userId}/history`);
  return response.data;
}
