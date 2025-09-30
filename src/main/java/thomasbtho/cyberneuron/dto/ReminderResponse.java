package thomasbtho.cyberneuron.dto;

import thomasbtho.cyberneuron.entity.ReminderStatus;

import java.util.Date;

public record ReminderResponse(
        Long id,
        Long userId,
        String title,
        Date deadline,
        ReminderStatus status) {
}
