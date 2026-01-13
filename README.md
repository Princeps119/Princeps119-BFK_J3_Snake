# BFK_J3_Snake

## Robin Mössinger, Maximilian Wagner, Nico Wieland, Jan Hauf

## Run the backend locally and with Docker

This project contains a Java backend (HTTP server on port 8080) and a MongoDB database .
As well as a Frontend directory where the html pages are served (HTTP server on port 8080), when using the docker
implementation.
You can run it either with Docker (recommended) or locally using Maven/Java.
For it to work you need these files which are not checked in git as per security reasons:
`backend/src/main/resources/config.properties`
`backend/src/main/resources/encryption.txt`
`backend/src/main/resources/uri.txt`

- Base URL (frontend): http://localhost:3000
- Base URL (backend): http://localhost:8080
- For development API requests can be tested with POSTMAN, ask Robin for the examples 
- CORS: enabled for all origins. Allowed methods: GET, POST, PUT, PATCH, DELETE, OPTIONS.

### Dockerized setup (recommended)

Prerequisites:
- Docker and Docker Compose

Commands (execute from project root):
1. Build and start the stack (backend + MongoDB) in the background:
    - `docker compose -f docker/docker-compose.yml up --build -d`
2. Check logs (optional):
   - Backend: `docker logs -f snakebackendcontainer`
   - MongoDB: `docker logs -f mongoDB`
3. Stop the stack:
   - `docker compose -f docker/docker-compose.yml down`

Details:
- The backend container exposes:
    - 8080 → HTTP API
    - 3000 → HTTP API (for the Frontend, which the user interacts with)
    - 5005 -> enables remote debugging of the running docker application via intellij
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
      "timestamp": "<encrypted string>", 
      "version": "uuid saved as String"
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
- Status: currently not implemented in the frontend. Calling this endpoint will not perform any action as the data which
  the frontend should provide this endpoint is not done

---

### 5) Delete Endpoint (placeholder)

- Method: `DELETE`
- Path: `/api/register`

- Header: `Authorization`
    - contains the Logintoken in the Authorization Header to check against DB to verify that a logged in user send the
      delete request:
  ```json
  {
    "username": "yourUsername",
    "encryptedMail": "<encrypted string>",
    "timestamp": "<encrypted string>", 
    "version": "uuid saved as String"
  }

- Successful response:
    - Status: `204 No Content` (no body)
- Error response:
    - `500 Internal Server Error`
---


## Notes for Frontend Integration
- Always include `Content-Type: application/json` for POST requests.
- Expect CORS headers to be present so that browser-based clients can communicate with the backend.

### Endpoints:

- `localhost:3000/login` after successful login user is redirected to snake page as
- `localhost:3000/register` after successful registration user is redirected to login page
- `localhost:3000/snake` after successful login user is redirected to snake page with his useraccount, if called
  directly user plays as a guest