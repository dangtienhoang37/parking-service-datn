package datn.service.parking.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TimeUtils {
    public int dayTime(Instant checkinTime){
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault(); // Múi giờ hiện tại
        LocalDateTime start = checkinTime.atZone(zoneId).toLocalDateTime();
        LocalDateTime end = now.atZone(zoneId).toLocalDateTime();

        // Tính tổng số giờ ban ngày và ban đêm
        int dayHours = 0;
        int nightHours = 0;
        // Duyệt từng giờ trong khoảng thời gian
        while (start.isBefore(end)) {
            int hour = start.getHour();
            if (hour >= 6 && hour < 18) {
                dayHours++;
            } else {
                nightHours++;
            }
            start = start.plusHours(1); // Tiến tới giờ tiếp theo
        }
        System.out.println("Số giờ ban ngày: " + dayHours);
        return dayHours;
    }
    public int nightTime(Instant checkinTime){
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault(); // Múi giờ hiện tại
        LocalDateTime start = checkinTime.atZone(zoneId).toLocalDateTime();
        LocalDateTime end = now.atZone(zoneId).toLocalDateTime();

        // Tính tổng số giờ ban ngày và ban đêm
        int dayHours = 0;
        int nightHours = 0;
        // Duyệt từng giờ trong khoảng thời gian
        while (start.isBefore(end)) {
            int hour = start.getHour();
            if (hour >= 6 && hour < 18) {
                dayHours++;
            } else {
                nightHours++;
            }
            start = start.plusHours(1); // Tiến tới giờ tiếp theo
        }
        System.out.println("Số giờ ban đêm: " + nightHours);

        return nightHours;
    }
}
