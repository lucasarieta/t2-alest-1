package io.arieta;

import java.util.*;

public class Pokedex {
    private Node root = null;
    private final ArrayList<Pokemon> pokemonsSortedByCP = new ArrayList<>();
    private final Map<String, TreeMap<Integer, ArrayList<Pokemon>>> typeAndLevelIndex = new HashMap<>();
    
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
        Node node = searchNode(id);

        Pokemon pokemon = node.pokemon;
        pokemonsSortedByCP.remove(pokemon);
        removeFromType(pokemon);
            
        Node current = searchNode(id);
        if (current == null) return false;

        /*
            Node é uma folha (leaf)
        */
        if (current.left == null && current.right == null) {
            if (current.parent == null) {
                root = null;
            } else {
                if (current.parent.left == current) {
                    current.parent.left = null;
                } else {
                    current.parent.right = null;
                }
            }
            /* O node tem um filho */
        } else if (current.left == null || current.right == null) {
            Node child;
            child = Objects.requireNonNullElseGet(current.left, () -> current.right);

            if (current.parent == null) {
                root = child;
            } else if (current.parent.left == current) {
                current.parent.left = child;
            } else {
                current.parent.right = child;
            }

            child.parent = current.parent;
            /* node tem dois filhos */
        } else {
            Node prev = current.left;
            while (prev.right != null) {
                prev = prev.right;
            }

            current.pokemon = prev.pokemon;

            if (current.left == prev) {
                if (prev.left != null) {
                    prev.left.parent = current;
                    current.left = prev.left;
                } else {
                    prev.parent = null;
                    current.left = null;
                }
            } else {
                if (prev.left != null) {
                    prev.left.parent = prev.parent;
                    prev.parent.left = prev.left;
                } else {
                    prev.parent.right = null;
                }
            }
        }

        Node father = current.parent;
        while (father != null) {
            father = balance(father);
            father = father.parent;
        }

        return true;
    }

    /*
        Pesquisa um node na árvore com base no id do pokemon
    */
    String searchByPokedex(Integer id){
        return searchNode(id).pokemon.toString();
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
        Lista todos os pokemons de um determinado tipo
    */
    void listByTypeAndLevel(String type, Integer levelMin){
        if (!typeAndLevelIndex.containsKey(type)) {
            System.out.println("Tipo não encontrado");
            return;
        }

        TreeMap<Integer, ArrayList<Pokemon>> levelMap = typeAndLevelIndex.get(type);
        for (Map.Entry<Integer, ArrayList<Pokemon>> entry : levelMap.tailMap(levelMin).entrySet()) {
            for (Pokemon pokemon : entry.getValue()) {
                System.out.println(pokemon.toString());
            }
        }
    }

    /*
        Método auxiliar para adicionar um node na árvore
    */
    Node addAux(Node current, Pokemon pokemon, Node parent) {
        if (current == null) {
            Node newNode = new Node(pokemon);
            newNode.parent = parent;

            if (root == null) {
                root = newNode;
            }

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

        int balance = getBalance(current);

        /*
            Se o balanceamento do node for maior que 1 e o balanceamento do filho à esquerda for 1
            a rotação simples à direita é realizada
        */
        if (balance > 1 && current.left != null && getBalance(current.left) == 1) {
            return rotateRight(current);
        }

        /*
            Se o balanceamento do node for menor que -1 e o balanceamento do filho à direita for -1
            a rotação simples à esquerda é realizada
        */
        if (balance < -1 && current.right != null && getBalance(current.right) == -1) {
            return rotateLeft(current);
        }

        /*
            Se o balanceamento do node for maior que 1 e o balanceamento do filho à esquerda for -1
            a rotação dupla esquerda-direita é realizada
        */
        if (balance > 1 && current.left != null && getBalance(current.left) == -1) {
            current.left = rotateLeft(current.left);
            return rotateRight(current);
        }

        /*
            Se o balanceamento do node for menor que -1 e o balanceamento do filho à direita for 1
            a rotação dupla direita-esquerda é realizada
        */
        if (balance < -1 && current.right != null && getBalance(current.right) == 1) {
            current.right = rotateRight(current.right);
            return rotateLeft(current);
        }

        return current;
    }

    /*
        Adiciona um pokemon na lista de pokemons ordenados por CP
    */
    void addByCP(Pokemon pokemon){
        if (this.pokemonsSortedByCP.isEmpty()) {
            this.pokemonsSortedByCP.add(pokemon);
            return;
        }

        for(Pokemon p : this.pokemonsSortedByCP){
            if(p.getCombatPoints() < pokemon.getCombatPoints()){
                this.pokemonsSortedByCP.add(this.pokemonsSortedByCP.indexOf(p), pokemon);
                return;
            }
        }

        this.pokemonsSortedByCP.addFirst(pokemon);
    }

    /*
        Adiciona um pokemon na lista de pokemons ordenados por tipo
    */
    void addByType(Pokemon pokemon){
        for (String type : pokemon.getTypes()) {
            typeAndLevelIndex.putIfAbsent(type, new TreeMap<>());
            TreeMap<Integer, ArrayList<Pokemon>> levelMap = typeAndLevelIndex.get(type);

            levelMap.putIfAbsent(pokemon.getLevel(), new ArrayList<>());
            levelMap.get(pokemon.getLevel()).add(pokemon);
        }
    }

    /*
        Remove um Pokémon da estrutura de índice por tipo e nível
    */
    void removeFromType(Pokemon pokemon) {
        for (String type : pokemon.getTypes()) {
            TreeMap<Integer, ArrayList<Pokemon>> levelMap = typeAndLevelIndex.get(type);
            if (levelMap != null) {
                ArrayList<Pokemon> levelList = levelMap.get(pokemon.getLevel());
                if (levelList != null) {
                    levelList.remove(pokemon);
                    if (levelList.isEmpty()) {
                        levelMap.remove(pokemon.getLevel());
                    }
                }

                if (levelMap.isEmpty()) {
                    typeAndLevelIndex.remove(type);
                }
            }
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
        if (current == null) throw new IllegalArgumentException("Falha ao encontrar pokemon");
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
        } else {
            root = two;
        }

        three.parent = two;
        three.height = Math.max(getHeight(three.left), getHeight(three.right)) + 1;
        two.height = Math.max(getHeight(two.left), getHeight(two.right)) + 1;

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
