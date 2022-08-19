package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;

public class App extends JApplet {

	/**
	 * the graph
	 */
	DirectedSparseGraph<String, Integer> graph;

	Supplier<DirectedGraph<String, Integer>> graphFactory = new Supplier<DirectedGraph<String, Integer>>() {

		public DirectedGraph<String, Integer> get() {
			return new DirectedSparseMultigraph<String, Integer>();
		}
	};

	Supplier<Tree<String, Integer>> treeFactory = new Supplier<Tree<String, Integer>>() {

		public Tree<String, Integer> get() {
			return new DelegateTree<String, Integer>(graphFactory);
		}
	};

	Supplier<Integer> edgeFactory = new Supplier<Integer>() {
		int i = 0;

		public Integer get() {
			return i++;
		}
	};

	Supplier<String> vertexFactory = new Supplier<String>() {
		int i = 0;

		public String get() {
			return "V" + i++;
		}
	};

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<String, Integer> vv;

	String root;

	AbstractLayout<String, Integer> layout;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		content.add(new App());
		frame.pack();
		frame.setVisible(true);
	}

	public App() {

		// create a simple graph for the demo
		graph = new DirectedSparseGraph<String, Integer>();

		GraphCreator.createTree(graph, edgeFactory);

//		layout = new CircleLayout(graph);
//		layout = new DAGLayout<>(graph);
//		layout = new FRLayout<>(graph);++++++
//		layout = new FRLayout2<>(graph);++++
//		layout = new ISOMLayout<>(graph);
//		layout = new KKLayout<>(graph);
//		layout = new SpringLayout<>(graph);
		layout = new MyLayout<>(graph);
//		radialLayout = new BalloonLayout<String, Integer>(graph);
//		radialLayout.setSize(new Dimension(900, 900));
		vv = new VisualizationViewer<>(layout, new Dimension(600, 600));
		vv.setBackground(Color.white);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		VertexLabelAsShapeRenderer<String, Integer> vlasr = new VertexLabelAsShapeRenderer<>(vv.getRenderContext());
		vv.getRenderContext().setVertexShapeTransformer(vlasr);
		vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<String, Integer>(Color.white, Color.white, true));
		vv.getRenderer().setVertexLabelRenderer(vlasr);
		vv.getRenderContext().setVertexDrawPaintTransformer(new InOutVertexPaintTransformer<String, Integer>(graph,
				vv.getPickedVertexState(), Color.black, Color.red, Color.green));
		vv.getRenderContext()
				.setVertexLabelRenderer(new InOutVertexLabelRenderer<Integer>(Color.blue, Color.red, Color.green));

		vv.getRenderContext().setEdgeDrawPaintTransformer(new InOutEdgePaintTransformer<String, Integer>(graph,
				vv.getPickedVertexState(), Color.black, Color.red, Color.cyan));
		vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.quadCurve(graph));
		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		vv.getRenderContext().setArrowFillPaintTransformer(Functions.<Paint>constant(Color.lightGray));
//		rings = new Rings(radialLayout);

		Container content = getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		final DefaultModalGraphMouse<String, Integer> graphMouse = new DefaultModalGraphMouse<String, Integer>();

		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		JComboBox<?> modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		final ScalingControl scaler = new CrossoverScalingControl();

		vv.scaleToLayout(scaler);

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel controls = new JPanel();
		scaleGrid.add(plus);
		scaleGrid.add(minus);
		controls.add(scaleGrid);
		controls.add(modeBox);
		content.add(controls, BorderLayout.SOUTH);
	}

	class Rings implements VisualizationServer.Paintable {

		BalloonLayout<String, Integer> layout;

		public Rings(BalloonLayout<String, Integer> layout) {
			this.layout = layout;
		}

		public void paint(Graphics g) {
			g.setColor(Color.gray);

			Graphics2D g2d = (Graphics2D) g;

			Ellipse2D ellipse = new Ellipse2D.Double();
			for (String v : layout.getGraph().getVertices()) {
				Double radius = layout.getRadii().get(v);
				if (radius == null)
					continue;
				Point2D p = layout.apply(v);
				ellipse.setFrame(-radius, -radius, 2 * radius, 2 * radius);
				AffineTransform at = AffineTransform.getTranslateInstance(p.getX(), p.getY());
				Shape shape = at.createTransformedShape(ellipse);

				MutableTransformer viewTransformer = vv.getRenderContext().getMultiLayerTransformer()
						.getTransformer(Layer.VIEW);

				if (viewTransformer instanceof MutableTransformerDecorator) {
					shape = vv.getRenderContext().getMultiLayerTransformer().transform(shape);
				} else {
					shape = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, shape);
				}

				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
	}

}
