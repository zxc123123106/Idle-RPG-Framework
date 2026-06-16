package com.idlerpg.service.offline;

public record OfflineProgressResult(
        long secondsApplied,
        int cyclesCompleted,
        String itemId,
        int quantity,
        int experience
) {
    public boolean hasRewards() {
        return cyclesCompleted > 0;
    }
}
