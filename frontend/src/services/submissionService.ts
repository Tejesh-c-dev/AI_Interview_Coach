import { http } from "@/services/authService";
import type { SubmissionRequest, SubmissionResponse } from "@/types/submission";

export async function submitCode(data: SubmissionRequest): Promise<SubmissionResponse> {
  const response = await http.post<SubmissionResponse>("/submissions", data);
  return response.data;
}
