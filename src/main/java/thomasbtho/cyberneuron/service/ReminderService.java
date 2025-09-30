package thomasbtho.cyberneuron.service;

import thomasbtho.cyberneuron.dto.ReminderPatchRequest;
import thomasbtho.cyberneuron.dto.ReminderRequest;
import thomasbtho.cyberneuron.dto.ReminderResponse;
import thomasbtho.cyberneuron.entity.User;

import java.util.List;

public interface ReminderService {
    ReminderResponse createReminder(ReminderRequest reminderRequest, User user);

    List<ReminderResponse> getAllUserReminders(User user);

    ReminderResponse updateReminder(Long reminderId, ReminderPatchRequest patchRequest);

    void deleteReminder(Long reminderId);
}
