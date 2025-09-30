package thomasbtho.cyberneuron.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import thomasbtho.cyberneuron.entity.ReminderStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class ReminderPatchRequest {
    private String title;
    private Date deadline;
    private ReminderStatus status;
}