package de.emn4tor.modules.lobby.crates.reward;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class MoneyReward extends BaseReward{
    private final int amount;
}
