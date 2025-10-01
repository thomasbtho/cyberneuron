package thomasbtho.cyberneuron.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import thomasbtho.cyberneuron.entity.Reminder;
import thomasbtho.cyberneuron.entity.ReminderStatus;
import thomasbtho.cyberneuron.repository.ReminderRepository;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {
    private final ReminderRepository reminderRepository;
    private final int batch = 20;

    @Scheduled(fixedDelayString = "${reminder.scheduler.delay}")
    public void run() {
        List<Reminder> reminders = reminderRepository.findByStatusAndDeadlineBefore(ReminderStatus.SCHEDULED, Instant.now().plusSeconds(3600), Pageable.ofSize(batch));
    }
}
