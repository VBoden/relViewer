package graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Set;

import javax.swing.JComponent;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;

public class InOutVertexLabelRenderer<E> extends DefaultVertexLabelRenderer {

	private Color defaultColor;
	private Color inColor;
	private Color outColor;

	public InOutVertexLabelRenderer(Color defaultColor, Color inColor, Color outColor) {
		super(defaultColor);
		this.inColor = inColor;
		this.outColor = outColor;
	}

	public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected,
			V vertex) {
		super.getVertexLabelRendererComponent(vv, value, font, isSelected, vertex);
		VisualizationViewer viewer = (VisualizationViewer) vv;
		DirectedSparseGraph<V, E> graph = (DirectedSparseGraph<V, E>) viewer.getGraphLayout().getGraph();
		Set<V> pickedVertices = viewer.getPickedVertexState().getPicked();
		for (V vert : pickedVertices) {
			if (graph.getPredecessors(vert).contains(vertex)) {
				setForeground(outColor);
				break;
			} else if (graph.getSuccessors(vert).contains(vertex)) {
				setForeground(inColor);
				break;
			}
		}
		if (isSelected)
			setForeground(pickedVertexLabelColor);
		return this;
	}
}
