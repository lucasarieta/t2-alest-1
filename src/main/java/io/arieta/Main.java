package io.arieta;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Pokedex pokedex = new Pokedex();

        String caminhoCSV = "pokemons.csv";
        List<Pokemon> pokemons = PokemonUtils.lerPokemonsDoCSV(caminhoCSV);
        for (Pokemon pokemon : pokemons) {
            pokedex.add(pokemon);
        }

        System.out.println("=========================== GERAÇÃO DA ÁRVORE DO CSV: =========================== ");
        //gerador da árvore:
        pokedex.generateDOT();


        System.out.println("=========================== FUNCIONALIDADE 1 (ADICIONAR OU REMOVER NA POKÉDEX)): =========================== ");
        System.out.println("\n");

        //ADICIONANDO POKEMON
        ArrayList<String> tipo = new ArrayList<>();
        tipo.add("Water");
        Pokemon pokemon = new Pokemon(134,"Vaporean", tipo, 100, 1400);
        pokedex.add(pokemon);
        //REMOVENDO POKEMON
        pokedex.remove(150);
        System.out.println("\n");


        System.out.println("=========================== FUNCIONALIDADE 2 (BUSCAR POR NÚMERO NA POKÉDEX)): =========================== ");
        System.out.println("\n");
        String poke = pokedex.searchByPokedex(150);
        System.out.println(poke);
        System.out.println("\n");


        System.out.println("=========================== FUNCIONALIDADE 3 (LISTAGEM POR PONTOS DE COMBATE)): =========================== ");
        System.out.println("\n");
        pokedex.listByCP();
        System.out.println("\n");


        System.out.println("=========================== FUNCIONALIDADE 4 (BUSCAR POR TIPO E NÍVEL): =========================== ");
        System.out.println("\n");
        pokedex.listByTypeAndLevel("Normal", 5);
        System.out.println("\n");


        System.out.println("=========================== GERAÇÃO DA ÁRVORE ÁPOS INSERÇÕES DA FUNC. 1: =========================== ");
        //gerador da árvore:
        pokedex.generateDOT();
    }
}