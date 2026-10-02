# AI Interview Coach

An AI-assisted interview preparation platform designed to help students
practice technical and behavioral interviews, receive structured feedback, and
track their interview performance.

The project combines a React frontend, Spring Boot backend, and PostgreSQL
database to provide a modular foundation for AI-powered interview workflows.

## Current scope

Phase 1 provides the development foundation, authentication, question bank,
deterministic interview sessions, code execution, submissions, progress
tracking, interview history, and a dashboard:

* User registration and login
* BCrypt password hashing
* JWT bearer-token authentication
* Protected `GET /api/auth/me` endpoint
* Track and difficulty-based question bank
* Authenticated interview sessions with ownership enforcement
* Session progression, safe question responses, and completion
* PostgreSQL persistence with Flyway migrations
* Environment-driven database, JWT, and Judge0 configuration
* Server-side Judge0 execution with normalized deterministic verdicts
* Per-user topic attempts, accuracy, and last-practiced metrics
* Owned dashboard and interview-history APIs with a React dashboard

Adaptive difficulty, LangGraph, LLM features, and AI evaluation are not part of
Phase 1.

## Tech stack

* React, TypeScript, Vite, and Tailwind CSS
* Java 25 and Spring Boot 4.1
* Spring Web, Spring Data JPA, Spring Security, and Bean Validation
* PostgreSQL and Flyway
* Maven and Docker Compose

## Running locally

### Prerequisites

Install Java 25, Node.js, Docker Desktop, and Git.

1. Copy `.env.example` to `.env` and replace the development values. Do not
   commit `.env`.
2. Start PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

3. Start the backend:

   ```text
   backend\mvnw.cmd spring-boot:run
   ```

4. Start the frontend from the `frontend` directory:

   ```bash
   npm install
   npm run dev
   ```

The Vite development server proxies `/api` requests to the backend at
`http://localhost:8080`.

## Authentication API

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

The login response contains a bearer JWT. Send it in the
`Authorization: Bearer <token>` header when calling protected endpoints.
`JWT_SECRET` must be a Base64-encoded value containing at least 32 random
bytes; `JWT_EXPIRATION` is expressed in milliseconds.

## Interview Session API

All session endpoints require the bearer JWT returned by login. The backend
gets the session owner from the authenticated security context; clients must
not send a user ID.

```text
POST /api/sessions
GET  /api/sessions/{id}/next-question
POST /api/sessions/{id}/finish
```

Start a session with a track and difficulty:

```json
{
  "track": "DSA",
  "difficulty": "MEDIUM",
  "configuration": {
    "companyMode": "general"
  }
}
```

The question response contains only safe question fields. Hidden test cases
are retained by the question bank and are never returned by the API.

## Submission, progress, and dashboard APIs

```text
POST /api/submissions
GET  /api/progress/{userId}
GET  /api/progress/{userId}/dashboard
GET  /api/progress/{userId}/history
```

Submission requests contain `sessionId`, `questionId`, `sourceCode`, and a
supported `language` (`java`, `python`, or `cpp`). The authenticated user is
always taken from the JWT. The progress endpoints reject user IDs that do not
belong to the authenticated account.

Open `http://localhost:3000/dashboard` after login to view totals, recent
sessions, topic accuracy, attempts, weak topics (below 60%), and a basic
submission-accuracy trend.

## Project structure

```text
AI_Interview_Coach/
├── frontend/
│   ├── src/
│   └── package.json
├── backend/
│   ├── src/
│   │   ├── main/java/
│   │   └── main/resources/
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## Required environment variables

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and optionally
`JWT_EXPIRATION`, `SERVER_PORT`, `JUDGE0_BASE_URL`, `JUDGE0_API_KEY`,
`JUDGE0_API_HOST`, and `JUDGE0_POLL_TIMEOUT_MS`. `JWT_SECRET` must be a
Base64-encoded value containing at least 32 random bytes.

## Known Phase 1 limitations

* Progress is updated for coding submissions only; non-coding answers are not
  yet scored.
* Each valid submission is an attempt; Phase 1 does not provide client-side
  idempotency keys.
* Judge0 execution is synchronous and returns `PROCESSING` when polling times
  out.

## Phase 2

Implement adaptive difficulty, structured answer evaluation, LangGraph
orchestration, LLM-assisted feedback, and richer long-term analytics.
