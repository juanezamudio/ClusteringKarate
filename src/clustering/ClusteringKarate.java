/**
 * 
 */
package clustering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

/**
 * @author jzamudio
 * @version 11/21/17
 *
 */
public class ClusteringKarate {
	
	
	public List<Map<String, Double>> bfs (Graph<String, String> graph, String start) {
		Map<String, Double> count = new HashMap<String, Double>();
		Map<String, Double> dist = new HashMap<String, Double>();
		Queue<String> q = new LinkedList<String>();
		Set<String> visited = new HashSet<String>();
		List<Map<String, Double>> result = new ArrayList<Map<String, Double>>();
		
		double value = 0.0;
		
		count.put(start, 1.0);
		dist.put(start, 0.0);
		q.add(start);
		
		while (!q.isEmpty()) {
			String v = q.remove();
			
			for (String u : graph.getNeighbors(v)) {
				if (!visited.contains(u)) {
					visited.add(u);
					q.add(u);
					dist.put(u, dist.get(v) + 1);
					count.put(u, count.get(v));
				} else if (dist.get(u) == dist.get(v) + 1) {
					value = count.get(u);
					value += count.get(v);
					count.put(u, value);
				}	
			}
		}
		
		result.add(count);
		result.add(dist);
		
		return result;
	}
	
	public void eb_clustering (Graph<String, String> graph) {
		Map<String, Map<String, Double>> d = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> sp = new HashMap<String, Map<String, Double>>();
		double score = 0.0;
		double numerator = 0.0;
		double denominator = 0.0;
		double sum = 0.0;
		
		for (String v : graph.getVertices()) {
			List<Map<String, Double>> result = bfs(graph, v);
			
			sp.put(v, result.get(0));
			d.put(v, result.get(1));
		}
		
		// Begin clustering
		for (String x : graph.getVertices()) {
			
			for (String y : graph.getNeighbors(x)) {
				
				
				for (String u : graph.getVertices()) {
					
					
					for (String v : graph.getNeighbors(u)) {
						
					
						if (u == v) {
							break;
						}
						
						if (d.get(x).get(y) == (d.get(x).get(u) + 1 + d.get(v).get(y))) {
							
							score += sp.get(x).get(u) * sp.get(v).get(y);
							
						} else if (d.get(x).get(y) == (d.get(x).get(v) + 1 + d.get(u).get(y))) {
							
							score += sp.get(x).get(v) * sp.get(u).get(y);
							
						} else {
							
							score += 0.0;
							
						}
					}
					
					numerator += score;
					denominator += sp.get(x).get(y);
				}
				
				
				sum += numerator/denominator;
				
			}
			
		}
		
		
	}
	
	public void sn_clustering (Graph<String, String> graph) {
		
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		//The two data files used here
		//W. W. Zachary, An information flow model for conflict and fission in small groups, Journal of Anthropological Research 33, 452-473 (1977). 

		//Initialize a JUNG graph with 34 vertices
		Graph<String, String> g = new SparseGraph<String,String>();
		for(int i = 1; i <= 34; i++) {
			g.addVertex(Integer.toString(i));
		}

		//Parse the zachary karate edge data
		BufferedReader edges = new BufferedReader(new FileReader("karate_club_edges.txt"));
		String line;
		for(int i = 0; (line = edges.readLine()) != null; i++) {
			int spaceIndex = line.indexOf(' ');
			g.addEdge("e" + Integer.toString(i),line.substring(0,spaceIndex), line.substring(spaceIndex+1));
		}
		edges.close();

		//Parse the zachary karate split data
		Map<String,Integer> labels = new HashMap<String,Integer>();
		BufferedReader groups = new BufferedReader(new FileReader("karate_club_true_clusters.txt"));
		while((line = groups.readLine()) != null) {
			int spaceIndex = line.indexOf(' ');
			labels.put(line.substring(0,spaceIndex), Integer.parseInt(line.substring(spaceIndex+1)));
		}
		groups.close();

		//TODO: Run the two clustering algorithms on the graph
		
		
     
		Layout<String,String> l = new FRLayout<String,String>(g);
		Dimension dim = new Dimension(800,800);	
		VisualizationImageServer<String,String> vis = new VisualizationImageServer<String,String>(l,dim);
		
		//Color the vertices based on their label
		Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>() {
            public Paint transform(String s) {
            	switch(labels.get(s)) {
            	case 1:
            		return (Paint) Color.RED;
            	case 2:
            		return (Paint) Color.BLUE;
        		default:
            		return (Paint) Color.BLACK;
            	}
            }
        };
		vis.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
		BufferedImage im = (BufferedImage) vis.getImage(
				new Point2D.Double(dim.getWidth()/2, dim.getHeight()/2),
				dim);
		ImageIO.write((RenderedImage) im, "jpg", new File("out.jpg"));
	}
}
