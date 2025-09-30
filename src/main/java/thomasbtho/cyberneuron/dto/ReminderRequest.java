package thomasbtho.cyberneuron.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ReminderRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotNull(message = "Deadline is required")
        @Future(message = "Deadline must be in the future")
        Date deadline) {
}
