package interviewfinals;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AllConnectedComponents {

    private Matrix copyMatrix;
    private LinkedHashSet<HashSet<Index>> allConnectedComponents;

    /**
     * constructor
     * @param matrix
     */
    public AllConnectedComponents(Matrix matrix){
        this.copyMatrix = new Matrix(matrix.getPrimitiveMatrix());
        this.allConnectedComponents =  new LinkedHashSet<HashSet<Index>>();
    }

    /**
     * The function goes through all the vertices of the graph and where
     * the vertex is one it runs DFS on it and thus gets the binding element
     * from it (then changes all the vertices of that binding element to zero).
     * and return all the connected components in the graph.
     * @return LinkedHashSet<HashSet<Index>> allConnectedComponents
     */
    public LinkedHashSet<HashSet<Index>> ConnectedComponents(){
        List<HashSet<Index>> l =  new ArrayList<HashSet<Index>>();
        MatrixAsGraph matrixAsGraph = new MatrixAsGraph(copyMatrix);
        for (int i=0;i<copyMatrix.getPrimitiveMatrix().length;i++) {
            for (int j = 0; j < copyMatrix.getPrimitiveMatrix()[0].length; j++) {
                //DFS not work when the root is 0,need to check this...
                if (copyMatrix.getPrimitiveMatrix()[i][j] != 0) {
                    matrixAsGraph.setSource(new Index(i, j));
                    DfsVisit<Index> algorithm = new DfsVisit<>();
                    HashSet<Index> connectedComponent = algorithm.traverse(matrixAsGraph);
                    l.add(connectedComponent);
                    /*when find a connected component we change all its values to zero, so that
                    we do not repeat the same connected component and look for another
                    connected component*/
                    Index[] indexA = connectedComponent.toArray(new Index[connectedComponent.size()]);
                    for (int m = 0; m < indexA.length; m++) {
                        copyMatrix.getPrimitiveMatrix()[indexA[m].row][indexA[m].column] = 0;
                    }
                }
            }
        }
        //sort the list by size of Connected Component
        Collections.sort(l, new Comparator<HashSet<Index>>() {
            @Override
            public int compare(HashSet<Index> o1, HashSet<Index> o2) {
                if (o1.size() > o2.size())
                    return 1;
                else if (o1.size() < o2.size())
                    return -1;
                return 0;
            }
        });

        for(HashSet<Index> hashSetObject : l){
            allConnectedComponents.add(hashSetObject);
        }
        return allConnectedComponents;
    }
}
