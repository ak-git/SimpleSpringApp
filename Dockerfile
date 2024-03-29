FROM openjdk:20

# Create a new app directory for my application files
RUN mkdir /app

ENV JAR_TO_COPY=SimpleSpringApp-2023.12.21.jar
# Copy the app files from host machine to image filesystem
COPY build/libs/${JAR_TO_COPY} /app

## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
RUN chmod +x /wait

# Set the directory for executing future commands
WORKDIR /app
EXPOSE 8080/tcp

# Run the Main class
CMD /wait && java -jar ${JAR_TO_COPY}