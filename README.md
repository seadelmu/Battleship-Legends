
<p align="center">
  <img src="\Coding Project\client\public\bote.png" alt="Battleship Legends Logo" width="200"/>
</p>

# BattleshipLegends

Multiplayer Battleship Game

## Overview

BattleshipLegends is an exciting multiplayer battleship game built using modern web technologies. Challenge your friends or players around the world in this classic game of strategy and skill.

## Preview

<p align="center">
  <img src="\Coding Project\Assets\preview-screenshots\LobbyScreen.png" alt="Lobby Screen" width="500" style="display:inline-block; margin-right: 10px;"/>
  <img src="\Coding Project\Assets\preview-screenshots\GameplayScreen.png" alt="Gameplay Screen" width="500" style="display:inline-block;"/>
</p>


## Features
- **Multiplayer Mode**: Play against friends online.
- **Real-time Communication**: Chat with your friends in real-time in the lobby.
- **Turn System**: Take turns attacking your opponent.
- **Reward/Point System**: Earn points for drowning ships and earn power ups.

## Technologies Used

- **Frontend**: React, Vite, Javascript, Typescript
- **Backend**: Java, Springboot
- **Testing Frameworks**: JUnit 5
- **Real-time Communication**: Spring Sockets

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- Node.js and npm installed
- Java installed and running >= JDK 17

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/p-rinceS/BattleshipLegends.git
    cd BattleshipLegends
    ```

2. Install dependencies for the client and server:
    ```bash
    cd '.\Coding Project\client\'
    npm install
    ```

### Running the Application

1. Start the backend server:
    ```bash
    cd '.\Coding Project\server\' 
    mvn spring-boot:run
    ```
   You can also run the project as a spring boot application via your IDE.



2. Start the frontend development server:
    ```bash
    cd '.\Coding Project\client\'
    npm run dev
    ```

3. Open your browser and navigate to `http://localhost:5173` to play the game.

### Testing the Application
```bash
  cd '.\Coding Project\server\' 
  mvn test
```


## Contributing

We welcome contributions to BattleshipLegends! To contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch with your feature or bugfix.
3. Commit your changes and push the branch to your fork.
4. Submit a pull request with a detailed description of your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

Created by Raymond, Seann & Prince.
