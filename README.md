# Spring Up - Alarm Engine Backend

An Alarm engine designed to help me wake up

---

## 🛠 Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 21
- **Build Tool:** Maven (`mvnw`)
- **Persistence:** Spring Data JPA / H2 In-Memory (or PostgreSQL)
- **Networking:** RESTful APIs, JSON

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 or higher installed and set to `JAVA_HOME`
- Maven (optional, wrapper script included)

### Running Locally

Run the development server using the Maven wrapper:

```bash
# Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

The server binds to http://localhost:8080.

📱 Mobile Device Connection (ADB Reverse)
When testing with a physical Android device connected over USB, establish a reverse TCP bridge so the mobile app can reach http://localhost:8080:

PowerShell
adb reverse tcp:8080 tcp:8080
Verify active bridges:

PowerShell
adb reverse --list
(Note: Android emulators do not require adb reverse; route requests to http://10.0.2.2:8080 instead).

📡 REST API Reference
1. Alarm Synchronization
GET /api/v1/alarms
Fetches all configured alarms.

POST /api/v1/alarms
Persists a newly created alarm and assigns a backend identifier.

Request Payload:

JSON
{
  "title": "Morning Wake Up",
  "alarmTime": "06:30:00",
  "isEnabled": true,
  "repeatDaysMask": 31,
  "wakeUpCheckEnabled": true,
  "wakeUpCheckDelayMinutes": 5,
  "missions": [
    {
      "stepOrder": 1,
      "minigame": "MATH",
      "difficulty": 1,
      "requiredCompletions": 3,
      "targetBarcodeHash": null
    },
    {
      "stepOrder": 2,
      "minigame": "POWER_SHAKE",
      "difficulty": 1,
      "requiredCompletions": 25,
      "targetBarcodeHash": null
    }
  ],
  "vibrate": true,
  "volumeLevel": 100
}
Response (200 OK):

JSON
{
  "id": 1,
  "title": "Morning Wake Up",
  "alarmTime": "06:30:00",
  "isEnabled": true,
  "repeatDaysMask": 31,
  "wakeUpCheckEnabled": true,
  "wakeUpCheckDelayMinutes": 5,
  "missions": [...],
  "vibrate": true,
  "volumeLevel": 100
}
DELETE /api/v1/alarms/{id}
Deletes an alarm by its server ID.

2. Mission Verification & Telemetry
POST /api/v1/minigames/verify
Logs and verifies mission completion telemetry submitted by the Android client upon completing a mission gauntlet.

Request Payload:

JSON
{
  "alarmId": 1,
  "minigameType": "MISSION_GAUNTLET",
  "submittedSolution": "PASSED",
  "submittedSequence": null,
  "secondsTaken": 30,
  "snoozeCount": 0
}
Response (200 OK):

JSON
{
  "isDismissed": true,
  "currentStreak": 1,
  "message": "Challenge verified successfully."
}