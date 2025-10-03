package thomasbtho.cyberneuron.dto;

public record NotificationResult(
        Long reminderId,
        boolean success,
        String errorMessage) {
    public static NotificationResult success(Long reminderId) {
        return new NotificationResult(reminderId, true, null);
    }

    public static NotificationResult failure(Long reminderId, String message) {
        return new NotificationResult(reminderId, false, message);
    }
}
