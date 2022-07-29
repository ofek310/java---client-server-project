package interviewfinals;

import server.IHandler;

import javax.print.attribute.SetOfIntegerSyntax;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  This class handles Matrix-related tasks
 * Adapts the functionality  of IHandler to a Matrix object
 */
public class MatrixHandler implements IHandler {
    private Matrix matrix;
    private Index sourceIndex;
    private boolean doWork;


    @Override
    public void handleClient(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        /*
        data is sent eventually as bytes
        read data as bytes then transform to meaningful data
        ObjectInputStream and ObjectOutputStream can read and write both primitives
        and Reference types
         */
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);

        doWork = true;

        while(doWork){
            switch(objectInputStream.readObject().toString()){
                case "matrix":{
                    // expect to get a 2d array. handler will create a Matrix object
                    int[][] anArray = (int[][])objectInputStream.readObject();
                    System.out.println("Got 2d array");
                    this.matrix = new Matrix(anArray);
                    this.matrix.printMatrix();
                    break;
                }

                case "get neighbors":{
                    this.sourceIndex = (Index)objectInputStream.readObject();
                    if (this.matrix!=null){
                        List<Index> neighbors =
                                new ArrayList<>(this.matrix.getNeighbors(this.sourceIndex));
                        System.out.println(neighbors);
                        objectOutputStream.writeObject(neighbors);
                    }
                    break;
                }

                case "connected component":{
                    this.sourceIndex = (Index)objectInputStream.readObject();
                    if (this.matrix!=null){
                        MatrixAsGraph matrixAsGraph = new MatrixAsGraph(this.matrix);
                        matrixAsGraph.setSource(this.sourceIndex);
                        if(matrix.getPrimitiveMatrix()[this.sourceIndex.row][this.sourceIndex.column]!=1){
                            DfsVisit<Index> algorithm = new DfsVisit<>();
                            Set<Index> connectedComponent = algorithm.traverse(matrixAsGraph);
                            System.out.println(connectedComponent);
                            objectOutputStream.writeObject(connectedComponent);
                        }
                    }
                    break;
                }

                case "question1":{
                    HashSet<HashSet<Index>> allConnectedComponents = new HashSet<HashSet<Index>>();
                    if(matrix!=null){
                        AllConnectedComponents allConnectedComponentsObject = new AllConnectedComponents(matrix);
                        allConnectedComponents = allConnectedComponentsObject.ConnectedComponents();
                        objectOutputStream.writeObject(allConnectedComponents);
                    }
                    break;
                }

                case "question2":
                {
                    ArrayList<ArrayList<Index>> shortestPaths =new ArrayList<ArrayList<Index>>();
                    Index start = (Index)objectInputStream.readObject();
                    Index end = (Index)objectInputStream.readObject();
                    if(matrix!=null){
                        MatrixAsGraph matrixAsGraph= new MatrixAsGraph(matrix);
                        matrixAsGraph.setSource(start);
                        //provided that the start index is non-zero
                        if(matrix.getPrimitiveMatrix()[start.row][start.column]!=0){
                            DfsVisit<Index> algorithm = new DfsVisit<>();
                            HashSet<Index> connectedComponent = algorithm.traverse(matrixAsGraph);
                            //It is possible that the start index does not
                            //belong to the same connected component of the destination index
                            //or it does nit reach it(ie zero)
                            if(connectedComponent.contains(end)){
                                ShortestPath shortestPath = new ShortestPath(matrixAsGraph,start,end);
                                shortestPaths = shortestPath.Dfs();
                            }
                        }
                        objectOutputStream.writeObject(shortestPaths);
                    }
                    break;
                }
                case "question3":
                {
                    int[] numberSubmarines;
                    if(matrix!=null){
                        Submarines submarines = new Submarines(matrix);
                        numberSubmarines = submarines.FindCountSubmarinesAndIfInputValid();
                        objectOutputStream.writeObject(numberSubmarines);
                    }
                    break;
                }

                case "question4":
                    {
                        ArrayList<ArrayList<Index>> easyPaths =new ArrayList<ArrayList<Index>>();
                        Index start = (Index)objectInputStream.readObject();
                        Index end = (Index)objectInputStream.readObject();
                        if(matrix!=null){
                            MatrixAsGraph matrixAsGraph = new MatrixAsGraph(matrix);
                            if(matrix.getPrimitiveMatrix()[start.row][start.column]!=0 && matrix.getPrimitiveMatrix()[end.row][end.column]!=0){
                                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(start,end);
                                easyPaths = dijkstraAlgorithm.Dijkstra(matrixAsGraph);
                            }
                        }
                        objectOutputStream.writeObject(easyPaths);
                        break;
                    }

                case "stop":{
                    doWork = false;
                    break;
                }
            }
        }
    }
}
