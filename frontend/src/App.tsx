import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import RegisterPage from "@/pages/RegisterPage";
import LoginPage from "@/pages/LoginPage";
import InterviewPage from "@/pages/InterviewPage";

export default function App() {
  // Defines the application's client-side routes and fallback page.
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/interview" element={<InterviewPage />} />
        <Route path="*" element={<Navigate to="/register" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
