package thomasbtho.cyberneuron.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thomasbtho.cyberneuron.dto.NotificationResult;
import thomasbtho.cyberneuron.dto.ReminderNotification;
import thomasbtho.cyberneuron.service.NotificationService;

@Service
public class LogNotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(LogNotificationServiceImpl.class);

    @Override
    public NotificationResult send(ReminderNotification reminder) {
        log.info("Sending reminder {} to user {}", reminder.reminderId(), reminder.userId());
        return NotificationResult.success(reminder.reminderId());
    }
}
