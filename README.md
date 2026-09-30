# AI Interview Coach

An AI-assisted interview preparation platform designed to help students
practice technical and behavioral interviews, receive structured feedback, and
track their interview performance.

The project combines a React frontend, Spring Boot backend, and PostgreSQL
database to provide a modular foundation for AI-powered interview workflows.

## Current scope

Phase 1 provides the development foundation, authentication, question bank,
and deterministic interview sessions:

* User registration and login
* BCrypt password hashing
* JWT bearer-token authentication
* Protected `GET /api/auth/me` endpoint
* Track and difficulty-based question bank
* Authenticated interview sessions with ownership enforcement
* Session progression, safe question responses, and completion
* PostgreSQL persistence with Flyway migrations
* Environment-driven database, JWT, and Judge0 configuration

Code execution, evaluation, progress, dashboard, and LLM orchestration remain
intentionally unimplemented.

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

## Future improvements

* Structured answer capture for interview sessions
* AI-assisted answer evaluation and contextual hints
* Code execution integration
* Progress tracking and performance dashboards
* Resume-based and company-specific interview modes
* Voice-based interviews and long-term learning analytics
