package thomasbtho.cyberneuron.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thomasbtho.cyberneuron.dto.ReminderPatchRequest;
import thomasbtho.cyberneuron.dto.ReminderRequest;
import thomasbtho.cyberneuron.dto.ReminderResponse;
import thomasbtho.cyberneuron.entity.Reminder;
import thomasbtho.cyberneuron.entity.ReminderStatus;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.repository.ReminderRepository;
import thomasbtho.cyberneuron.service.ReminderService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public ReminderResponse createReminder(ReminderRequest reminderRequest, User user) {
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle(reminderRequest.title());
        reminder.setDeadline(reminderRequest.deadline());
        reminder.setStatus(ReminderStatus.SCHEDULED);

        Reminder savedReminder = reminderRepository.save(reminder);
        return convertToResponse(savedReminder);
    }

    @Override
    public List<ReminderResponse> getAllUserReminders(User user) {
        return reminderRepository.findByUser(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReminderResponse updateReminder(Long reminderId, ReminderPatchRequest patchRequest) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));


        if (patchRequest.getTitle() != null) {
            reminder.setTitle(patchRequest.getTitle());
        }

        if (patchRequest.getDeadline() != null) {
            reminder.setDeadline(patchRequest.getDeadline());
        }

        if (patchRequest.getStatus() != null) {
            reminder.setStatus(patchRequest.getStatus());
        }

        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToResponse(updatedReminder);
    }

    @Override
    public void deleteReminder(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));

        reminderRepository.delete(reminder);
    }

    private ReminderResponse convertToResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getUser().getId(),
                reminder.getTitle(),
                reminder.getDeadline(),
                reminder.getStatus()
        );
    }
}
