package net.cg360.spigot.ooftracker.lists;

import net.cg360.nsapi.commons.Check;
import net.cg360.spigot.ooftracker.causes.DamageTrace;

import java.util.LinkedList;
import java.util.UUID;

public class DamageList {

    protected UUID owner;
    protected LinkedList<DamageTrace> stack;

    public DamageList(UUID owner) {
        Check.nullParam(owner, "Owner");
        this.owner = owner;
        this.stack = new LinkedList<>();
    }


    /** Adds a DamageTrace to the top of the DamageList stack. */
    public void push(DamageTrace trace) {
        if(!trace.getVictim().getUniqueId().equals(owner)) throw new IllegalArgumentException("DamageTrace being pushed must belong to the list's owner.");
        stack.add(0, trace);
    }

    /**
     * Removes the most recent element on the DamageList stack.
     * @return the most recent element.
     */
    public DamageTrace pop() {
        DamageTrace t = peek();
        if(!isEmpty()) stack.remove(0);
        return t;
    }

    /**
     * Checks the the most recent element of the DamageList
     * stack without removing it.
     * @return the most recent element
     */
    public DamageTrace peek() {
        return isEmpty() ? null : this.stack.get(0);
    }

    /** Clears the DamageList stack. */
    public void clear() {
        this.stack.clear();
    }



    /** @return true if the list is empty. */
    public boolean isEmpty() { return this.stack.size() == 0; }

    /** @return the owning entity id of this DamageList. */
    public UUID getOwner() { return owner; }
}
