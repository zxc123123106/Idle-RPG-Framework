package com.idlerpg.core.engine;

import com.idlerpg.domain.context.GameContext;

public interface Tickable {
    void tick(GameContext context);
}
