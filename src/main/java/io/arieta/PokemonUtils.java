package io.arieta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/***
 * Classe utilitaria para carregar os pokemons de um csv.
 * O csv deve estar no seguinte formato:
 * id,nome,tipos,nivel,pontosCombate
 *  1,Bulbasaur,Grama;Veneno,5,318
 *  4,Charmander,Fogo,5,309
 *  7,Squirtle,Água,5,314
 * 
 * Caso o pokemon tenha mais de um tipo, seus tipos devem ser armazenados em uma lista cujo separador é ; (ponto e vírgula)
 */
public class PokemonUtils {

    public static List<Pokemon> lerPokemonsDoCSV(String caminhoCSV) {
        List<Pokemon> pokemons = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {

            br.readLine();
            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                List<String> tipos = Arrays.asList(data[2].split(";"));

                Pokemon pokemon = new Pokemon(
                        Integer.parseInt(data[0]), // id
                        data[1],                   // nome
                        tipos,                     // tipos
                        Integer.parseInt(data[3]), // nivel
                        Integer.parseInt(data[4])  // pontosCombate
                );

                pokemons.add(pokemon);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pokemons;
    }
}
