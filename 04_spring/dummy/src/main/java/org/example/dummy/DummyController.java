package org.example.dummy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    private final DummyService dummyService;

    public DummyController(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/time")
    public ResponseEntity<String> time() {
        return ResponseEntity.ok(dummyService.getTimeAsText());
    }

    @GetMapping("/time-json")
    public ResponseEntity<TimeDTO> timeJson() {
        return ResponseEntity.ok(dummyService.getTimeAsDTO());
    }
}
