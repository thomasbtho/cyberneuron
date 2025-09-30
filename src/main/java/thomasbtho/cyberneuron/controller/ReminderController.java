package thomasbtho.cyberneuron.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import thomasbtho.cyberneuron.dto.ReminderPatchRequest;
import thomasbtho.cyberneuron.dto.ReminderRequest;
import thomasbtho.cyberneuron.dto.ReminderResponse;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.service.ReminderService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminder")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ReminderResponse> createReminder(
            @Valid @RequestBody ReminderRequest reminderRequest,
            @PathVariable Long userId,
            @AuthenticationPrincipal User user
    ) {
        ReminderResponse response = reminderService.createReminder(reminderRequest, user);
        String uri = String.format("/api/users/%d/reminders/%d", user.getId(), response.id());
        return ResponseEntity
                .created(URI.create(uri))
                .body(response);
    }

    @GetMapping
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<ReminderResponse>> getUserReminders(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user
    ) {
        List<ReminderResponse> reminders = reminderService.getAllUserReminders(user);
        return ResponseEntity.ok(reminders);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ReminderResponse> updateReminderStatus(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody ReminderPatchRequest patchRequest
    ) {
        ReminderResponse response = reminderService.updateReminder(id, patchRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<Void> deleteReminder(
            @PathVariable Long userId,
            @PathVariable Long id
    ) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
