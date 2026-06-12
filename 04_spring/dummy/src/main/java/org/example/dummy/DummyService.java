package org.example.dummy;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DummyService {
    String getTimeAsText() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    TimeDTO getTimeAsDTO() {
        return new TimeDTO();
    }
}
