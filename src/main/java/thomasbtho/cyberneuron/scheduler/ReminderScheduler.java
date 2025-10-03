package thomasbtho.cyberneuron.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import thomasbtho.cyberneuron.dto.ReminderNotification;
import thomasbtho.cyberneuron.entity.Reminder;
import thomasbtho.cyberneuron.entity.ReminderStatus;
import thomasbtho.cyberneuron.repository.ReminderRepository;
import thomasbtho.cyberneuron.service.NotificationService;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {
    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;

    private final int batch = 20;

    @Scheduled(fixedDelayString = "${reminder.scheduler.delay}")
    public void run() {
        List<Reminder> scheduledReminders = reminderRepository.findByStatusAndDeadlineBefore(ReminderStatus.SCHEDULED, Instant.now().plusSeconds(3600), Pageable.ofSize(batch));

        for (Reminder reminder : scheduledReminders) {
            reminder.setStatus(ReminderStatus.SENDING);
            reminderRepository.save(reminder);

            if (notificationService.send(convertToNotification(reminder)).success()) {
                reminder.setStatus(ReminderStatus.SENT);
            } else {
                reminder.setStatus(ReminderStatus.FAILED);
            }

            reminderRepository.save(reminder);
        }
    }

    private ReminderNotification convertToNotification(Reminder reminder) {
        return new ReminderNotification(
                reminder.getId(),
                reminder.getUser().getId(),
                reminder.getTitle(),
                reminder.getDeadline()
        );
    }
}
