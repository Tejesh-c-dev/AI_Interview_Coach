# AI Interview Coach

An AI-assisted interview preparation platform designed to help students practice **technical and behavioral interviews**, receive structured feedback, and track their interview performance.

The project combines a **React frontend**, **Spring Boot backend**, and **PostgreSQL database** to provide a modular foundation for AI-powered interview workflows.

## 🚀 Features

* User registration and authentication
* Technical mock interview sessions
* Question management and difficulty-based selection
* AI-assisted answer evaluation
* Contextual hints for technical questions
* Structured interview feedback
* Interview progress tracking
* Performance-oriented dashboard
* RESTful backend APIs
* Responsive React frontend

## 🏗️ Architecture

```text
                    ┌─────────────────────┐
                    │    React Frontend   │
                    │                     │
                    │  Interview UI       │
                    │  Dashboard          │
                    │  Question Interface │
                    └──────────┬──────────┘
                               │
                         REST APIs
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Spring Boot API   │
                    │                     │
                    │ Authentication      │
                    │ Interview Services  │
                    │ Question Services   │
                    │ Evaluation Flow     │
                    └──────────┬──────────┘
                               │
                 ┌─────────────┼─────────────┐
                 │             │             │
                 ▼             ▼             ▼
          ┌────────────┐ ┌───────────┐ ┌──────────────┐
          │ PostgreSQL │ │ AI Layer  │ │ External     │
          │            │ │           │ │ Services     │
          │ Users      │ │ Feedback  │ │              │
          │ Questions  │ │ Evaluation│ │ Code/AI APIs │
          │ Sessions   │ │ Hints     │ │              │
          └────────────┘ └───────────┘ └──────────────┘
```

## 🛠️ Tech Stack

### Frontend

* React.js
* JavaScript
* HTML
* CSS

### Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* REST APIs
* Maven

### Database

* PostgreSQL

### AI

* LLM-based interview assistance
* AI-assisted answer evaluation
* AI-generated feedback
* Contextual hint generation

### Development Tools

* Git
* GitHub
* Postman
* VS Code

## 📁 Project Structure

```text
AI_Interview_Coach/
│
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
└── README.md
```

> The exact directory structure may vary as the project evolves.

## 🔑 Core Modules

### 1. Authentication

Handles user registration, login, authorization, and protected application resources.

### 2. Interview Management

Creates and manages interview sessions including questions, answers, and session state.

### 3. Question Management

Provides a structured question bank that can be organized by:

* Topic
* Difficulty
* Interview type
* Technical domain

### 4. AI Evaluation

AI-assisted evaluation analyzes candidate responses and produces structured feedback.

### 5. Hint System

Provides contextual hints to help candidates progress through difficult technical questions without immediately revealing the complete solution.

### 6. Progress Tracking

Stores interview performance and provides users with a way to identify strengths and areas for improvement.

## 🔌 API Design

Representative REST endpoints include:

```text
POST   /api/auth/register
POST   /api/auth/login

POST   /api/sessions
GET    /api/sessions/{id}
GET    /api/sessions/{id}/next-question

POST   /api/submissions
POST   /api/submissions/{id}/hint

POST   /api/behavioral/{sessionId}/answer

GET    /api/sessions/{id}/report
GET    /api/progress/{userId}
```

The API layer is designed to keep the frontend independent from the underlying business logic and data layer.

## 🔄 Interview Workflow

```text
User Login
    │
    ▼
Select Interview Type
    │
    ▼
Create Interview Session
    │
    ▼
Receive Question
    │
    ▼
Submit Answer / Code
    │
    ▼
Evaluation
    │
    ├──────────────┐
    ▼              ▼
AI Feedback      Hint
    │              │
    └──────┬───────┘
           ▼
    Next Question
           │
           ▼
    Final Performance Report
```

## 🎯 Project Goals

The project aims to address a common problem in interview preparation: candidates often practice questions without receiving meaningful feedback on **how they approach problems, communicate solutions, and improve over time**.

AI assistance is used to make the practice experience more interactive and personalized while keeping core application logic within the backend.

## 🧠 Engineering Focus

This project is primarily focused on learning and applying:

* REST API design
* Backend architecture
* Spring Boot application development
* Database design and persistence
* Authentication and authorization
* Frontend-backend integration
* AI API integration
* Modular software architecture
* Error handling and input validation
* Git-based development

## 🖥️ Running Locally

### Prerequisites

Make sure you have installed:

* Java 17+
* Maven
* Node.js
* PostgreSQL
* Git

### Clone the repository

```bash
git clone https://github.com/Tejesh-c-dev/AI_Interview_Coach.git

cd AI_Interview_Coach
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

Configure your PostgreSQL database and required environment variables before starting the backend.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend communicates with the Spring Boot backend through REST APIs.

## 🔐 Environment Variables

Do not commit API keys, database passwords, JWT secrets, or other credentials to GitHub.

Example:

```text
DATABASE_URL=your_database_url
DATABASE_USERNAME=your_database_username
DATABASE_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

AI_API_KEY=your_ai_api_key
```

Use environment-specific configuration for local development and deployment.

## 📈 Future Improvements

* Adaptive interview difficulty
* Resume-based interview question generation
* Company-specific interview modes
* Voice-based interviews
* Advanced coding evaluation
* Detailed learning recommendations
* Long-term performance analytics
* Deployment with Docker and cloud infrastructure


## 👨‍💻 Author

**Tejesh C**

Computer Science & Engineering
Siddaganga Institute of Technology

* GitHub: https://github.com/Tejesh-c-dev
* LinkedIn: https://www.linkedin.com/in/tejesh-c-533423384/
