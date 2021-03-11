package net.cg360.spigot.ooftracker.indicator;

import net.cg360.spigot.ooftracker.ConfigKeys;
import net.cg360.spigot.ooftracker.OofTracker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class HealthbarManager implements Listener {

    protected HashMap<Integer, LivingEntityHealthbar> healthbars; // OwnerID: Healthbar
    protected HashMap<Integer, Long> lastDamageMillis;

    public HealthbarManager () {
        this.healthbars = new HashMap<>();
        this.lastDamageMillis = new HashMap<>();
    }


    //TODO: Use the custom damage event triggered by DamageProcessing#onEntityDamage() when added.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if(event.getEntity() instanceof LivingEntity) {

            // Only create/update health bars if enabled (distance > 0)
            if(OofTracker.getConfiguration().getOrElse(ConfigKeys.HEALTH_BAR_VIEW_DISTANCE, 20d) > 0) {
                long currentMilli = System.currentTimeMillis(); // Get current time now so it's consistent if needed.
                int entityID = event.getEntity().getEntityId();
                int viewTicks = OofTracker.getConfiguration().get(ConfigKeys.HEALTH_BAR_VIEW_TICKS);

                this.lastDamageMillis.put(entityID, currentMilli);

                if (!healthbars.containsKey(entityID)) {
                    healthbars.put(entityID, new LivingEntityHealthbar((LivingEntity) event.getEntity()) );
                }

                LivingEntityHealthbar health = this.healthbars.get(entityID); // If this fails, hOW??
                health.visible = true; // Set visible and update.
                health.updateVisibility();

                OofTracker.get().getServer().getScheduler().scheduleSyncDelayedTask(OofTracker.get(), () -> {

                    if(checkTicks(entityID)) {
                        LivingEntityHealthbar hb = this.healthbars.get(entityID); // Shouldn't fail unless someone has messed with it >:(
                        hb.visible = false; // Set invisible and update.
                        hb.updateVisibility();
                    }

                }, viewTicks + 1); // Ensure the delta will be past the max.
            }
        }
    }

    protected boolean checkTicks(int entityID) {
        long currentMilli = System.currentTimeMillis();  // Get current time now so it's consistent if needed.

        long lastMilli = lastDamageMillis.get(entityID);
        long delta = currentMilli - lastMilli; // Difference between then and now.
        long maxConfigDelta = OofTracker.getConfiguration().get(ConfigKeys.HEALTH_BAR_VIEW_TICKS) * 50; // Tick is 0.050 seconds = 50 millis

        // TRUE:    Delta is out of the bounds, remove the healthbar.
        // FALSE:   Delta is in meaning a more recent damage occurred. Keep healthbar.
        return delta < maxConfigDelta;
    }

}
