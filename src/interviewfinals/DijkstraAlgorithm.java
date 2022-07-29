package interviewfinals;

import java.util.ArrayList;
import java.util.Arrays;

public class DijkstraAlgorithm {

    private ArrayList<Index> path;
    private ArrayList<ArrayList<Index>> paths;
    private ArrayList<ArrayList<Index>> parent;
    private Index src;
    private Index end;

    /**
     * constructor
     * @param src
     * @param end
     */
    public DijkstraAlgorithm(Index src,Index end){
        this.path = new ArrayList<>();
        this.paths= new ArrayList<>();
        this.src = src;
        this.end = end;
        this.parent = new ArrayList<>();
    }

    /**
     * The function returns the number of the vertex whose weight is
     * the lowest and in addition we did not finish visiting it
     * If the distance is the largest - meaning that the
     * remaining vertices are not in the same component of the return bond -1
     * @param dist
     * @param visited
     * @return int numberNode
     */
    public int minNodeDistance(int[] dist,Boolean[] visited){
        int miniDistance=Integer.MAX_VALUE,numberNode=-1;
        for(int i=0;i<dist.length;i++){
            if(dist[i]<miniDistance && !visited[i]){
                //If there are several whose minimum weight is equal we will
                // choose the vertex if the smallest number
                miniDistance = dist[i];
                numberNode =i;
            }
        }
        return numberNode;
    }

    /**
     *The function receives a graph and returns a list
     * of index lists with all the easy
     * routes from the source to the destination
     * @param matrixAsGraph
     * @return ArrayList<ArrayList<Index>> paths
     */
    public ArrayList<ArrayList<Index>> Dijkstra(MatrixAsGraph matrixAsGraph){
        int numberRow = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix().length,
                numberCol = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[0].length;
        //Turns the matrix into a long array in which it will maintain the
        // easiest weighted distances of each of the vertices
        int dist[] = new int[numberRow*numberCol];

        //The parent array in which each index will keep its parent confirming from which to reach
        // the vertex at the easiest weighted distance
        for(int i = 0; i < numberRow*numberCol; i++){
            parent.add(new ArrayList<>());
        }
        Index index;

        //The parent source index is NULL and the weighted distance is zero
        Arrays.fill(dist,Integer.MAX_VALUE);
        Boolean visited[] = new Boolean[numberRow*numberCol];
        Arrays.fill(visited,false);

        dist[numberCol*src.row+src.column]=matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[src.row][src.column];
        parent.get(src.row*numberCol+src.column).clear();
        parent.get(src.row*numberCol+src.column).add(null);

        for (int count=0;count<numberRow*numberCol;count++) {
            int numberNodeMinDistance = minNodeDistance(dist, visited);
            //If equal to -1 it means that the minimum distance is the largest number and should be
            // stopped because we have reached a vertex that is not in the same binding element
            if (numberNodeMinDistance != -1) {
                if (numberNodeMinDistance == 0) {
                    index = new Index(0, 0);
                } else {
                    //Convert the vertex number in the array to the index position in the matrix so we can find its Reachable neighbors
                    int rowN = numberNodeMinDistance / numberCol, colN = numberNodeMinDistance % numberCol;
                    index = new Index(rowN, colN);
                }

                //Going through each of the Reachable neighbors
                for (Node<Index> reachableNeighbor : matrixAsGraph.getReachableNodes(new Node<Index>(index))) {
                    int rowR = reachableNeighbor.getData().row, colR = reachableNeighbor.getData().column;
                    int position = rowR * numberCol + colR;

                    //If we have not already visited the vertex and the weighted distance is through a parent it
                    // is easier We will change the weighted distance of the neighbor and update his parent
                    //If the distance is equal we will add the parent in the appropriate position in the parent array
                    if (!visited[position] && dist[position] >= dist[numberNodeMinDistance] +
                            matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[rowR][colR]) {
                        dist[position] = dist[numberNodeMinDistance] +
                                matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[rowR][colR];
                        parent.get(position).add(index);
                    }
                }
                visited[numberNodeMinDistance]=true;
            }
        }


        HelpFindParent helpFindParent = new HelpFindParent();
        helpFindParent.find_paths(matrixAsGraph,paths,path,parent,numberCol*numberRow,this.end);

        return paths;
    }
}
