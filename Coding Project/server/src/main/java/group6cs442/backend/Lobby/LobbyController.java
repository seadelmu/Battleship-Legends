package group6cs442.backend.Lobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lobby")
@CrossOrigin(origins = "http://localhost:5173") // Currently hardcoded: TODO: Move to application.properties
public class LobbyController {

    @Autowired
    private Lobbies lobbyService;

    @GetMapping("/doesLobbyExist/{lobbyCode}")
    public ResponseEntity<Boolean> doesLobbyExist(@PathVariable String lobbyCode) {
        if (lobbyService.isFull(lobbyCode)) {
            return ResponseEntity.ok(false);
        }
        boolean exists = lobbyService.exists(lobbyCode);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/createLobby")
    public Lobby createLobby(@RequestParam String password, @RequestParam int maxPlayers, @RequestParam String lobbyCode) {
        Lobby newLobby = new Lobby(lobbyCode, password, maxPlayers);
        System.out.println("New lobby created with lobby code:" + lobbyCode);
        lobbyService.addLobby(newLobby);
        return newLobby;
    }
}