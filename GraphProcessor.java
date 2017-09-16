import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
/**
 * Graph Processor class that computes Strongly connnected components of a graph
 * It reads the graph from a txt file and stores it in a newly created data structure 
 * This class also computes bfs path for 2 vertices
 * @author Smriti
 *
 */
public class GraphProcessor {
	private Map<String,ArrayList<String>> graph;
	//stack to store nodes 
	Stack<String> S = new Stack<>();
	//hashset to store visited elements while doing dfs
	Set<String> visited = new HashSet<String>();
	//hashset to store visited elements while doing reverse dfs
	Set<String> revVisited = new HashSet<String>();
	//arraylist that holds elements of all strongly connected components  
	 ArrayList<ArrayList<String>> scc = new ArrayList<ArrayList<String>>();
	/**
	 * graphData hold the absolute path of a file that stores a directed 
	 * graph.This file will be of the following format: First line indicates 
	 * number of vertices. Each subsequent line lists a directed edge of the 
	 * graph.The vertices of this graph are represented as strings.
	 * @param graphData
	 * @throws FileNotFoundException 
	 */
	public GraphProcessor (String graphData) throws FileNotFoundException{
		graph = new HashMap<String,ArrayList<String>>();
		//ArrayList<String> values = new ArrayList<>();
		File file = new File(graphData);
		Scanner scan = new Scanner(file);
		int vertices = scan.nextInt();
		while(scan.hasNext()){
			String key = scan.next();
			ArrayList<String> value = new ArrayList<>();
			if(graph.containsKey(key)){
				value.addAll(graph.get(key));
				//graph.get(key).add(scan.next());
			}
				value.add(scan.next());
				graph.put(key,value);
		}
		scan.close();	
		SCC(graph);
	}
	
	/**
	 * method that returns a reverse graph in form of adjacency list,in which 
	 * every edge direction is reversed.
	 * @param adjList
	 */
	private  Map<String, ArrayList<String>> reverseAdjList(Map<String,ArrayList<String>> adjList) {
		   Map<String, ArrayList<String>> tGraph = new HashMap<>();
		    for (Map.Entry<String, ArrayList<String>> entry : adjList.entrySet()) {
		        for (String value : entry.getValue()) {
		        	ArrayList<String> newValues = tGraph.get(value);
		            if (newValues == null) {
		                newValues = new ArrayList<>();
		                tGraph.put(value, newValues);
		            }
		            newValues.add(entry.getKey());
		        }
		    }
		    return tGraph;
	}
	
	/**
	 * method used to compute DFS of the graph
	 * @param adjList
	 */
	 private void DFS(Map<String,ArrayList<String>> adjList,String source) { 	
		 visited.add(source);
		 if(adjList.containsKey(source)){
			 for (String destination: adjList.get(source)){
				 if(!(visited.contains(destination))){
					DFS(adjList,destination);
				 }
			 }	
		 }
		 S.push(source);
	 }
	
	 /**
	  * Method to compute DFS of graph with reversed vertices
	  * @param adjList
	  * @param source
	  */
	 private void reverseDFS(Map<String,ArrayList<String>> adjList,String source, int index) { 	
		 visited.add(source);
		 if(scc.size()>index){
			 scc.get(index).add(source);
		 }
		 else{
			 ArrayList<String> sccVertices = new ArrayList<String>();
			 sccVertices.add(source);
			 scc.add(index, sccVertices);
		 }
		 if(adjList.containsKey(source)){
			 for (String destination: adjList.get(source)){
				 if(!(visited.contains(destination))){
					 reverseDFS(adjList,destination,index);
				 }
			 }
		 }
	 }
	 
