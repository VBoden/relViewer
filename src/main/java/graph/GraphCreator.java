package graph;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Supplier;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class GraphCreator {

	
	public static void createTree(DirectedSparseGraph<String, Integer> graph, Supplier<Integer> edgeFactory) {
		ObjectReader reader = new com.fasterxml.jackson.databind.ObjectMapper().readerForMapOf(List.class);
		try {
//			Map<String, List<String>> result = reader.readValue(UncomitableConstants.RELATIONS.replaceAll("'", "\""));
			Map<String, List<String>> result = reader.readValue(UncomitableConstants.CUST_PROJ_RELATIONS.replaceAll("'", "\""));
			boolean rootAdded = false;
			for (String key : result.keySet()) {
				if (!rootAdded) {
					graph.addVertex(key);
					rootAdded = true;
				}
				for(String value : result.get(key)) {
				graph.addEdge(edgeFactory.get(), key, value);
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
