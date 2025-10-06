# 🧠 Cyberneuron

**Cyberneuron** is a minimalist backend API built with **Spring Boot**.  
It helps users create and manage **reminders** that trigger automatically before a given deadline.  
(Currently monolithic, with scheduling handled internally.)

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Maven
- Docker Compose

---

## ⚙️ Features

- Signup, login
- Create, read, update and delete reminders
- Automatic scheduling of reminders based on deadlines
- Basic user association (1 user → many reminders)
- Security with JWT

---

## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`JWT_SECRET`

`POSTGRES_USERNAME`

`POSTGRES_PASSWORD`

`POSTGRES_DB`

---

## 🧩 Run locally

Start the app

```bash
docker compose up -d
```

➡️ Then go to http://localhost:8080/api

## 🧪 Running Tests

```bash
./mvnw test
```
