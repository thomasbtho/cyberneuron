package thomasbtho.cyberneuron.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import thomasbtho.cyberneuron.entity.Reminder;
import thomasbtho.cyberneuron.entity.ReminderStatus;
import thomasbtho.cyberneuron.entity.User;

import java.time.Instant;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser(User user);
    List<Reminder> findByStatusAndDeadlineBefore(ReminderStatus status, Instant before, Pageable pageable);
}
