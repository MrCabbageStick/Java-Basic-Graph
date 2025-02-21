package dev.mrcabbagestick.graph;


import dev.mrcabbagestick.graph.graph.Graph;
import dev.mrcabbagestick.graph.graph.GraphNode;
import dev.mrcabbagestick.graph.graph.LinkType;

import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) throws Exception{
        formattedCodeScope(() -> {
            GraphNode<String> nodeA = new GraphNode<>("A");
            Graph<String> graph = new Graph<>(nodeA);

            // A ->
            graph.printAdjacencies();
            return null;
        });

        formattedCodeScope(() -> {
            GraphNode<String> nodeA = new GraphNode<>("A");
            Graph<String> graph = new Graph<>(nodeA);

            // false
            System.out.println(graph.addNode(nodeA, nodeA, LinkType.TYPE_1));

            // A ->
            graph.printAdjacencies();
            return null;
        });

        formattedCodeScope(() -> {
            GraphNode<String> nodeA = new GraphNode<>("A");
            Graph<String> graph = new Graph<>(nodeA);

            GraphNode<String> nodeB = new GraphNode<>("B");

            // true
            System.out.println(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

            // A -> (B, TYPE_1),
            // B -> (A, TYPE_1),
            graph.printAdjacencies();
            return null;
        });

        formattedCodeScope(() -> {
            GraphNode<String> nodeA = new GraphNode<>("A");
            Graph<String> graph = new Graph<>(nodeA);

            GraphNode<String> nodeB = new GraphNode<>("B");

            // true
            System.out.println(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

            // A -> (B, TYPE_1),
            // B -> (A, TYPE_1),
            graph.printAdjacencies();
            return null;
        });

        formattedCodeScope(() -> {
            GraphNode<String> nodeA = new GraphNode<>("A");
            Graph<String> graph = new Graph<>(nodeA);

            GraphNode<String> nodeB = new GraphNode<>("B");
            GraphNode<String> nodeC = new GraphNode<>("C");

            // true
            System.out.println(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
            // true
            System.out.println(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
            // true
            System.out.println(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

            // A -> (B, TYPE_1), (C, TYPE_3),
            // B -> (C, TYPE_2), (A, TYPE_1),
            // C -> (B, TYPE_2), (A, TYPE_3),
            graph.printAdjacencies();
            return null;
        });
    }

    static void formattedCodeScope(Callable<Void> callable) throws Exception {
        System.out.print("------------\n");
        callable.call();
    }
}