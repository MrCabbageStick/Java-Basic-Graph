package dev.mrcabbagestick.graph.graph;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.HashSet.newHashSet;

public class Graph<NodeType, LinkType> {
    private final Map<GraphNode<NodeType>, HashSet<GraphLink<NodeType, LinkType>>> adjacencyList = new HashMap<>();

    public Graph(GraphNode<NodeType> firstNode){
        adjacencyList.put(firstNode, newHashSet(0));
    }

    private Graph(){}

//    private void addNode_unsafe(GraphNode<T> node){
//        adjacencyList.put(node, newHashSet(0));
//    }

    public boolean addNode(GraphNode<NodeType> node, GraphNode<NodeType> connectedTo, LinkType linkType){
        var allNodes = adjacencyList.keySet();

        if(allNodes.contains(node) || !allNodes.contains(connectedTo)){
            return false;
        }

        HashSet<GraphLink<NodeType, LinkType>> connectionSet = HashSet.newHashSet(0);
        connectionSet.add(new GraphLink<>(connectedTo, linkType));
        adjacencyList.put(node, connectionSet);

        adjacencyList.get(connectedTo).add(new GraphLink<>(node, linkType));

        return true;
    }

    public boolean addLink(GraphNode<NodeType> node1, GraphNode<NodeType> node2, LinkType linkType){
        var allNodes = adjacencyList.keySet();

        if(!allNodes.contains(node1) || !allNodes.contains(node2)){
            return false;
        }

        adjacencyList.get(node1).add(new GraphLink<>(node2, linkType));
        adjacencyList.get(node2).add(new GraphLink<>(node1, linkType));

        return true;
    }

    public Map<GraphNode<NodeType>, Set<GraphLink<NodeType, LinkType>>> getAdjacencies(){
        return Map.copyOf(adjacencyList);
    }

    private boolean removeConnection_unsafe(GraphNode<NodeType> node1, GraphNode<NodeType> node2){
        var allNodes = adjacencyList.keySet();

        if(!allNodes.contains(node1) || !allNodes.contains(node2))
            return false;

        var node1Connections = adjacencyList.get(node1);
        var node2Connections = adjacencyList.get(node2);

        adjacencyList.put(
                node1,
                node1Connections.stream().filter(link -> link.node() != node2).collect(Collectors.toCollection(HashSet::new))
        );

        adjacencyList.put(
                node2,
                node2Connections.stream().filter(link -> link.node() != node1).collect(Collectors.toCollection(HashSet::new))
        );

        return true;
    }

    public Optional<Graph<NodeType, LinkType>> removeConnectionAndSplit(GraphNode<NodeType> node1, GraphNode<NodeType> node2){
        boolean nodesDisconnected = removeConnection_unsafe(node1, node2);

        // Nodes not in network
        if(!nodesDisconnected)
            return Optional.empty();

        var connectedToNode1 = getConnectedNodes(node1);

        // Nodes still connected after link removal
        if(connectedToNode1.contains(node2))
            return Optional.empty();

        Graph<NodeType, LinkType> newGraph = new Graph<>();

        HashSet<GraphNode<NodeType>> connectedToNode2 = new HashSet<>(adjacencyList.size() - connectedToNode1.size());

        // Populate new graph
        adjacencyList.forEach((key, value) -> {
            if (!connectedToNode1.contains(key)) {
                connectedToNode2.add(key);
                newGraph.adjacencyList.put(key, value);
            }
        });

        // Remove unneeded nodes from this graph
        connectedToNode2.forEach(adjacencyList::remove);

        return Optional.of(newGraph);
    }

    private boolean removeNode_unsafe(GraphNode<NodeType> node){
        if(!adjacencyList.containsKey(node))
            return false;

        var connectedTo = adjacencyList.get(node);
        adjacencyList.remove(node);

        connectedTo.forEach(link -> {
//            adjacencyList.get(link.node()).remove(new GraphLink<>(link.node(), link.type()));
            adjacencyList.put(
                    link.node(),
                    adjacencyList.get(link.node()).stream()
                            .filter(_link -> _link.node() != node)
                            .collect(Collectors.toCollection(HashSet::new))
            );
        });

        return true;
    }

    public Optional<Set<Graph<NodeType, LinkType>>> removeNodeAndSplit(GraphNode<NodeType> node){
        if(!adjacencyList.containsKey(node))
            return Optional.empty();

        var danglingNodes = adjacencyList.get(node).stream().map(GraphLink::node).collect(Collectors.toSet());
        removeNode_unsafe(node);

        if(danglingNodes.isEmpty())
            return Optional.of(Set.of());

        var thisGraphSeed = danglingNodes.stream().findFirst().get();
        var currentGraphNodes = getConnectedNodes(thisGraphSeed);

        danglingNodes.removeAll(currentGraphNodes);

        HashSet<Graph<NodeType, LinkType>> newGraphs = new HashSet<>();

        // Combine dangling nodes to graphs
        while(!danglingNodes.isEmpty()){
            var newGraphSeed = danglingNodes.stream().findFirst().get();
            danglingNodes.remove(newGraphSeed);

            var newGraphNodes = getConnectedNodes(newGraphSeed);

            Graph<NodeType, LinkType> newGraph = new Graph<>();
            adjacencyList.forEach((key, value) -> {
                if(newGraphNodes.contains(key))
                    newGraph.adjacencyList.put(key, value);
            });

            newGraphs.add(newGraph);

            danglingNodes.removeAll(newGraphNodes);

            newGraphNodes.forEach(adjacencyList::remove);
        }

        return Optional.of(newGraphs);
    }

    public Set<GraphNode<NodeType>> getConnectedNodes(GraphNode<NodeType> node){
        if(!adjacencyList.containsKey(node))
            return Set.of();

        Map<GraphNode<NodeType>, Set<GraphNode<NodeType>>> adjacentNodes = new HashMap<>(adjacencyList.size());

        adjacencyList.forEach((key, value) -> {
            var nodeSet = value.stream().map(GraphLink::node).collect(Collectors.toSet());
            adjacentNodes.put(key, nodeSet);
        });

        HashSet<GraphNode<NodeType>> visitedNodes = new HashSet<>();
        Stack<GraphNode<NodeType>> nodesToVisit = new Stack<>();

        nodesToVisit.push(node);

        while(!nodesToVisit.empty()){
            var currentNode = nodesToVisit.pop();
            visitedNodes.add(currentNode);

            adjacentNodes.get(currentNode).forEach(_node -> {
                if(!visitedNodes.contains(_node))
                    nodesToVisit.push(_node);
            });
        }

        return visitedNodes;
    }

    public boolean canExist(){
        return !adjacencyList.isEmpty();
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






