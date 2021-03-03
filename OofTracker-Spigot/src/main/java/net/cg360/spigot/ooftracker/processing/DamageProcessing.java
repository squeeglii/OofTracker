package net.cg360.spigot.ooftracker.processing;

import net.cg360.nsapi.commons.Check;
import net.cg360.spigot.ooftracker.ConfigKeys;
import net.cg360.spigot.ooftracker.OofTracker;
import net.cg360.spigot.ooftracker.Util;
import net.cg360.spigot.ooftracker.cause.DamageTrace;
import net.cg360.spigot.ooftracker.cause.TraceKeys;
import net.cg360.spigot.ooftracker.list.DamageList;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DamageProcessing implements Listener {

    protected List<UUID> ignoredVanillaEvents; // Used to block events caused by custom damage.
    protected List<DamageProcessor> damageProcessors;


    public DamageProcessing() {
        this.ignoredVanillaEvents = new ArrayList<>();
        this.damageProcessors = new ArrayList<>();
    }



    /**
     * Applies custom damage to an entity, thus skipping the
     * DamageProcessing system and appending the DamageTrace
     * on directly
     * @param trace a damage trace to be applied to the DamageList stack
     * @return true if the damage has been applied.
     */
    public boolean applyCustomDamage(DamageTrace trace) {
        Check.nullParam(trace, "Damage Trace");

        if(trace.getVictim() instanceof Player || Util.check(ConfigKeys.LIST_NON_PLAYER_ENABLED, true)) {

            if (trace.getVictim() instanceof Damageable) {
                Damageable entity = (Damageable) trace.getVictim();
                Entity attacker = trace.getData().get(TraceKeys.ATTACKER_ENTITY);

                // The following Entity#damage() calls are only to support
                // Spigot damage events + update player health.

                // Add the entity to the list of ignored events
                // Then damage without the event triggering :)
                ignoredVanillaEvents.add(entity.getUniqueId());

                if (attacker == null) {
                    entity.damage(trace.getFinalDamageDealt());

                } else { // Had an attacker, trigger damage with attacker.
                    entity.damage(trace.getFinalDamageDealt(), attacker);
                }

                pushTrace(entity, trace);
                return true;
            }
        }

        return false;
    }


    public void addDamageProcessor(DamageProcessor processor) {
        Check.nullParam(processor, "Damage Processor");

        int priority = processor.getPriority();
        for(int i = 0; i < damageProcessors.size(); i++) {
            DamageProcessor originalProcessor = damageProcessors.get(i);

            if(originalProcessor.getPriority() < priority) {
                damageProcessors.add(i, processor);
                return;
            }
        }
        damageProcessors.add(processor); // Add if not already added.
    }



    // Should be last in the chain, ignoring if the event has been cancelled.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        // If it's a player or if non-player lists are enabled, do.
        if(event.getEntity() instanceof Player || Util.check(ConfigKeys.LIST_NON_PLAYER_ENABLED, true)) {

            if (!ignoredVanillaEvents.remove(event.getEntity().getUniqueId())) {
                // ^ If an item gets removed, it must've existed.
                // Thus, only run the following if nothing was removed.

                for (DamageProcessor processor : damageProcessors) {
                    Optional<DamageTrace> trace = processor.getDamageTrace(event);

                    if (trace.isPresent()) {
                        pushTrace(event.getEntity(), trace.get());
                        return;
                    }
                }

                // Panic! The default DamageTrace generator should've been last.
                throw new IllegalStateException("Default DamageProcessor didn't kick in. Why's it broke? :<");
            }
        }
    }

    // Like damage, it should be within the end of the chain.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        // Doesn't seem to have a named animal death event.
        // If there's a death of a pet, there'll be no custom death message as that doesn't
        // seem to have a setter.

        if((event instanceof PlayerDeathEvent) && (!Util.check(ConfigKeys.DEATH_MESSAGE_OVERRIDE, true))) {
            ((PlayerDeathEvent) event).setDeathMessage(""); // Handle deaths in damage event if death messages are overriden.
        }
    }



    private static void pushTrace(Entity entity, DamageTrace t) {
        DamageList ls = OofTracker.getDamageListManager().getDamageList(entity);
        ls.push(t);
    }
}
