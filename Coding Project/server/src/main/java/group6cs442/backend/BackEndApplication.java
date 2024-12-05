package group6cs442.backend;

import group6cs442.backend.Lobby.Lobbies;
import group6cs442.backend.Lobby.Lobby;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BackEndApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BackEndApplication.class, args);

		Lobbies lobbyService = context.getBean(Lobbies.class);
		Lobby defaultLobby = new Lobby("1234", "defaultPassword", 4);
		lobbyService.addLobby(defaultLobby);
	}


}