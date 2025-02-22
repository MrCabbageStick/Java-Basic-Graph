package dev.mrcabbagestick.graph.graph;

public record GraphLink<NodeType, LinkType>(GraphNode<NodeType> node, LinkType type){}
