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
        Graph<String> graph = new Graph<>(nodeA);

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure = Map.of(
            nodeA, Set.of()
        );

        assertEquals(graph.getAdjacencies(), correctStructure);
    }

    @Test
    void graphNodeAddition_RepeatedNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        assertFalse(graph.addNode(nodeA, nodeA, LinkType.TYPE_1));
        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure = Map.of(
                nodeA, Set.of()
        );

        assertEquals(graph.getAdjacencies(), correctStructure);
    }

    @Test
    void graphNodeAddition_NewNode(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");

       assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1))
        );

        assertEquals(graph.getAdjacencies(), correctStructure);
    }

    @Test
    void graphLinkAddition_Triangle(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1), new GraphLink<>(nodeC, LinkType.TYPE_3)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1), new GraphLink<>(nodeC, LinkType.TYPE_2)),
                nodeC, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_3), new GraphLink<>(nodeB, LinkType.TYPE_2))
        );

        assertEquals(graph.getAdjacencies(), correctStructure);
    }

    @Test
    void graph_ConnectedNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        var connectedNodes = graph.getConnectedNodes(nodeA);

        assertEquals(connectedNodes, Set.of(nodeA, nodeB, nodeC));
    }

    @Test
    void graphConnectionRemoval_Unsafe_Triangle(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_2));
        assertTrue(graph.addLink(nodeC, nodeA, LinkType.TYPE_3));

        var secondGraph = graph.removeConnectionAndSplit(nodeA, nodeB);

        assertEquals(secondGraph, Optional.empty());

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_3)),
                nodeB, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_2)),
                nodeC, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_3), new GraphLink<>(nodeB, LinkType.TYPE_2))
        );

        assertEquals(graph.getAdjacencies(), correctStructure);
    }

    @Test
    void graphConnectionRemoval_Split_TwoNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));

        var newGraph = graph.removeConnectionAndSplit(nodeA, nodeB);
        assertFalse(newGraph.isEmpty());

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure1 = Map.of(nodeA, Set.of());
        assertEquals(graph.getAdjacencies(), correctStructure1);

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure2 = Map.of(nodeB, Set.of());
        assertEquals(newGraph.get().getAdjacencies(), correctStructure2);
    }

    @Test
    void graphConnectionRemoval_Split_FourNodes(){
        GraphNode<String> nodeA = new GraphNode<>("A");
        Graph<String> graph = new Graph<>(nodeA);

        GraphNode<String> nodeB = new GraphNode<>("B");
        GraphNode<String> nodeC = new GraphNode<>("C");
        GraphNode<String> nodeD = new GraphNode<>("D");

        assertTrue(graph.addNode(nodeB, nodeA, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeC, nodeB, LinkType.TYPE_1));
        assertTrue(graph.addNode(nodeD, nodeC, LinkType.TYPE_1));

        var newGraph = graph.removeConnectionAndSplit(nodeB, nodeC);
        assertFalse(newGraph.isEmpty());

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure1 = Map.of(
                nodeA, Set.of(new GraphLink<>(nodeB, LinkType.TYPE_1)),
                nodeB, Set.of(new GraphLink<>(nodeA, LinkType.TYPE_1))
        );
        assertEquals(graph.getAdjacencies(), correctStructure1);

        Map<GraphNode<String>, Set<GraphLink<String>>> correctStructure2 = Map.of(
                nodeC, Set.of(new GraphLink<>(nodeD, LinkType.TYPE_1)),
                nodeD, Set.of(new GraphLink<>(nodeC, LinkType.TYPE_1))
        );
        assertEquals(newGraph.get().getAdjacencies(), correctStructure2);
    }
}
