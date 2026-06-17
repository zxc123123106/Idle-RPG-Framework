package com.idlerpg.domain.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {
    @Test
    void healRestoresHpWithoutExceedingMaximum() {
        Player player = new Player();
        player.takeDamage(40);

        player.heal(12);

        assertEquals(72, player.getCurrentHp());

        player.heal(100);

        assertEquals(player.getMaxHp(), player.getCurrentHp());
    }
}
