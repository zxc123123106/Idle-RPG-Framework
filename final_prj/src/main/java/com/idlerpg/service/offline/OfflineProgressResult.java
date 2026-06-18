package com.idlerpg.service.offline;

public record OfflineProgressResult(
        long secondsApplied,
        int cyclesCompleted,
        String itemId,
        int quantity,
        int experience,
        boolean stoppedForMissingMaterials
) {
    public boolean hasRewards() {
        return cyclesCompleted > 0;
    }
}
