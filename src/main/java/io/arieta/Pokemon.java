package io.arieta;

import java.util.List;

class Pokemon {
    private final int id, level, combatPoints;
    private final String name;
    private final List<String> types;

    Pokemon(
            int id,
            String name,
            List<String> types,
            int level,
            int combatPoints
    ) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.level = level;
        this.combatPoints = combatPoints;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    List<String> getTypes() {
        return types;
    }

    int getLevel() {
        return level;
    }

    int getCombatPoints() {
        return combatPoints;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "numero=" + id +
                ", nome='" + name + '\'' +
                ", tipos=" + types +
                ", nivel=" + level +
                ", pontosCombate=" + combatPoints +
                '}';
    }
}
