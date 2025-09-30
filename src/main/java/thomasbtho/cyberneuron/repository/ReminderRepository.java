package thomasbtho.cyberneuron.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thomasbtho.cyberneuron.entity.Reminder;
import thomasbtho.cyberneuron.entity.User;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser(User user);
}
