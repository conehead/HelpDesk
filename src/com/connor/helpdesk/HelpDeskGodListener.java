package com.connor.helpdesk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class HelpDeskGodListener implements Listener {
    private HelpDesk helpDeskInstance;

    public HelpDeskGodListener(HelpDesk helpDeskInstance) {
        this.helpDeskInstance = helpDeskInstance;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (helpDeskInstance.doesHaveTicketAssigned(player)) {
                if (player.getFireTicks() > 0)
                    player.setFireTicks(0);
                event.setCancelled(true);
            }
        }
    }
}
