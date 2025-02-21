package dev.mrcabbagestick.graph.graph;

import org.junit.jupiter.api.Test;

import java.util.Map;
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
}
