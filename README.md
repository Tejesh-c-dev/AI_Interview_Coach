# AI Interview Coach

An AI-assisted interview preparation platform designed to help students
practice technical and behavioral interviews, receive structured feedback, and
track their interview performance.

The project combines a React frontend, Spring Boot backend, and PostgreSQL
database to provide a modular foundation for AI-powered interview workflows.

## Current scope

Phase 1 provides the development foundation and authentication:

* User registration and login
* BCrypt password hashing
* JWT bearer-token authentication
* Protected `GET /api/auth/me` endpoint
* PostgreSQL persistence with Flyway migrations
* Environment-driven database, JWT, and Judge0 configuration

Question Bank, code execution, evaluation, progress, dashboard, and LLM
orchestration remain intentionally unimplemented.

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

* Technical and behavioral interview sessions
* Structured question bank with difficulty-based selection
* AI-assisted answer evaluation and contextual hints
* Code execution integration
* Progress tracking and performance dashboards
* Resume-based and company-specific interview modes
* Voice-based interviews and long-term learning analytics
