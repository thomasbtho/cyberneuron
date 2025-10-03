package thomasbtho.cyberneuron.service;

import thomasbtho.cyberneuron.dto.NotificationResult;
import thomasbtho.cyberneuron.dto.ReminderNotification;

public interface NotificationService {
    NotificationResult send(ReminderNotification reminder);
}
