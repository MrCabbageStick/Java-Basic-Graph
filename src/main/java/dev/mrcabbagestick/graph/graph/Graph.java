package dev.mrcabbagestick.graph.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.HashSet.newHashSet;

public class Graph<T> {
    private Map<GraphNode<T>, HashSet<GraphLink<T>>> adjacencyList = new HashMap<>();

    public Graph(GraphNode<T> firstNode){
        adjacencyList.put(firstNode, newHashSet(0));
    }

    private void addNode_unsafe(GraphNode<T> node){
        adjacencyList.put(node, newHashSet(0));
    }

    public boolean addNode(GraphNode<T> node, GraphNode<T> connectedTo, LinkType linkType){
        var allNodes = adjacencyList.keySet();

        if(allNodes.contains(node) || !allNodes.contains(connectedTo)){
            return false;
        }

        HashSet<GraphLink<T>> connectionSet = HashSet.newHashSet(0);
        connectionSet.add(new GraphLink<>(connectedTo, linkType));
        adjacencyList.put(node, connectionSet);

        adjacencyList.get(connectedTo).add(new GraphLink<>(node, linkType));

        return true;
    }

    public boolean addLink(GraphNode<T> node1, GraphNode<T> node2, LinkType linkType){
        var allNodes = adjacencyList.keySet();

        if(!allNodes.contains(node1) || !allNodes.contains(node2)){
            return false;
        }

        adjacencyList.get(node1).add(new GraphLink<>(node2, linkType));
        adjacencyList.get(node2).add(new GraphLink<>(node1, linkType));

        return true;
    }

    public Map<GraphNode<T>, Set<GraphLink<T>>> getAdjacencies(){
        return Map.copyOf(adjacencyList);
    }

    public void printAdjacencies(){
        for(var entry : adjacencyList.entrySet()){
            System.out.print(entry.getKey().data().toString() + " -> ");

            for(var connectedTo : entry.getValue()){
                System.out.print(
                        "(" + connectedTo.node().data().toString() + ", "
                        + connectedTo.type().toString()
                        + "), ");
            }
            System.out.print('\n');
        }
    }
}






