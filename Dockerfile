FROM openjdk:18.0.1.1

# Create a new app directory for my application files
RUN mkdir /app

ENV JAR_TO_COPY=SimpleSpringApp-2022.07.02.jar
# Copy the app files from host machine to image filesystem
COPY build/libs/${JAR_TO_COPY} /app

# Set the directory for executing future commands
WORKDIR /app

# Run the Main class
CMD java -jar ${JAR_TO_COPY}