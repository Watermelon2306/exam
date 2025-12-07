# Exam System 

This repository turns the delivery plan in `docs/system-plan.md` into a backend skeleton. The project uses a Maven multi-module layout to separate reusable components from exam-specific logic.

## Modules

- `common/common-core`: shared DTO helpers such as the `Result` wrapper.
- `exam/exam-api`: request/response contracts and enums shared by controllers and services.
- `exam/exam-core`: domain models, repositories, and application services that drive the exam lifecycle.

## Next steps

- Wire repositories to your database technology of choice (MyBatis/JPA) and implement caching per the plan.
- Expose REST controllers that delegate to `ExamAttemptService` for `start`, `save`, and `submit` flows.
- Add module-level unit tests to validate grading, state transitions, and idempotency guarantees.
- See `docs/github-sync.md` for step-by-step instructions to push this branch to your GitHub repository and open a PR.
