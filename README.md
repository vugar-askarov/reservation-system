# Reservation System API

A RESTful backend API for reservation and availability management built with Java 21 and Spring Boot.

## Tech Stack
* **Core:** Java 21
* **Framework:** Spring Boot (Data JPA, MVC)
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **DevOps:** Docker

## Key Features
* **Reservation Management:** Full lifecycle management of bookings with status tracking (`ReservationStatus`).
* **Advanced Filtering:** Flexible reservation search and filtering capabilities using `ReservationSearchFilter`.
* **Availability Tracking:** Real-time status checks (`AVAILABLE`, `RESERVED`) and availability management.
* **Data Mapping:** Decoupled Entity and DTO layers using dedicated mappers for clean data transfer.
* **Global Exception Handling:** Centralized error handling mechanism ensuring consistent API responses.

