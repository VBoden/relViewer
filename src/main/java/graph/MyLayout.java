package graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Function;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

public class MyLayout<V, E> extends AbstractLayout<V, E> {

	private static final int TOP_COUNT = 5;

	public MyLayout(Graph<V, E> graph, Function<V, Point2D> initializer, Dimension size) {
		super(graph, initializer, size);
	}

	public MyLayout(Graph<V, E> graph, Function<V, Point2D> initializer) {
		super(graph, initializer);
	}

	public MyLayout(Graph<V, E> graph) {
		super(graph);
	}

	public MyLayout(Graph<V, E> graph, Dimension size) {
		super(graph, size);
	}

	public void initialize() {
		List<List<V>> verticesByLevels = divideByLevels();
		createAllocation(verticesByLevels);
//		createSimpleAllocation();
	}

	private List<List<V>> divideByLevels() {
		List<List<V>> levels = new ArrayList<>();
		List<V> top = new ArrayList<>(TOP_COUNT);
		for (int i = 0; i < TOP_COUNT; i++) {
			top.add(getSortedByIncoming().get(i).getKey());
		}
		levels.add(top);
		Set<V> usedVertices = new HashSet<>();
		usedVertices.addAll(top);
		addPredecessors(levels, top, usedVertices);
//		V startVertex = getSortedByIncoming().get(0).getKey();
//		levels.add(Collections.singletonList(startVertex));
//		usedVertices.add(startVertex);
//		Collection<V> predecessors = graph.getPredecessors(startVertex);
//		levels.add(new ArrayList<>(predecessors));
//		usedVertices.addAll(predecessors);
//		addPredecessors(levels, predecessors, usedVertices);
		List<V> withoutPredecessors = graph.getVertices().stream().filter(v -> !usedVertices.contains(v))
				.collect(Collectors.toList());
		levels.add(withoutPredecessors);
		return levels;
	}

	private void addPredecessors(List<List<V>> levels, Collection<V> vertices, Set<V> usedVertices) {
		if (vertices.isEmpty()) {
			return;
		}
		List<V> added = new ArrayList<>();
		for (V vertex : vertices) {
			Collection<V> predecessors = graph.getPredecessors(vertex);
			for (V v : predecessors) {
				if (!usedVertices.contains(v)) {
					added.add(v);
					usedVertices.add(v);
				}
			}
		}
		levels.add(added);
		addPredecessors(levels, added, usedVertices);
	}

	private List<Entry<V, Collection<V>>> getSortedByIncoming() {
		Map<V, Collection<V>> incoming = new HashMap<>();
		for (V vertex : graph.getVertices()) {
			incoming.put(vertex, graph.getPredecessors(vertex));
		}
		List<Entry<V, Collection<V>>> keysByIncoming = incoming.entrySet().stream()// .filter(entry ->
																					// entry.getValue().size()>0)
				.sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size()).collect(Collectors.toList());
		return keysByIncoming;
	}

	private void createAllocation(List<List<V>> verticesByLevels) {
		int i = 1;
		int j = 0;
		for (List<V> v : verticesByLevels) {
			int movePos = v.size() > 5 ? 30 : 0;
			int interval = movePos ==0 ? 200 : 50;
			int startLine = size.width / 2 - interval * v.size() / 2;
			for (V vertex : v) {
//				setLocation(vertex, size.width / 2 + 50 * j / 2 * getSign(j), 50 + 150 * i + 30*(j%5));
				setLocation(vertex, startLine + interval * j, 50 + 150 * i + movePos * (j % 5));
				j++;
			}
			i++;
			j = 0;
		}

	}

	private void createSimpleAllocation() {
		int i = 1;
		int j = 0;
		int shift = i % 2 * 25;

		for (V vertex : graph.getVertices()) {
//    		setLocation(vertex, size.width/2, 50+50*i);
//    		setLocation(vertex, shift+size.width/2+10*j*(-1*(j+1)%2), 50+50*i);
			setLocation(vertex, size.width / 2 + 50 * j / 2 * getSign(j), 50 + 50 * i + 10 * getSign(j));
			j++;
			if ((j + 1) / 2 == i) {
				j = 0;
				i++;
				shift = i % 2 * 25;
			}
			if (i == 200) {
				break;
			}
//    	Point2D c = apply(vertex);
//        c.setLocation(c.getX()+xOffset, c.getY()+yOffset);
//		setLocation(vertex, c);
		}
	}

	private int getSign(int j) {
		return j % 2 == 0 ? 1 : -1;
	}

	public void reset() {
	}
}