	 /**
	  * Method to compute all strongly connected components
	  * @param adjList
	  */
	 private void SCC(Map<String,ArrayList<String>> adjList){
		//counter to keep track of no. of scc's
		 int sccCounter = 0; 
		 //loop through all the vertices till all of them are stored in visited
		 for(String vertex: adjList.keySet()){
			 if(!(visited.contains(vertex))){
				 DFS(adjList,vertex);
			 }
		 }
		 String source = "";
		 visited.clear();
		 //Do a DFS based off vertex finish time in decreasing order on reverse graph.
		 while (!(S.isEmpty())){
			 source = S.pop();
			 if(!(visited.contains(source))){
				 //adding each strongly connected component to scc list
				 reverseDFS(reverseAdjList(adjList),source,sccCounter);
				 sccCounter++;
			 }
		 }
	 }
	 
	/**
	 * Returns the out degree of v.
	 * @param v
	 * @return
	 */
	public int outDegree(String v){
		//vertex has an outdegree
		if(graph.containsKey(v)){
			return graph.get(v).size();
		}
		//vertex does not have an outdegree
		else{
			return -1;
		}

	}
	
	/**
	 * Returns vertex with highest outdegree 
	 * @return
	 */
	private String getVertexHOD(){
		//highest number of out vertex
		int max=0;
		//vertex with highest outdegree
		String key="";
		for(Map.Entry<String, ArrayList<String>> entry : graph.entrySet()){
			if(max<entry.getValue().size()){
				key=entry.getKey();
				max=entry.getValue().size();
			}
		}
		return key;
	}
	
	/**
	 * Returns true if u and v belong to the same SCC; otherwise returns false.
	 * @param u
	 * @param v
	 * @return
	 */
	public boolean sameComponent(String u, String v){
		for(int i=0; i<scc.size();i++){
			if(scc.get(i).contains(u) && scc.get(i).contains(v)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return all the vertices that belong to the same Strongly Connected 
	 * Component of v (including v). 
	 * @param v
	 * @return
	 */
	public ArrayList<String> componentVertices(String v){
		for(int i=0; i<scc.size();i++){
			if(scc.get(i).contains(v)){
				return scc.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns the size of the largest component.
	 * @return
	 */
	public int largestComponet(){
		int maxSize=0;
		for(int i=0; i<scc.size();i++){
			maxSize = Math.max(scc.get(i).size(),maxSize);
		}
		return maxSize;
	}
	
	/**
	 * Returns the number of strongly connect components.
	 * @return
	 */
	public int numComponents(){
		return scc.size();	
	}
	
	/**
	 * bfsPath(String u, string v) Returns the BFS path from u to v. This 
	 * method returns an array list of strings that represents the BFS path 
	 * from u to v. First vertex in the path must be u and the last vertex 
	 * must be v. If there is no path from u to v, then this method returns 
	 * an empty list.
	 * @param u
	 * @param v
	 * @return
	 */
	public ArrayList<String> bfsPath(String u, String v){
		ArrayList<String> bfsPathVertices = new ArrayList<String>();
		int counter = 0;
		//queue to store vertices in the order they are to be examined
		Queue<String> Q = new LinkedList<String>();
		//hashset to store visited elements
		Set<String> visited = new HashSet<String>();
		String source = u;
		Q.add(source);
		visited.add(u);
		//bfsPathVertices.add(source);
		while(!(Q.isEmpty())){
			counter++;
			source = Q.poll();
			bfsPathVertices.add(source);
			//this means u=v => start and end vertices are the same so add source to bfsPathVertices list as end vertex
			if (source.equals(v) && counter==1){
				bfsPathVertices.add(source);
	            break;
	        }
			else if(source.equals(v) && counter>1){
				break;
			}
			else{
	        	if(graph.containsKey(source)){
					for (String neighbors: graph.get(source) ){
						if(!(visited.contains(neighbors))){
							Q.add(neighbors);
							visited.add(neighbors);		
						}
					}
	        	}
	        }
		}
		//clear the list to be returned since there is no path
		if(!(visited.contains(v))){
			bfsPathVertices.clear();
		}
		return bfsPathVertices;
	}
}
