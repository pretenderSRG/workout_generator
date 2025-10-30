# Fitness Program Generator (Smart-Generation)

An enhanced version of the Fitness Program Generator with **intelligent workout generation** based on rules, muscle group balancing, and user preferences.

## Features

- All features from [basic-api](../basic-api) branch:
  - User registration and authentication
  - Generate workout programs by equipment and days per week
  - Retrieve, update, and delete workout programs
- **Smart workout generation**:
  - Balances muscle groups across days
  - Implements Push/Pull/Legs (PPL) or other training splits
  - Avoids repetitive exercises in adjacent days
  - Considers exercise difficulty and equipment availability
- **Rule-based engine** for creating balanced and effective programs

## Endpoints
Same as [basic-api](../basic-api), with potential future additions for:
- `POST /api/workouts/generate-smart` - Generate a program using smart rules


## Tech Stack
- Java 17
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- Lombok
- Jackson

## Example Request
Same as [basic-api](../basic-api) , but the generation logic will be smarter.

## Next Steps
- Implement PPL (Push/Pull/Legs) split logic
- Add exercise difficulty and category (compound/isolation)
- Integrate with a frontend (optional)