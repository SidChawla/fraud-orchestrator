# fraud-orchestrator

This is a springboot application that acts as an entry point behind Elastic Load Balancer (ALB) and interacts with the AWS Fraud detector. Once the Risk Outcome response is received, it is transfered to event-manager-lambda.

## Tech Stack
- Java 17
- Springboot 2.7.11
- AWS SDK 2.x
- Gradle

## Code Build
gradle build

## Code Run
java -jar fraud-orchestrator-0.0.1-SNAPSHOT