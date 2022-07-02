FROM openjdk:latest

# Create a new app directory for my application files
RUN mkdir /app

# Copy the app files from host machine to image filesystem
COPY build/classes/java/main /app

# Set the directory for executing future commands
WORKDIR /app

# Run the Main class
CMD java com.ak.spring.Application