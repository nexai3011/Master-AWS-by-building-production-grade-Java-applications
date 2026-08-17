# Contributing

This repository is designed as a professional, Java-first AWS learning resource. Contributions should keep the content practical, production-minded, and aligned with engineering realities.

## Contribution principles

- focus on real Java/Spring Boot use cases
- prefer production-grade architecture over toy examples
- include security, cost, and cleanup guidance in labs
- keep examples executable and documented
- explain the trade-offs behind AWS design choices

## Lab format

Every new lab should include:

- README.md
- architecture.md
- src/
- docker/
- terraform/
- scripts/
- tests/

## Required README sections

Each lab should have:

1. What the lab builds
2. Why it matters for Java developers
3. AWS services used
4. Architecture diagram
5. Prerequisites
6. Project structure
7. Step-by-step implementation
8. Java and Spring Boot code
9. AWS configuration
10. Deployment
11. Testing
12. Monitoring
13. Security notes
14. Cost considerations
15. Common mistakes
16. Production improvements
17. Interview questions
18. Cleanup steps

## Quality bar

Before contributing:

- verify the code and commands are current
- prefer AWS best practices over shortcuts
- keep instructions cost-aware and safe
- add cleanup instructions to all AWS labs
- document assumptions clearly

## Suggested contribution areas

- Java integration with AWS SDKs
- production deployment patterns
- Terraform/CDK examples
- troubleshooting guides
- system design case studies
- interview question banks
- observability and performance deep dives

## Pull request expectations

- include a clear title and summary
- describe the learning objective
- link the relevant lab or topic
- explain any AWS cost implications
- include cleanup guidance when creating resources
