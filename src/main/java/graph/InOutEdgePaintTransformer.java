package graph;

import java.awt.Paint;

import com.google.common.base.Function;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.picking.PickedInfo;

public class InOutEdgePaintTransformer<V, E> implements Function<E, Paint> {
	DirectedSparseGraph<V, E> graph;
	protected Paint defaultPaint;
	protected Paint inPaint;
	protected Paint outPaint;
	protected PickedInfo<V> pi;

	public InOutEdgePaintTransformer(DirectedSparseGraph<V, E> graph, PickedInfo<V> pi, Paint defaultPaint,
			Paint inPaint, Paint outPaint) {
		if (graph == null)
			throw new IllegalArgumentException("PickedInfo instance must be non-null");
		this.graph = graph;
		this.pi = pi;
		this.defaultPaint = defaultPaint;
		this.inPaint = inPaint;
		this.outPaint = outPaint;
	}

	public Paint apply(E e) {
		if (pi.isPicked(graph.getSource(e))) {
			return inPaint;
		} else if (pi.isPicked(graph.getDest(e))) {
			return outPaint;
		} else {
			return defaultPaint;
		}
	}
}
