package interviewfinals;

import java.io.Serializable;
import java.util.*;

public class DfsVisit<T> implements Serializable {
    private Stack<Node<T>> workingStack;
    private Set<Node<T>> finished;

    public DfsVisit(){
        workingStack = new Stack<>();
        /*
         Set - a data structure with no duplicates
         TreeSet - Set that preserves natural ordering
         HashSet - Set with a Hash Table, which enables search operation in O(1)
         LinkedHashSet - HashSet with a linked list which enables insertion order
         */
        finished = new LinkedHashSet<>();
    }

    /*
    נשתמש במחסנית עבור הקודקודים שגילינו
    עבור הקודקודים שסיימנו איתם אפשר להשתמש בסט
    סט - מבנה נתונים שאין בו כפילויות ובדרך כלל אין משמעות לסדר

    נכניס למחסנית את הקודקוד שממנו מתחילים (שורש)
    כל עוד המחסנית לא ריקה:
        נוציא את הקודקוד בראש המחסנית ונשמור אותו למשתנה
        את הקודקוד הזה נכניס לסט של הקודקודים שסיימנו איתם
        נבין מהם הקודקדים שישיגים ממנו באמצעות המתודנה getReachableNodes
        עבור כל קודקוד ישיג:
        אם הקודקוד לא נמצא במחסנית וגם שהוא לא נמצא בסט של אלה שסיימתי איתם:
            נכניס אותו למחסנית
  */
  /*
            1 1 0 0 1
            0 1 1 0 1
            0 1 1 0 0

            מחסנית

            סט של קודקודים שסיימתי איתם
            (0,0)
            (0,1)
            (1,1)
            (2,1)
            (2,2)
            (1,2)
            רשימה של קודקודים ישיגים
   */

    /**
     * The function does DFS and returns the connected
     * element from the root of the graph
     * @param aGraph
     * @return HashSet<T> blackSet
     */
    public HashSet<T> traverse(Graph<T> aGraph){
        workingStack.push(aGraph.getRoot());
        while (!workingStack.empty()){
            Node<T> removed = workingStack.pop();
            finished.add(removed);
            Collection<Node<T>> reachableNodes = aGraph.getReachableNodes(removed);
            for(Node<T> reachableNode :reachableNodes){
                if (!finished.contains(reachableNode) &&
                        !workingStack.contains(reachableNode)){
                    workingStack.push(reachableNode);
                }
            }
        }
        HashSet<T> blackSet = new LinkedHashSet<>();
        for (Node<T> node: finished)
            blackSet.add(node.getData());
        finished.clear();
        return blackSet;
    }
}
