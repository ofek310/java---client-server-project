package interviewfinals;

import java.util.ArrayList;
import java.util.Collections;

public class HelpFindParent {
    /**
     * The function is a recursive function in that it is called again each time to
     * find a short / easy path from the root of the graph to the destination (U which it receives in the function)
     * The function starts from the target function and passes over the parent of
     * the vertex once and thus it puts in the path array the shortest / easiest path and after
     * finding the path i.e. we have reached the vertex which is NULL we will reverse the path (because it is
     * saved from the end to the beginning with auxiliary array). Of indexes that contains all the easy / short routes that
     * exist between the source and the destination
     * @param matrixAsGraph
     * @param paths
     * @param path
     * @param parent
     * @param n
     * @param u
     */
    public void find_paths(MatrixAsGraph matrixAsGraph, ArrayList<ArrayList<Index>> paths, ArrayList<Index> path,
                                  ArrayList<ArrayList<Index>> parent, int n, Index u) {
        int numberColumn = matrixAsGraph.getInnerMatrix().getPrimitiveMatrix()[0].length;

        // Base Case
        if (u == null) {
            //copy array
            ArrayList<Index> p = new ArrayList<>();
            for(int i=0;i<path.size();i++){
                p.add(path.get(i));
            }
            Collections.reverse(p);
            paths.add(new ArrayList<Index>(p));
            return;
        }

        // Loop for all the parents, of the given vertex
        for (Index par : parent.get(u.row*numberColumn+u.column)) {
            // Insert the current, vertex in path
            path.add(u);
            // Recursive call for its parent
            find_paths(matrixAsGraph,paths, path, parent, n, par);
            // Remove the current vertex
            path.remove(path.size()-1);
        }
    }
}
