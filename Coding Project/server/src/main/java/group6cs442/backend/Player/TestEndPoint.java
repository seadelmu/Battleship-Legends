package group6cs442.backend.Player;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestEndPoint {
        @GetMapping("/hello")
        public String sayHello() {
            return "Hello World";
        }

}
