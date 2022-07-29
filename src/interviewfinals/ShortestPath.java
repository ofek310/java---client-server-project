package interviewfinals;

import java.util.*;

public class ShortestPath {

    private MatrixAsGraph matrixAsGraph;
    private Index start;
    private Index end;
    private ArrayList<ArrayList<Index>> paths;
    private ArrayList<ArrayList<Index>> parent;
    private ArrayList<Index> path;

    /**
     * constructor
     * @param matrixAsGraph
     * @param start
     * @param end
     */
    public ShortestPath(MatrixAsGraph matrixAsGraph,Index start,Index end){
        this.matrixAsGraph = matrixAsGraph;
        this.start = start;
        this.end = end;
        this.paths = new ArrayList<>();
        this.parent = new ArrayList<>();
        int n = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix().length*matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[0].length;
        for(int i = 0; i < n; i++){
            parent.add(new ArrayList<>());
        }
        this.path = new ArrayList<>();
    }


    /**
     *The function performs sophisticated DFS, which means that every time
     *  it finds a parent whose distance is shorter, it updates the distance
     *  and adds the parent to the parent array.
     * If the array is equal it adds another parent from which to reach the vertex in the shortest way
     * @return ArrayList<ArrayList<Index>> paths
     */
    // Function which performs Dfs, from the given source vertex
    public ArrayList<ArrayList<Index>> Dfs() {
        int n = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix().length*matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[0].length;
        // dist will contain shortest distance, from start to every other vertex
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Stack<Index> s = new Stack<Index>();
        Set<Index> finished = new LinkedHashSet<>();
        // Insert source vertex in queue and make, its parent null and distance 0
        s.push(start);
        int numberColumn = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[0].length;
        parent.get(start.row*numberColumn+start.column).clear();
        parent.get(start.row*numberColumn+start.column).add(null);
        dist[start.row*numberColumn+start.column] = 0;

        // Until Stack is empty
        while (!s.isEmpty()) {
            Index node = s.pop();
            finished.add(node);
            for (Node<Index> reachableNeighbor : matrixAsGraph.getReachableNodes(new Node<Index>(node))) {
                if(!finished.contains(reachableNeighbor) && !s.contains(reachableNeighbor)){
                    int placeReachableNeighbor = reachableNeighbor.getData().row*numberColumn+reachableNeighbor.getData().column;
                    int placeNode = node.row*numberColumn+node.column;
                    if (dist[placeReachableNeighbor] > dist[placeNode] + 1) {
                        // A shorter distance is found, So erase all the previous parents, and insert new parent u in parent[v]
                        dist[placeReachableNeighbor] = dist[placeNode] + 1;
                        s.push(reachableNeighbor.getData());
                        parent.get(placeReachableNeighbor).clear();
                        parent.get(placeReachableNeighbor).add(node);
                    }
                    else if (dist[placeReachableNeighbor] == dist[placeNode] + 1) {
                        // Another candidate parent for,shortest path found
                        parent.get(placeReachableNeighbor).add(node);
                    }
                }
            }
        }

        HelpFindParent helpFindParent = new HelpFindParent();
        helpFindParent.find_paths(matrixAsGraph,paths,path,parent,n,this.end);
        return paths;
    }
}
