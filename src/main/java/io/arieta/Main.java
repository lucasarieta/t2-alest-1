package io.arieta;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Pokedex pokedex = new Pokedex();

        String caminhoCSV = "pokemons.csv";
        List<Pokemon> pokemons = PokemonUtils.lerPokemonsDoCSV(caminhoCSV);
        for (Pokemon pokemon : pokemons) {
            pokedex.add(pokemon);
        }

        pokedex.generateDOT();
    }
}
