export interface TopicProgress {
  topic: string;
  attempts: number;
  accuracy: number;
  lastPracticed: string;
}

export interface InterviewHistory {
  id: string;
  track: string;
  difficulty: string;
  startedAt: string;
  endedAt: string | null;
  status: "ACTIVE" | "COMPLETED";
  submissionCount: number;
  acceptedSubmissionCount: number;
}

export interface ProgressDashboard {
  totalSessions: number;
  completedSessions: number;
  recentSessions: InterviewHistory[];
  progress: TopicProgress[];
  recentAccuracyTrend: number[];
}
