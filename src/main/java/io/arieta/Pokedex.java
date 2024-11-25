package io.arieta;
import java.util.*;

public class Pokedex {
    private Node root = null;
    private final ArrayList<Pokemon> pokemonsSortedByCP = new ArrayList<>();
    private final Map<String, ArrayList<Pokemon>> listByType = new HashMap<>();
    private final Map<String, TreeMap<Integer, ArrayList<Pokemon>>> typeAndMinLevel = new HashMap<>();

    /*
        Adiciona um novo node (pokemon) na árvore
    */
    void add(Pokemon pokemon) {
        root = addAux(root, pokemon, null);
        addByCP(pokemon);
        addByType(pokemon);
    }

    /*
        Remove um novo node (pokemon) da árvore
    */
    boolean remove(Integer id) {
        root = removeAux(root, id);
        return root != null;
    }

    private Node removeAux(Node current, Integer id) {
        if (current == null) return null;

        if (id < current.pokemon.getId()) {
            current.left = removeAux(current.left, id);
        } else if (id > current.pokemon.getId()) {
            current.right = removeAux(current.right, id);
        } else {
            /*
                Caso 1: Nó sem filhos
             */
            if (current.left == null && current.right == null) {
                return null;
            }

            /*
                Nó com um filho
             */

            if (current.left == null) {
                Node temp = current.right;
                temp.parent = current.parent;
                return temp;
            }

            if (current.right == null) {
                Node temp = current.left;
                temp.parent = current.parent;
                return temp;
            }
            /*
                Caso 3: Nó com dois
             */
            Node successor = getMax(current.left);
            current.pokemon = successor.pokemon;
            current.left = removeAux(current.left, successor.pokemon.getId());
        }

        current.height = 1 + Math.max(getHeight(current.left), getHeight(current.right));
        return balance(current);
    }

