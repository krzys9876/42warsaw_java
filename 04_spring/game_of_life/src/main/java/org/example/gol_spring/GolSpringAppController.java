package org.example.gol_spring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class GolSpringAppController {
    private final GolSpringAppService golSpringAppService;

    public GolSpringAppController(GolSpringAppService golSpringAppService) {
        this.golSpringAppService = golSpringAppService;
    }

    @GetMapping("/time")
    public ResponseEntity<String> getTime() {
        return ResponseEntity.ok(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @GetMapping("/init")
    public ResponseEntity<GolDTO> init() {
        return ResponseEntity.ok(golSpringAppService.initGameGliderGun());
    }

    @PostMapping("/cycle")
    public ResponseEntity<GolDTO> cycle(@RequestBody GolDTO currentState) {
        return ResponseEntity.ok(golSpringAppService.cycleGame(currentState));
    }
}
