# Spring Up - Alarm Engine Backend

A high-reliability telemetry and synchronization backend for the **Spring Up** mission-enforced Android alarm system. Built with Spring Boot, this service maintains alarm states, synchronizes mission configurations, and validates post-wake challenge telemetry.

---

## 🛠 Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 17+ / 21
- **Build Tool:** Maven (`mvnw`)
- **Persistence:** Spring Data JPA / H2 In-Memory (or PostgreSQL)
- **Networking:** RESTful APIs, JSON

---

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher installed and set to `JAVA_HOME`
- Maven (optional, wrapper script included)

### Running Locally

Run the development server using the Maven wrapper:

```bash
# Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run