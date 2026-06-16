package com.idlerpg.domain.action;

import com.idlerpg.domain.context.GameContext;

public interface ActionStrategy {
    ActionResult execute(GameContext context);
}
