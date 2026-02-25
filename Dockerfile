# First Stage: Build the application
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /financeApp
# ... (Keep your optimization steps here: COPY pom.xml -> go-offline -> COPY src) ...
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Second Stage: Run the application
# CHANGE THIS LINE BELOW:
FROM eclipse-temurin:17-jre-jammy
WORKDIR /financeApp

# Copy the built JAR file
COPY --from=build /financeApp/target/*.jar financeApp.jar

# Expose the port
EXPOSE 8080

# Keep your user creation (This works because we chose 'jammy')
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

# Run the application
ENTRYPOINT ["java", "-jar", "financeApp.jar"]