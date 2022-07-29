package server;

import interviewfinals.Index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class Client {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Socket clientSocket = new Socket("127.0.0.1",8010);
            System.out.println("Socket created");

            ObjectOutputStream toServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream fromServer = new ObjectInputStream(clientSocket.getInputStream());

            int[][] sourceArray = {
                    {1,1,0},
                    {0,0,0},
                    {0,1,1},
                    {0,1,1}
            };

            toServer.writeObject("matrix");
            toServer.writeObject(sourceArray);

            Index index1 = new Index(0,0);
            Index index2 = new Index(0,1);

            toServer.writeObject("get neighbors");
            toServer.writeObject(index2);

            List<Index> neighbors = new ArrayList<Index>((List<Index>)fromServer.readObject());
            System.out.println("Neighbors of " + index2 + ": " + neighbors);

            /*toServer.writeObject("connected component");
            toServer.writeObject(index1);

            Set<Index> connectedComponent = new LinkedHashSet<Index>((Set<Index>)fromServer.readObject());
            System.out.println("Connected Component of " + index1 + ": " + connectedComponent);*/

            toServer.writeObject("question1");
            LinkedHashSet<HashSet<Index>> allConnectedComponents = new LinkedHashSet<HashSet<Index>>((LinkedHashSet<HashSet<Index>>)fromServer.readObject());
            System.out.println("question1 - allConnectedComponents : " + allConnectedComponents);

            toServer.writeObject("question2");
            Index start = new Index(1,0);
            Index end = new Index(2,2);
            toServer.writeObject(start);
            toServer.writeObject(end);
            ArrayList<ArrayList<Index>> shortestPaths = new ArrayList<ArrayList<Index>>((ArrayList<ArrayList<Index>>)fromServer.readObject());
            System.out.println("question2 - shortestPaths : " + shortestPaths);

            toServer.writeObject("question3");
            int[] numberSubmarines = (int[])fromServer.readObject();
            if(numberSubmarines[1]==0){
                System.out.println("question3 - submarines : input valid, number valid submarines " + numberSubmarines[0]);
            }else{
                System.out.println("question3 - submarines : input not valid, number valid submarines " + numberSubmarines[0]);
            }

            int[][] sourceArrayQuestions4 = {
                    {100,0,10},
                    {500,10,10}
            };

            toServer.writeObject("matrix");
            toServer.writeObject(sourceArrayQuestions4);
            toServer.writeObject("question4");
            Index startE = new Index(0,0);
            Index endE = new Index(1,2);
            toServer.writeObject(startE);
            toServer.writeObject(endE);
            ArrayList<ArrayList<Index>> easyPaths = new ArrayList<ArrayList<Index>>((ArrayList<ArrayList<Index>>)fromServer.readObject());
            System.out.println("question4 - easyPaths : " + easyPaths);

            toServer.writeObject("stop");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
