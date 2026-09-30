import { useState } from "react";
import { Navigate } from "react-router-dom";
import Button from "@/components/Button";
import Toast from "@/components/Toast";
import { AUTH_TOKEN_KEY } from "@/services/authService";
import { finishSession, getNextQuestion, startSession } from "@/services/interviewSessionService";
import { getApiErrorMessage } from "@/utils/apiError";
import type { QuestionDifficulty, QuestionTrack } from "@/types/questionBank";
import type { NextQuestionResponse } from "@/types/interviewSession";

const tracks: QuestionTrack[] = ["DSA", "BEHAVIORAL", "SYSTEM_DESIGN", "OOP", "JAVA"];
const difficulties: QuestionDifficulty[] = ["EASY", "MEDIUM", "HARD"];

export default function InterviewPage() {
  const [track, setTrack] = useState<QuestionTrack>("DSA");
  const [difficulty, setDifficulty] = useState<QuestionDifficulty>("MEDIUM");
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [current, setCurrent] = useState<NextQuestionResponse | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [finished, setFinished] = useState(false);

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
  });

  return (
    <main className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-8">
      <section className="w-full max-w-2xl rounded-xl bg-white p-8 shadow-sm">
        <h1 className="text-2xl font-bold text-gray-900">Interview Session</h1>
        {error && <div className="mt-4"><Toast type="error" message={error} onDismiss={() => setError("")} /></div>}
        {!sessionId ? (
          <div className="mt-6 space-y-4">
            <label className="block text-sm font-medium text-gray-700">
              Track
              <select className="mt-1 w-full rounded-lg border p-2.5" value={track} onChange={(event) => setTrack(event.target.value as QuestionTrack)}>
                {tracks.map((option) => <option key={option} value={option}>{option.replace("_", " ")}</option>)}
              </select>
            </label>
            <label className="block text-sm font-medium text-gray-700">
              Difficulty
              <select className="mt-1 w-full rounded-lg border p-2.5" value={difficulty} onChange={(event) => setDifficulty(event.target.value as QuestionDifficulty)}>
                {difficulties.map((option) => <option key={option}>{option}</option>)}
              </select>
            </label>
            <Button isLoading={busy} loadingText="Starting interview..." onClick={start}>Start Interview</Button>
          </div>
        ) : (
          <div className="mt-6">
            {finished ? (
              <div className="rounded-lg bg-green-50 p-4 text-green-800">Interview completed successfully.</div>
            ) : current ? (
              <>
                <p className="text-sm text-gray-500">Question {current.questionNumber} · {current.question.topic}</p>
                <h2 className="mt-3 text-xl font-semibold text-gray-900">{current.question.prompt}</h2>
                {current.question.starterCode && <pre className="mt-4 overflow-x-auto rounded-lg bg-gray-900 p-4 text-sm text-white">{current.question.starterCode}</pre>}
                <div className="mt-6 flex gap-3">
                  <Button className="flex-1" isLoading={busy} loadingText="Loading..." onClick={next}>Next Question</Button>
                  <Button className="flex-1 bg-gray-700 hover:bg-gray-800" disabled={busy} onClick={finish}>Finish Session</Button>
                </div>
              </>
            ) : null}
          </div>
        )}
      </section>
    </main>
  );
}
