package interviewfinals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Submarines {
    private HashSet<HashSet<Index>> allConnectedComponents;
    private int rows;
    private int cols;
    private ThreadPoolExecutor threadPoolExecutor;
    private ThreadLocal<int[]> countInWhichRow;
    private ThreadLocal<int[]> countInWhichCol;
    private ThreadLocal<int[]> lastAndStartIndexInRowAndCol;
    private int indexInList =-1;
    private  ArrayList<HashSet<Index>> listAllConnectedComponents;
    private ReadWriteLock lock;

    /**
     * constructor
     * @param matrix
     */
    public Submarines(Matrix matrix){
        AllConnectedComponents allConnectedComponentsObject = new AllConnectedComponents(matrix);
        this.allConnectedComponents = allConnectedComponentsObject.ConnectedComponents();
        this.rows = matrix.getPrimitiveMatrix().length;
        this.cols = matrix.getPrimitiveMatrix()[0].length;
        this.threadPoolExecutor = new ThreadPoolExecutor(10,10,0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());
        listAllConnectedComponents = new ArrayList<>(allConnectedComponents);
        countInWhichRow = ThreadLocal.withInitial(()->new int[rows]);
        countInWhichCol = ThreadLocal.withInitial(()->new int[cols]);
        lastAndStartIndexInRowAndCol =ThreadLocal.withInitial(()->new int[]{0,0,rows,cols});
        //lastIndexRow=0,lastIndexCol=0,startIndexRow=rows,startIndexCol=cols;
        lock = new ReentrantReadWriteLock();
    }

    /**
     * The function returns an array that describes in a zero
     * index the number of normal submarines according
     * to the conditions and in one index the number of defective submarines according to the conditions.
     * @return int[] numberSubmarines
     */
    public int[] FindCountSubmarinesAndIfInputValid(){
        int[] numberSubmarines ={0,0};

        //when we do execute to thread in the threadPoolExecutor
       Runnable runnable=()->{
           //Critical code snippet
            lock.writeLock().lock();
            indexInList++;
            //Each thread goes through a different connected component
            HashSet<Index> hashSet= listAllConnectedComponents.get(indexInList);
            lock.writeLock().unlock();

            if(hashSet.size()>1){
                for (Index index : listAllConnectedComponents.get(indexInList)){
                    //Passes an index in a binding element and increases the value by one in the
                    // array of rows and columns accordingly.
                    // To create two arrays containing one the number of ones that are
                    // in each row and the other in each column.
                    countInWhichRow.get()[index.row]++;
                    countInWhichCol.get()[index.column]++;

                    //To calculate the sides that should be in the submarine - we will keep the last index that had one
                    // in the row and column and the first index that had one in the row and column
                    if(index.row> lastAndStartIndexInRowAndCol.get()[0]){
                        lastAndStartIndexInRowAndCol.get()[0]=index.row;
                    }
                    if(index.row< lastAndStartIndexInRowAndCol.get()[2]){
                        lastAndStartIndexInRowAndCol.get()[2]=index.row;
                    }
                    if(index.column> lastAndStartIndexInRowAndCol.get()[1]){
                        lastAndStartIndexInRowAndCol.get()[1]=index.column;
                    }
                    if(index.column< lastAndStartIndexInRowAndCol.get()[3]){
                        lastAndStartIndexInRowAndCol.get()[3]=index.column;
                    }
                }
                int diffRow = diffValues(countInWhichRow.get()),diffCol = diffValues(countInWhichCol.get());

                //if the function return -1 in mean that the values in the array is not the same(not include zero)
                //and in means that it is not submarines.
                if(diffRow==-1 || diffCol==-1){
                    numberSubmarines[1]++;
                }
                //calculates the side that should and check if the amount of values in each row or column is
                //equal to that
                else if(diffRow== lastAndStartIndexInRowAndCol.get()[1]- lastAndStartIndexInRowAndCol.get()[3]+1 && diffCol== lastAndStartIndexInRowAndCol.get()[0]- lastAndStartIndexInRowAndCol.get()[2]+1){
                    numberSubmarines[0]++;
                }else{
                    numberSubmarines[1]++;
                }
            }else{
                numberSubmarines[1]++;
            }
        };

       //Each binding component creates its own trade that checks whether the connected
        // component is a submarine or not
        for(HashSet<Index> hashSet: allConnectedComponents){
            threadPoolExecutor.execute(runnable);
        }

        while (threadPoolExecutor.getActiveCount()!=0){}
        threadPoolExecutor.shutdown();

        return numberSubmarines;
    }

    /**
     *The function receives an array that denotes the number of
     *  units in each row or column in the above connected component
     * And returns - if the number of units is different in each of
     * the rows or columns accordingly (ie the array contains different values without reference to zero) return -1
     * And if the quantity is the same (i.e. the array contains identical
     * values without reference to zero) return the value that is in the array in each cell
     * @param numArray
     * @return int numOfDifferentVals
     */
    public int diffValues(int[] numArray){
        int numOfDifferentVals=0;
        ArrayList<Integer> diffNum = new ArrayList<>();
        for(int i=0; i<numArray.length; i++){
            if(!diffNum.contains(numArray[i]) && numArray[i]!=0){
                diffNum.add(numArray[i]);
            }
        }
        //the quantity is the same (i.e. the array contains identical
        //values without reference to zero)
        if(diffNum.size()==1){
            numOfDifferentVals = diffNum.get(0);
        }
        else{
            numOfDifferentVals = -1;
        }
        return numOfDifferentVals;
    }
}
