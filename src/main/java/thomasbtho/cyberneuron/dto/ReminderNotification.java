package thomasbtho.cyberneuron.dto;

import java.util.Date;

public record ReminderNotification(
        Long reminderId,
        Long userId,
        String title,
        Date deadline) {
}
