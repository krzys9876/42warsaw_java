package org.example.dummy;


import java.time.LocalDateTime;

public class TimeDTO {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public TimeDTO(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public TimeDTO() {
        var now = LocalDateTime.now();
        year = now.getYear();
        month = now.getMonthValue();
        day = now.getDayOfMonth();
        hour = now.getHour();
        minute = now.getMinute();
        second = now.getSecond();
    }
}
