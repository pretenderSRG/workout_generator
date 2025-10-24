# Fitness Program Generator (Basic API)

A Spring Boot REST API for generating personalized fitness programs based on equipment and schedule.

## Features

- User registration and authentication (HTTP Basic)
- Generate workout programs by equipment type and days per week
- Store programs in database (H2) linked to users
- Retrieve all workout programs for authenticated user
- Clean JSON responses using DTOs (no circular references)

## Endpoints

### Authentication

- `POST /api/auth/register`  
  Register a new user  
  Body: `{"username": "string", "password": "string"}`

### Workout Programs

- `POST /api/workouts/generate?equipment={type}&daysPerWeek={num}`  
  Generate a new workout program  
  Requires authentication

- `GET /api/workouts`  
  Get all workout programs for authenticated user  
  Requires authentication

### Equipment Types

- `BODYWEIGHT`
- `DUMBBELLS`
- `BARBELL`
- `RESISTANCE_BANDS`
- `GYM`

## Running

```bash
./gradlew bootRun

```

## Database
- H2 file-based database stored in `./data/fitnessdb`
- Access H2 Console at: `http://localhost:8080/h2-console`
(Stop the app first to access the database)


## Tech Stack
- Java 17
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- Lombok
- Jackson (DTO serialization)

## Example Request
```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "1", "password": "password"}'

# Generate program (requires Basic Auth)
curl -X POST "http://localhost:8080/api/workouts/generate?equipment=GYM&daysPerWeek=4" \
  -H "Authorization: Basic MTpwYXNzd29yZA==" \
  -H "Content-Type: application/json"

# Get all programs
curl -X GET "http://localhost:8080/api/workouts" \
  -H "Authorization: Basic MTpwYXNzd29yZA=="
```