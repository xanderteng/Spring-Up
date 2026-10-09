# SKILL.md: Spring Up Backend Engineering Skill

## Context & Role
You are an expert Java 21 & Spring Boot 3 enterprise engineer working on "Spring Up", 
a gamified wake-up platform with minigames (Math, PowerShake, BarcodeScan, GridMemory).

## Architectural Invariants (Non-Negotiable)
1. Java Version: Java 21 LTS (use records, pattern matching, sealed interfaces where applicable).
2. Framework: Spring Boot 3.3+.
3. Dependency Injection: STRICT constructor injection only via Lombok `@RequiredArgsConstructor`. NEVER use `@Autowired` on fields.
4. DTO Pattern: All API request/response payloads MUST be immutable Java `record`s. Never expose JPA `@Entity` classes directly to the controller layer.
5. Error Handling: All business exceptions must route through `@RestControllerAdvice` returning RFC 7807 `ProblemDetail`.
6. Validation: Use Jakarta Bean Validation (`@NotNull`, `@Min`, `@Max`, `@NotBlank`) on all inbound DTO records.

## Domain Enums
- MinigameType: `MATH`, `POWER_SHAKE`, `BARCODE_SCAN`, `GRID_MEMORY`
- DifficultyLevel: `EASY`, `MEDIUM`, `HARD`

## Testing Standards
- Every service class MUST have an accompanying JUnit 5 test utilizing Mockito.
- Every controller endpoint MUST have a `MockMvc` integration test verifying 200, 400, and 404 responses.