import { useEffect, useState } from "react";
import { Link, Navigate } from "react-router-dom";
import Button from "@/components/Button";
import CodingWorkspace from "@/components/CodingWorkspace";
import Toast from "@/components/Toast";
import { AUTH_TOKEN_KEY } from "@/services/authService";
import { finishSession, getNextQuestion, startSession } from "@/services/interviewSessionService";
import { getApiErrorMessage } from "@/utils/apiError";
import type { QuestionDifficulty, QuestionTrack } from "@/types/questionBank";
import type { NextQuestionResponse } from "@/types/interviewSession";

const tracks: QuestionTrack[]           = ["DSA", "BEHAVIORAL", "SYSTEM_DESIGN", "OOP", "JAVA"];
const difficulties: QuestionDifficulty[] = ["EASY", "MEDIUM", "HARD"];
const INTERVIEW_STATE_KEY = "ai_coach_active_interview";

type SavedInterviewState = {
  sessionId: string;
  current: NextQuestionResponse;
};

function loadSavedInterviewState(): SavedInterviewState | null {
  const saved = localStorage.getItem(INTERVIEW_STATE_KEY);
  if (!saved) return null;

  try {
    const state = JSON.parse(saved) as SavedInterviewState;
    if (typeof state.sessionId === "string" && state.current?.question?.prompt) {
      return state;
    }
  } catch {
    // Discard an incomplete snapshot and let the user start a new session.
  }
  localStorage.removeItem(INTERVIEW_STATE_KEY);
  return null;
}

export default function InterviewPage() {
  const savedState = loadSavedInterviewState();
  const [track,      setTrack]      = useState<QuestionTrack>("DSA");
  const [difficulty, setDifficulty] = useState<QuestionDifficulty>("MEDIUM");
  const [sessionId,  setSessionId]  = useState<string | null>(savedState?.sessionId ?? null);
  const [current,    setCurrent]    = useState<NextQuestionResponse | null>(savedState?.current ?? null);
  const [busy,       setBusy]       = useState(false);
  const [error,      setError]      = useState("");
  const [finished,   setFinished]   = useState(false);

  useEffect(() => {
    if (sessionId && current) {
      localStorage.setItem(INTERVIEW_STATE_KEY, JSON.stringify({ sessionId, current }));
    } else if (!sessionId) {
      localStorage.removeItem(INTERVIEW_STATE_KEY);
    }
  }, [sessionId, current]);

  if (!localStorage.getItem(AUTH_TOKEN_KEY)) return <Navigate to="/login" replace />;

  const run = async (action: () => Promise<void>) => {
    setBusy(true);
    setError("");
    try {
      await action();
    } catch (requestError) {
      setError(getApiErrorMessage(requestError));
    } finally {
      setBusy(false);
    }
  };

  const start = () => run(async () => {
    const session = await startSession({ track, difficulty });
    setSessionId(session.id);
    setFinished(false);
    setCurrent(await getNextQuestion(session.id));
  });

  const next = () => sessionId && run(async () => setCurrent(await getNextQuestion(sessionId)));

  const finish = () => sessionId && run(async () => {
    await finishSession(sessionId);
    setFinished(true);
    setCurrent(null);
    localStorage.removeItem(INTERVIEW_STATE_KEY);
  });

  const isCodingQuestion = current?.question.questionType === "CODING";

  return (
    <main className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto w-full max-w-4xl">
        <div className="flex items-center justify-between gap-3">
          <h1 className="text-2xl font-bold text-gray-900">Interview Session</h1>
          <Link className="text-sm font-medium text-indigo-600 hover:text-indigo-800" to="/dashboard">
            View dashboard
          </Link>
        </div>

        {error && (
          <div className="mt-4">
            <Toast type="error" message={error} onDismiss={() => setError("")} />
          </div>
        )}

        {!sessionId ? (
          /* ── Session setup ─────────────────────────────────────────────── */
          <section className="mt-6 rounded-xl bg-white p-8 shadow-sm space-y-4">
            <label className="block text-sm font-medium text-gray-700">
              Track
              <select
                className="mt-1 w-full rounded-lg border p-2.5"
                value={track}
                onChange={(e) => setTrack(e.target.value as QuestionTrack)}
              >
                {tracks.map((opt) => (
                  <option key={opt} value={opt}>
                    {opt.replace("_", " ")}
                  </option>
                ))}
              </select>
            </label>
            <label className="block text-sm font-medium text-gray-700">
              Difficulty
              <select
                className="mt-1 w-full rounded-lg border p-2.5"
                value={difficulty}
                onChange={(e) => setDifficulty(e.target.value as QuestionDifficulty)}
              >
                {difficulties.map((opt) => (
                  <option key={opt}>{opt}</option>
                ))}
              </select>
            </label>
            <Button isLoading={busy} loadingText="Starting interview…" onClick={start}>
              Start Interview
            </Button>
          </section>
        ) : (
          /* ── Active session ────────────────────────────────────────────── */
          <div className="mt-6">
            {finished ? (
              <div className="rounded-lg bg-green-50 p-6 text-green-800 font-medium text-center">
                🎉 Interview session completed successfully.
              </div>
            ) : current ? (
              isCodingQuestion ? (
                /* Coding question → full workspace */
                <CodingWorkspace
                  sessionId={sessionId}
                  question={current.question}
                  questionNumber={current.questionNumber}
                  onNext={next}
                  onFinish={finish}
                />
              ) : (
                /* Non-coding question → simple prompt display */
                <section className="rounded-xl bg-white p-8 shadow-sm">
                  <p className="text-sm text-gray-400">
                    Question {current.questionNumber} · {current.question.topic}
                  </p>
                  <h2 className="mt-3 text-xl font-semibold text-gray-900">
                    {current.question.prompt}
                  </h2>
                  {current.question.starterCode && (
                    <pre className="mt-4 overflow-x-auto rounded-lg bg-gray-900 p-4 text-sm text-white">
                      {current.question.starterCode}
                    </pre>
                  )}
                  <div className="mt-6 flex gap-3">
                    <Button className="flex-1" isLoading={busy} loadingText="Loading…" onClick={next}>
                      Next Question
                    </Button>
                    <Button
                      className="flex-1 bg-gray-700 hover:bg-gray-800"
                      disabled={busy}
                      onClick={finish}
                    >
                      Finish Session
                    </Button>
                  </div>
                </section>
              )
            ) : null}
          </div>
        )}
      </div>
    </main>
  );
}
