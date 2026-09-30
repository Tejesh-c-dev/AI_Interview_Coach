# AI_Interview_Coach
# AI Interview Coach

## Phase 1 development

The backend uses Java 25, Spring Boot 4.1, PostgreSQL, and Flyway. PostgreSQL
is the source of truth for the schema; migrations are packaged under
`backend/src/main/resources/db/migration` and run automatically when the
application starts. Hibernate validates the migrated schema and does not
modify it.

1. Copy `.env.example` to `.env` and replace the development values. Spring
   Boot loads this optional local file, and Docker Compose uses it for the
   PostgreSQL container.
2. Start PostgreSQL with `docker compose up -d postgres`.
3. Start the backend with `backend\mvnw.cmd spring-boot:run`.
4. Start the frontend with `npm run dev` from `frontend`.

The Vite development server proxies `/api` requests to the backend at
`http://localhost:8080`. JWT and Judge0 settings are environment-driven now so
future authentication and execution modules do not need embedded credentials.

Authentication is available at `POST /api/auth/register` and
`POST /api/auth/login`. The login response contains a bearer JWT; send it as
`Authorization: Bearer <token>` to access protected endpoints such as
`GET /api/auth/me`. Set `JWT_SECRET` to a Base64-encoded value containing at
least 32 random bytes; `JWT_EXPIRATION` is expressed in milliseconds.

The current Phase 1 scope is the development foundation and authentication.
Question Bank, code execution, evaluation, progress, dashboard, and LLM
orchestration remain intentionally unimplemented.