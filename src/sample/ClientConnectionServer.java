package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by 100560820 on 3/27/2017.
 */
public class ClientConnectionServer extends Thread{
    private ServerSocket serverSocket;
    private int port;

    public ClientConnectionServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public void handleRequests() throws IOException {
        try {
            int i = 0;
            Vector<Thread> handlerThread = new Vector<Thread>(); // Vector of threads to make it Multithreading
            while (!serverSocket.isClosed()) {
                System.out.println("ClientConnectionServer listening on port " + port); // Displays waiting text
                Socket socket = serverSocket.accept(); // Waits for clients to get sockets
                System.out.println("Client Found..."); // When client is connected
                handlerThread.add(i, new Thread(new ClientConnectionHandler(socket))); // makes new thread to handle it
                handlerThread.get(i).start(); // starts new thread
                i++;
            }
        } catch (IOException e) {}
    }

    public void quit() { // Helps let the thread close
        try {
            serverSocket.close();
            //System.out.println("Socket Closing");
        } catch (IOException e) {
            //System.out.println("Socket Closed");
        }
    }

    @Override
    public void run() {
        try {
            this.handleRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
