package com.idlerpg.service.shop;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.shop.ShopEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopServiceTest {
    @Test
    void buyDeductsGoldAndAddsItem() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        testContext.itemRegistry().register(new ItemDefinition("potion", "Potion", ItemType.CONSUMABLE, 5));
        ShopEntry entry = new ShopEntry("buy_potion", "potion", 20, "sunlit_meadow");
        testContext.shopRegistry().register(entry);
        testContext.context().getPlayer().addGold(25);

        boolean bought = testContext.shopService().buy(testContext.context(), entry);

        assertTrue(bought);
        assertEquals(5, testContext.context().getPlayer().getGold());
        assertEquals(1, testContext.context().getPlayer().getInventory().getQuantity("potion"));
    }
}
