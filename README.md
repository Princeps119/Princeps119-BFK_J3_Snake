# BFK_J3_Snake

## Robin Mössinger, Maximilian Wagner, Nico Wieland, Jan Hauf

## Robin Mössinger, Maximilian Wagner, Nico Wieland, Jan Hauf

## Run the backend locally and with Docker

This project contains a Java backend (HTTP server on port 8080) and a MongoDB database. You can run it either with Docker (recommended) or locally using Maven/Java.

- Base URL (backend): http://localhost:8080
- For development API requests can be tested with POSTMAN, ask Robin for the examples 
- CORS: enabled for all origins. Allowed methods: GET, POST, PUT, DELETE, OPTIONS.

### Dockerized setup (recommended)

Prerequisites:
- Docker and Docker Compose

Commands (execute from project root):
1. Build and start the stack (backend + MongoDB) in the background:
   - Linux/macOS: `docker compose -f docker/docker-compose.yml up --build -d`
2. Check logs (optional):
   - Backend: `docker logs -f snakebackendcontainer`
   - MongoDB: `docker logs -f mongoDB`
3. Stop the stack:
   - `docker compose -f docker/docker-compose.yml down`

Details:
- The backend container exposes:
  - 8080/tcp → HTTP API
- MongoDB runs in a separate container and is exposed on host port 27018.
- Secrets/files mounted by Compose (see `docker/docker-compose.yml`):
  - `backend/src/main/resources/uri.txt` → Mongo connection string (as a Docker secret)
  - `backend/src/main/resources/encryption.txt` → Encryption key (as a Docker secret)

### Local development without Docker

Prerequisites:
- Java 21 (Amazon Corretto or OpenJDK)
- Maven 3.9+
- A running MongoDB, or adjust `backend/src/main/resources/uri.txt` to point to your Mongo instance

Steps:
1. Build the project:
   - `mvn clean package -DskipTests`
2. Run the backend:
   - run the `Main` class
   - From CLI: `java -cp target/BFK_J3_Snake-1.0-SNAPSHOT.jar:target/lib/* Main`
3. Verify health:
   - `curl -i http://localhost:8080/api/checkBackend`

Note: The server listens on port 8080 by default.

---

## Backend API Documentation

- Base URL: `http://localhost:8080`
- Common requirements:
  - For POST endpoints, set header: `Content-Type: application/json`
  - CORS is open (`Access-Control-Allow-Origin: *`).

### 1) Check Backend Health
- Method: `GET`
- Path: `/api/checkBackend`
- Request body: none
- Responses:
  - `200 OK` when backend is reachable

Example:
```
curl -i http://localhost:8080/api/checkBackend
```

### 2) User Login
- Method: `POST`
- Path: `/api/login`
- Headers:
  - `Content-Type: application/json`
- Request body (JSON):
  ```json
  {
    "mail": "user@example.com",
    "password": "yourPlainTextPassword"
  }
  ```
- Successful response:
  - Status: `200 OK`
  - Header: `Content-Type: application/json`
  - Body (JSON), is the Token which is used to validate save and delete requests :
    ```json
    {
      "username": "yourUsername",
      "encryptedMail": "<encrypted string>",
      "timestamp": "<encrypted string>"
    }
    ```
- Error responses (examples):
  - `400 Bad Request` if JSON is invalid or required fields are missing
  - `401 Unauthorized` if credentials are invalid
  - `415 Unsupported Media Type` if `Content-Type` is not `application/json`
  - `405 Method Not Allowed` if method is not `POST`
  - `500 Internal Server Error` for unexpected errors

Example cURL:
```
curl -i \
  -H "Content-Type: application/json" \
  -d '{"mail":"user@example.com","password":"secret"}' \
  http://localhost:8080/api/login
```

### 3) User Registration
- Method: `POST`
- Path: `/api/register`
- Headers:
  - `Content-Type: application/json`
- Request body (JSON):
  ```json
  {
    "username": "yourUsername",
    "mail": "user@example.com",
    "password": "yourPlainTextPassword"
  }
  ```
- Successful response:
  - Status: `204 No Content` (no body)
- Error responses (examples):
  - `400 Bad Request` for invalid JSON or validation failures
  - `500 Internal Server Error` for unexpected errors

Example cURL:
```
curl -i \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","mail":"alice@example.com","password":"secret"}' \
  http://localhost:8080/api/register
```

### 4) Save Endpoint (placeholder)
- Path: `/api/save`
- Status: currently not implemented in the backend. Calling this endpoint will not perform any action.

---

### 5) Delete Endpoint (placeholder)
- Path: `/api/delete`
- Status: currently not implemented in the backend. Calling this endpoint will not perform any action.

---


## Notes for Frontend Integration
- Always include `Content-Type: application/json` for POST requests.
- Expect CORS headers to be present so that browser-based clients can communicate with the backend.