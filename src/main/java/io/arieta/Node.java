package io.arieta;

import io.arieta.Pokemon;

class Node {
    public Node parent;
    public Node left;
    public Node right;
    public Pokemon pokemon;
    public Integer height;

    public Node(Pokemon pokemon){
        this.parent = null;
        this.left = null;
        this.right = null;
        this.pokemon = pokemon;
        this.height = 1;
    }
}