package graph;

import java.awt.Paint;
import java.util.Set;

import com.google.common.base.Function;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class InOutVertexPaintTransformer<V, E> implements Function<V, Paint> {

	private DirectedSparseGraph<V, E> graph;
	private Paint defaultPaint;
	private Paint inPaint;
	private Paint outPaint;
	private PickedState<V> pi;

	public InOutVertexPaintTransformer(DirectedSparseGraph<V, E> graph, PickedState<V> pi, Paint defaultPaint,
			Paint inPaint, Paint outPaint) {
		this.graph = graph;
		this.pi = pi;
		this.defaultPaint = defaultPaint;
		this.inPaint = inPaint;
		this.outPaint = outPaint;
	}

	public Paint apply(V v) {
		Set<V> pickedVertices = pi.getPicked();
		for (V vert : pickedVertices) {
			if (graph.getPredecessors(vert).contains(v)) {
				return outPaint;
			} else if (graph.getSuccessors(vert).contains(v)) {
				return inPaint;
			}
		}
		return defaultPaint;
	}

}
