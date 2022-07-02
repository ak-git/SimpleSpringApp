FROM openjdk:latest

# Create a new app directory for my application files
RUN mkdir /app

# Copy the app files from host machine to image filesystem
COPY build/libs /app

# Set the directory for executing future commands
WORKDIR /app

# Run the Main class
CMD java -jar SimpleSpringApp-2022.07.02.jar