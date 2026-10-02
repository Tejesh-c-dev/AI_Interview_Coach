import { useEffect, useState } from "react";
import { Navigate, Link } from "react-router-dom";
import { getCurrentUser, AUTH_TOKEN_KEY } from "@/services/authService";
import { getInterviewHistory, getProgressDashboard } from "@/services/progressService";
import type { InterviewHistory, ProgressDashboard } from "@/types/progress";
import { getApiErrorMessage } from "@/utils/apiError";

function formatDate(value: string | null) {
  return value ? new Date(value).toLocaleString() : "In progress";
}

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState<ProgressDashboard | null>(null);
  const [history, setHistory] = useState<InterviewHistory[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const user = await getCurrentUser();
        const [summary, sessions] = await Promise.all([
          getProgressDashboard(user.id),
          getInterviewHistory(user.id),
        ]);
        if (active) {
          setDashboard(summary);
          setHistory(sessions);
        }
      } catch (requestError) {
        if (active) setError(getApiErrorMessage(requestError));
      }
    }
    void load();
    return () => { active = false; };
  }, []);

  if (!localStorage.getItem(AUTH_TOKEN_KEY)) return <Navigate to="/login" replace />;
  if (error) return <main className="min-h-screen bg-gray-50 p-8 text-red-700">{error}</main>;
  if (!dashboard) return <main className="min-h-screen bg-gray-50 p-8 text-gray-600">Loading dashboard…</main>;

  return (
    <main className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto max-w-5xl">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-sm font-medium text-indigo-600">AI Interview Coach</p>
            <h1 className="text-3xl font-bold text-gray-900">Progress dashboard</h1>
          </div>
          <Link className="rounded-lg bg-indigo-600 px-4 py-2 font-medium text-white hover:bg-indigo-700" to="/interview">
            Start interview
          </Link>
        </div>

        <section className="mt-6 grid gap-4 sm:grid-cols-2">
          <div className="rounded-xl bg-white p-5 shadow-sm">
            <p className="text-sm text-gray-500">Total interview sessions</p>
            <p className="mt-2 text-3xl font-bold text-gray-900">{dashboard.totalSessions}</p>
          </div>
          <div className="rounded-xl bg-white p-5 shadow-sm">
            <p className="text-sm text-gray-500">Completed sessions</p>
            <p className="mt-2 text-3xl font-bold text-gray-900">{dashboard.completedSessions}</p>
          </div>
        </section>

        <section className="mt-6 rounded-xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold text-gray-900">Topic accuracy</h2>
          {dashboard.progress.some((item) => item.accuracy < 60) && (
            <p className="mt-2 text-sm text-amber-700">
              Focus next on: {dashboard.progress.filter((item) => item.accuracy < 60).map((item) => item.topic).join(", ")}
            </p>
          )}
          <div className="mt-4 space-y-4">
            {dashboard.progress.length === 0 ? (
              <p className="text-gray-500">Submit a coding solution to start tracking progress.</p>
            ) : dashboard.progress.map((item) => (
              <div key={item.topic}>
                <div className="flex justify-between text-sm">
                  <span className="font-medium text-gray-800">{item.topic}</span>
                  <span className="text-gray-500">{item.attempts} attempts · {item.accuracy.toFixed(1)}%</span>
                </div>
                <div className="mt-2 h-2 rounded-full bg-gray-100">
                  <div className={`h-2 rounded-full ${item.accuracy < 60 ? "bg-amber-500" : "bg-green-500"}`} style={{ width: `${Math.min(item.accuracy, 100)}%` }} />
                </div>
                <p className="mt-1 text-xs text-gray-400">Last practiced {formatDate(item.lastPracticed)}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="mt-6 grid gap-6 lg:grid-cols-2">
          <div className="rounded-xl bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-gray-900">Recent sessions</h2>
            <div className="mt-4 space-y-3">
              {dashboard.recentSessions.length === 0 ? <p className="text-gray-500">No sessions yet.</p> :
                dashboard.recentSessions.map((session) => (
                  <div key={session.id} className="rounded-lg border border-gray-100 p-3">
                    <div className="flex justify-between">
                      <span className="font-medium">{session.track} · {session.difficulty}</span>
                      <span className="text-sm text-gray-500">{session.status}</span>
                    </div>
                    <p className="mt-1 text-xs text-gray-500">{formatDate(session.startedAt)} · {session.submissionCount} submissions</p>
                  </div>
                ))}
            </div>
          </div>

          <div className="rounded-xl bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-gray-900">Interview history</h2>
            <div className="mt-4 space-y-3">
              {history.slice(0, 8).map((session) => (
                <div key={session.id} className="flex items-center justify-between border-b border-gray-100 pb-3 text-sm">
                  <span>{session.track} · {session.difficulty}</span>
                  <span className="text-gray-500">{session.acceptedSubmissionCount}/{session.submissionCount} accepted</span>
                </div>
              ))}
              {history.length === 0 && <p className="text-gray-500">No completed or active sessions yet.</p>}
            </div>
          </div>
        </section>

        <section className="mt-6 rounded-xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold text-gray-900">Basic accuracy trend</h2>
          <div className="mt-4 flex h-24 items-end gap-2">
            {dashboard.recentAccuracyTrend.length === 0 ? <p className="text-gray-500">Trend data will appear after submissions.</p> :
              dashboard.recentAccuracyTrend.map((value, index) => (
                <div key={`${value}-${index}`} className="flex flex-1 flex-col items-center gap-1">
                  <div className="w-full rounded-t bg-indigo-500" style={{ height: `${Math.max(value, 4)}%` }} title={`${value.toFixed(1)}%`} />
                  <span className="text-xs text-gray-400">{value.toFixed(0)}%</span>
                </div>
              ))}
          </div>
        </section>
      </div>
    </main>
  );
}