    private Node getMax(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    /*
        Pesquisa um node na árvore com base no id do pokemon
    */
    String searchByPokedex(Integer id) {
        Node node = searchNode(id);
        return node != null ? node.pokemon.toString() : "Pokémon não encontrado";
    }

    /*
        Lista todos os pokemons ordenados por CP
    */
    void listByCP(){
        for (int i = 0; i < pokemonsSortedByCP.size(); i++) {
            System.out.println("["+i+1+"]: "+pokemonsSortedByCP.get(i).toString());
        }
    }

    /*
        Lista todos os pokemons de um determinado tipo e nível mínimo
    */
    void listByTypeAndLevel(String type, Integer levelMin){
        if (!typeAndMinLevel.containsKey(type)) {
            System.out.println("Tipo não encontrado");
            return;
        }

        System.out.println("Listando Pokémon do tipo: " + type + " com nível mínimo de: " + levelMin);

        TreeMap<Integer, ArrayList<Pokemon>> levelMap = typeAndMinLevel.get(type);
        Map.Entry<Integer, ArrayList<Pokemon>> entry = levelMap.ceilingEntry(levelMin);

        if (entry != null) {
            levelMap.tailMap(entry.getKey()).forEach((keyLevel, pokemons) -> {
                for (Pokemon p : pokemons) {
                    System.out.println(p.toString());
                }
            });
        } else {
            System.out.println("Nenhum Pokémon encontrado com o nível mínimo especificado.");
        }
    }

    /*
        Método auxiliar para adicionar um node na árvore
    */
    Node addAux(Node current, Pokemon pokemon, Node parent) {
        if (current == null) {
            Node newNode = new Node(pokemon);
            newNode.parent = parent;
            return newNode;
        }

        if (pokemon.getId() < current.pokemon.getId()) {
            current.left = addAux(current.left, pokemon, current);
        } else if (pokemon.getId() > current.pokemon.getId()) {
            current.right = addAux(current.right, pokemon, current);
        } else {
            return current;
        }

        current.height = 1 + Math.max(getHeight(current.left), getHeight(current.right));
        return balance(current);
    }

    /*
        Adiciona um pokemon na lista de pokemons ordenados por CP
    */
    void addByCP(Pokemon pokemon){
        if (this.pokemonsSortedByCP.isEmpty()) {
            this.pokemonsSortedByCP.add(pokemon);
            return;
        }

        for (int i = 0; i < this.pokemonsSortedByCP.size(); i++) {
            if (this.pokemonsSortedByCP.get(i).getCombatPoints() < pokemon.getCombatPoints()) {
                this.pokemonsSortedByCP.add(i, pokemon);
                return;
            }
        }

        this.pokemonsSortedByCP.add(pokemon);
    }

    /*
        Adiciona um pokemon na lista de pokemons ordenados por tipo e nível
    */
    void addByType(Pokemon pokemon){
        for (String type : pokemon.getTypes()) {
            typeAndMinLevel.putIfAbsent(type, new TreeMap<>());

            int levelMin = pokemon.getLevel();
            TreeMap<Integer, ArrayList<Pokemon>> levelMap = typeAndMinLevel.get(type);
            levelMap.putIfAbsent(levelMin, new ArrayList<>());
            levelMap.get(levelMin).add(pokemon);
        }
    }

    /*
        Pesquisa um node na árvore com base no id do pokemon
    */
    private Node searchNode(Integer id) {
        if (id == null) return null;

        Node current = root;
        try {
            return searchNodeAux(id, current);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return current;
    }

    /*
        Método auxiliar para pesquisar um node na árvore
    */
    private Node searchNodeAux(Integer id, Node current) {
        if (current == null) throw new IllegalArgumentException("Falha ao encontrar Pokémon");
        if (current.pokemon.getId() > id) return searchNodeAux(id, current.left);
        if (current.pokemon.getId() < id) return searchNodeAux(id, current.right);

        return current;
    }

    /*
        Retorna a altura de um node
    */
    private Integer getHeight(Node n) {
        return n == null ? 0 : n.height;
    }

    /*
        Retorna o balanceamento de um node
    */
    private int getBalance(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    /*
        Realiza o balanceamento da árvore
    */
    private Node balance(Node node) {
        int _balance = getBalance(node);

        /*
            Desbalanceado a esquerda
        */
        if (_balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        /*
            Desbalanceado a direita
        */
        if (_balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        return node;
    }

    /*
        Realiza rotação à direita
    */
    private Node rotateRight(Node three) {
        Node two = three.left;
        Node one = two.right;

        two.right = three;
        three.left = one;

        return getNode(three, two, one);
    }

    /*
        Realiza rotação à esquerda
    */
    private Node rotateLeft(Node three) {
        Node two = three.right;
        Node one = two.left;

        two.left = three;
        three.right = one;

        return getNode(three, two, one);
    }

    private Node getNode(Node three, Node two, Node one) {
        if (one != null) {
            one.parent = three;
        }

        two.parent = three.parent;

        if (three.parent != null) {
            if (three.parent.left == three) {
                three.parent.left = two;
            } else {
                three.parent.right = two;
            }
        }

        three.parent = two;
        return two;
    }

    /*
      Gera uma saida DOT, pode ser visualizado em
      http://www.webgraphviz.com/
    */
    private void generateDOTConnection(Node node) {
        if (node == null) return;

        generateDOTConnection(node.left);
        if (node.left != null) {
            System.out.println("\"node" + node.pokemon.getId() + "\":esq -> \"node" + node.left.pokemon.getId() + "\" " + "\n");
        }

        generateDOTConnection(node.right);
        if (node.right != null) {
            System.out.println("\"node" + node.pokemon.getId() + "\":dir -> \"node" + node.right.pokemon.getId() + "\" " + "\n");
        }
    }

    private void generateDOTNodes(Node node) {
        if (node == null) return;

        generateDOTNodes(node.left);
        System.out.println("node" + node.pokemon.getId() + "[label = \"<esq> | " + node.pokemon.getId() + " | <dir> \"]" + "\n");
        generateDOTNodes(node.right);
    }

    public void generateDOT() {
        System.out.println("digraph g { \nnode [shape = record,height=.1];\n" + "\n");
        generateDOTNodes(this.root);
        generateDOTConnection(this.root);
        System.out.println("}" + "\n");
    }
}
