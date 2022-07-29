package server;

import interviewfinals.MatrixHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a TCP server that can handle multiple clients
 * concurrently. It can solve different algorithmic problems using
 * dedicated handler types
 */
public class InternetServer {
    /*
    tap http://www.ynet.co.il in chrome's address bar and click enter
    in OSI's 5 Layer model, transport layer is mainly in charge of:
    Transport layer is in charge of multiplexing: the ability to transmit different
    kinds of data on the same line '

    the browser wants to send an HTTP GET request in order to receive ynet's home page
    (HTML document and other referenced objects)
    1. check if exists an IP address associated with this named address:
        A. Web browser cache
        B. OS Cache
        C. Router cache

        2. Check with Local DNS Resolver: for home users, mostly ISP local DNS resolver.
        a user "contacts" Local DNS resolver recursively.
        expect either the DNS Record from DNS server's cache or if not in cache,
        contact a different resolver in DNS system.
        there are different kinds of dns records,
        we need DNS A record

        Who needs to send the request for A record?
        the browser.
        DNS A query request is done by the browser (In application layer).

        The application layer is an abstraction
        our web browser "thinks" that it communicates with a process on the
        same computer (we don't actually employ IPC)
        in order to communicate with the DNS server we need services from
        the layer below (Transport layer)

        we need additional services from the operating system:
        Socket - a data pipeline that is associated with 3 features:
        1. IP address
        2. Port - together with the transport layer protocol (UDP/TCP), port number
        enables to differentiate between different kinds of data
        3. Transport layer protocol - either UDP or TCP

        before we can send a DNS-A record request to a DNS server,
        OS will need to allocate a UDP SOCKET (most of the times) with port
        number 53.
        ports 1-1023 are predefined by the OS.

        OS will allocate A UDP socket associated with port 53,
        only then can the browser sends a DNS request.
        DNS Server sends a DNS response with the A query.

        now we have the IP address. what will happen now?

        Again, we need the services of the transport layer and the OS.
        HTTP protocol in application layer uses TCP in transport layer.
        3. OS will allocate TCP Socket associated with the IP address returned in the
        DNS A record.
        4. Only after TCP socket is created, can the browser send an HTTP GET request
        to the web server of ynet.

        Transport layer - multiplexing. send different kinds of data on the
        same communications pipeline

        Socket - abstraction for 2-way pipeline of data (of a certain kind)
        the kind is determined by the socket number + transport layer protocol


     */
    private final int port;
    private boolean stopServer; // TODO: transparency between threads
    private ThreadPoolExecutor clientsPool; // handle multiple clients concurrently
    /*
    ThreadPoolExecutor is a data structure that mainly contains 2 components:
    1. A dynamic array of threads
    2. Queue of tasks - Runnable/Callable tasks
     */
    private ThreadPoolExecutor clientsConnect;
    private IHandler requestHandler;

    public InternetServer(int port){
        this.port = port;
        this.clientsPool = null;
        this.requestHandler = null;
        this.stopServer = false; // if server should handle clients' requests
    }

    /**
     * In order for the accept to also be multi-threaded we created a new ThreadPoolExecutor
     * and put all the code into a suitable runnable - Requirement 2
     * @param concreteHandler
     */
    public void supportClients(IHandler concreteHandler){
        this.requestHandler = concreteHandler;
        /*
        No matter if handle 1 client or multiple clients,
        once a server has several operations at the same time,
        we ought to define different executable paths (threads)
         */

        Runnable clientHandling = ()->{
            this.clientsPool =
                    new ThreadPoolExecutor(
                            10,15,200, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>());
            this.clientsConnect =
                    new ThreadPoolExecutor(
                            10,15,200, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>());
            /*
        SOCKET API:
        Server-side:

        1. Create Server Socket
        ServerSocket is a special kind of socket that enables handling incoming requests
        from clients.
        * in Java, we can specify the backlog
        2. Bind Server Socket object to a specific port number (using either TCP/UDP)
        3. Tell Server Socket object to listen for incoming connection requests from clients
        4. Try to accept those incoming connection requests
        * once a client is successful in connecting to the server,
        an operational socket is returned.
        The server! maintains an operational socket for each client
             */
            try {
                //2
                ServerSocket serverSocket = new ServerSocket(this.port,50);
                while (!stopServer){
                    Runnable connectClient= () ->{
                        // listen + accept (phases 3+4), are done by accept method
                        try{
                            Socket clientToServerConnection = serverSocket.accept();
                            Runnable specificClientHandling = ()->{
                                System.out.println("Server: Handling a client in " + Thread.currentThread().getName() +
                                        " Thread");
                                try {
                                    this.requestHandler.handleClient(clientToServerConnection.getInputStream()
                                            ,clientToServerConnection.getOutputStream());
                                } catch (IOException | ClassNotFoundException ioException) {
                                    ioException.printStackTrace();
                                }
                                // we stopped handling the specific client
                                try {
                                    /*
                                     clientToServerConnection.getOutputStream().close();
                                     clientToServerConnection.getInputStream().close();
                                    */
                                    clientToServerConnection.close();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            };
                            clientsPool.execute(specificClientHandling);
                        }catch (IOException e){

                        }
                    };
                    clientsConnect.execute(connectClient);
                }
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        };
        new Thread(clientHandling).start();
    }

    public void stop(){
        if (!stopServer){
            stopServer = true;
            if (clientsPool!=null)
                clientsPool.shutdown();
        }

    }

    public static void main(String[] args) {
        InternetServer server = new InternetServer(8010);
        server.supportClients(new MatrixHandler());
    }


}
