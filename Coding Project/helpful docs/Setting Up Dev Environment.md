
# Project Overview

**Battleship Battle Royale**:
- Frontend:
  - Vite, React, Javascript
  - Tailwind CSS
- Backend:
  - Java
  - Spring Boot

# Backend Setup

## Download and install JDK 22 for windows or macOS
    https://www.oracle.com/java/technologies/downloads/#jdk22-windows
- Add JDK to your PATH
- Verify installation by running `java -version` in your terminal
- might run into some compatability issues with the version of java you have installed
  - if so, download the version of java that is compatible with the version of spring boot you are using

## Download and install maven for dependency management
    https://maven.apache.org/download.cgi
for windows download binary zip archive and extract it to a folder
- for macOS download binary tar.gz archive and extract it to a folder
- Add maven to your environment variables
    - make user variable called M2_HOME and MAVEN_HOME and set it to the path of the extracted folder
    - add %M2_HOME%\bin to your PATH in system variables
- Verify installation by running 'mvn -v' in your terminal


# Frontend Setup


## Download and install Node.js
    https://nodejs.org/en/download/

- Verify installation by running `node -v` and `npm -v` in your terminal
- make sure it's >= 10.8.3

## Go into client folder
    cd client
    npm install
    npm run dev
    
- This will start the frontend server on localhost:5173

    
## To build the backend
  - Open the project in your IDE
  - Run the config.
    - Do `mvn spring-boot:run` in the terminal (make sure you're in server directory)
    - Or run the project as a spring boot application via your IDE
  - This will start the backend server on localhost:8080
    - do curl http://localhost:8080/api/hello to verify that the server is running
      - you should see a response of `Hello World` in your terminal

## To build the frontend
  - Open the project in your IDE
  - Do `npm run dev `in the terminal (make sure you're in client directory)
  - This will start the frontend server on localhost:5173


    
    