package dev.mrcabbagestick.graph.graph;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    void graphCreation_OneNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(
            nodeA, Set.of()
        );

        assertEquals(correctStructure, graph.getAdjacencies());
    }

    @Test
    void graphNodeAddition_RepeatedNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        assertFalse(graph.addNode(nodeA, nodeA, LinkType.TYPE_1));
        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(
                nodeA, Set.of()
        );

        assertEquals(correctStructure, graph.getAdjacencies());
    }

    @Test
    void graphNodeAddition_NewNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");

       assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1))
        );

        assertEquals(correctStructure, graph.getAdjacencies());
    }

    @Test
    void graphLinkAddition_Triangle(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1), new GraphLink<>(nodeC, LinkType.TYPE_3)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1), new GraphLink<>(nodeC, LinkType.TYPE_2)),
                nodeC, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_3), new GraphLink<>(nodeB, LinkType.TYPE_2))
        );

        assertEquals(correctStructure, graph.getAdjacencies());
    }

    @Test
    void graph_ConnectedNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        var connectedNodes = graph.getConnectedNodes(nodeA);

        assertEquals(Set.of(nodeA, nodeB, nodeC), connectedNodes);
    }

    @Test
    void graphConnectionRemoval_NoSplit_Triangle(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        var secondGraph = graph.removeConnectionAndSplit(nodeA, nodeB);

        assertEquals(secondGraph, Optional.empty());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_3)),
                nodeB, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_2)),
                nodeC, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_3), new GraphLink<>(nodeB, LinkType.TYPE_2))
        );

        assertEquals(correctStructure, graph.getAdjacencies());
    }

    @Test
    void graphConnectionRemoval_Split_TwoNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

        var newGraph = graph.removeConnectionAndSplit(nodeA, nodeB);
        assertFalse(newGraph.isEmpty());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure1 = Map.of(nodeA, Set.of());
        assertEquals(correctStructure1, graph.getAdjacencies());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure2 = Map.of(nodeB, Set.of());
        assertEquals(correctStructure2, newGraph.get().getAdjacencies());
    }

    @Test
    void graphConnectionRemoval_Split_FourNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");
        GraphNode<String> nodeD = new GraphNode<>("D");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeD, nodeC, LinkType.TYPE_1));

        var newGraph = graph.removeConnectionAndSplit(nodeB, nodeC);
        assertFalse(newGraph.isEmpty());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure1 = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1))
        );
        assertEquals(correctStructure1, graph.getAdjacencies());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure2 = Map.of(
                nodeC, Set.of(new GraphLink<>(nodeD, LinkType.TYPE_1)),
                nodeD, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_1))
        );
        assertEquals(correctStructure2, newGraph.get().getAdjacencies());
    }

    @Test
    void graphNodeRemoval_NoSplit_SingleNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        var newGraphs = graph.removeNodeAndSplit(nodeA);
        assertFalse(newGraphs.isEmpty());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of();
        assertEquals(correctStructure, graph.getAdjacencies());

        assertFalse(graph.canExist());
    }

    @Test
    void graphNodeRemoval_NoSplit_TwoNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

        var newGraphs = graph.removeNodeAndSplit(nodeA);
        assertFalse(newGraphs.isEmpty());
        assertEquals(Set.of(), newGraphs.get());

        Map<GraphNode<String>, Set<GraphLink<String, LinkType>>> correctStructure = Map.of(nodeB, Set.of());
        assertEquals(correctStructure, graph.getAdjacencies());

        assertTrue(graph.canExist());
    }

    @Test
    void graphNodeRemoval_Split_ThreeNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));

        var _newGraphs = graph.removeNodeAndSplit(nodeB);
        assertFalse(_newGraphs.isEmpty());

        var newGraphs = _newGraphs.get();
        assertEquals(1, newGraphs.size());

        var structures = Set.of(
                newGraphs.stream().findFirst().get().getAdjacencies(),
                graph.getAdjacencies()
        );

        var correctStructures = Set.of(
                Map.of(nodeA, Set.of()),
                Map.of(nodeC, Set.of())
        );

        assertEquals(correctStructures, structures);
    }

    @Test
    void graphNodeRemoval_Split_FiveNodes(){
        /*
        * From:
        *      D-E
        *    /
        * A-B-C
        *
        * To:
        * A, C, D-E
        */
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");
        GraphNode<String> nodeD = new GraphNode<>("D");
        GraphNode<String> nodeE = new GraphNode<>("E");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeD, nodeB, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeE, nodeD, LinkType.TYPE_1));

        var _newGraphs = graph.removeNodeAndSplit(nodeB);
        assertFalse(_newGraphs.isEmpty());

        var newGraphs = _newGraphs.get().stream().toList();
        assertEquals(2, newGraphs.size());

        var structures = Set.of(
                newGraphs.get(0).getAdjacencies(),
                newGraphs.get(1).getAdjacencies(),
                graph.getAdjacencies()
        );

        var correctStructures = Set.of(
                Map.of(nodeA, Set.of()),
                Map.of(nodeC, Set.of()),
                Map.of(
                        nodeD, Set.of(new GraphLink<>(nodeE, LinkType.TYPE_1)),
                        nodeE, Set.of(new GraphLink<>(nodeD, LinkType.TYPE_1))
                )
        );

        assertEquals(correctStructures, structures);
    }

    @Test
    void graphMerge_OneAndOne(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph1 = new Graph<>(nodeA);

        GraphNode<String> nodeX = new GraphNode<>("X");
        Graph<String, LinkType> graph2 = new Graph<>(nodeX);

        graph1.mergeWith(nodeA, graph2, nodeX, LinkType.TYPE_1);

        assertEquals(Map.of(), graph2.getAdjacencies());

        var correctGraph1Structure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeX, LinkType.TYPE_1)),
                nodeX, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1))
        );
        assertEquals(correctGraph1Structure, graph1.getAdjacencies());
    }

    @Test
    void graphMerge_1And3(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String, LinkType> graph1 = new Graph<>(nodeA);

        GraphNode<String> nodeX = new GraphNode<>("X");
        GraphNode<String> nodeY = new GraphNode<>("Y");
        GraphNode<String> nodeZ = new GraphNode<>("Z");
        Graph<String, LinkType> graph2 = new Graph<>(nodeX);

        graph2.addNode(nodeY, nodeX, LinkType.TYPE_1);
        graph2.addNode(nodeZ, nodeX, LinkType.TYPE_1);

        graph1.mergeWith(nodeA, graph2, nodeX, LinkType.TYPE_1);

        assertEquals(Map.of(), graph2.getAdjacencies());

        var correctGraph1Structure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeX, LinkType.TYPE_1)),
                nodeX, Set.of(
                        new GraphLink<>(nodeA, LinkType.TYPE_1),
                        new GraphLink<>(nodeY, LinkType.TYPE_1),
                        new GraphLink<>(nodeZ, LinkType.TYPE_1)
                ),
                nodeY, Set.of(new GraphLink<>(nodeX, LinkType.TYPE_1)),
                nodeZ, Set.of(new GraphLink<>(nodeX, LinkType.TYPE_1))
        );

        assertEquals(correctGraph1Structure, graph1.getAdjacencies());
    }

    public enum LinkType {
        TYPE_1,
        TYPE_2,
        TYPE_3
    }
}
