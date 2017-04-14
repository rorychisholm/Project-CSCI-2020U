package sample;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by 100560820 on 3/27/2017.
 */

// "127.0.0.1 is the loopback Internet protocol (IP) address also referred to as the localhost."
public class ClientConnectionHandler implements Runnable {
    public static String ROOT = "ServerStorage"; // Root for server storage
    private Socket socket;
    private PrintWriter out;

    public ClientConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // opens streams
            InputStream is = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            OutputStream os = socket.getOutputStream();
            out = new PrintWriter(os);
            // Waits for command
            String request = null;
            while (request == null) {
                request = in.readLine();
            }
            //Sorts command into array
            String[] requestParts = request.split(",");// CMD Uri

            // Deals with command
            String command = requestParts[0]; // Command Format: "CMD,DATE,CLIENTNUM,OPENFILENAME"
            if (command.equalsIgnoreCase("UPDATE")){
                cmdUpdate(requestParts);
            }else if (command.equalsIgnoreCase("UPLOAD")) {
                cmdUPLOAD(requestParts[1]);
            } else if (command.equalsIgnoreCase("DOWNLOAD")) {
                cmdDOWNLOAD(requestParts[1]);
            } else {
                System.out.println("CMD not found.");
            }
            socket.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
<<<<<<< HEAD
    private void cmdDIR() throws IOException
    { // Handles DIR command, sends list of files in a string
        String toSend = "", line = "";
        File file = new File(ROOT, "RoomieShoppingList.txt");
        BufferedReader in = new BufferedReader(new FileReader(file));
        String newLineChar = System.getProperty("line.separator");

            while ((line = in.readLine()) != null)
            {
                toSend += line;
                toSend += newLineChar;
            }
            out2.print(toSend);
            out2.print(newLineChar);
            out2.flush();
=======

    private void cmdUpdate(String[] cmdParts) { // Handles DIR command, sends list of files in a string
        try {
            String toSend = "";
            File file = new File(ROOT);//, cmdParts[3]
            if (!file.exists()) { // Overwrites files
                file.createNewFile();
            }
            File fileList[] = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                toSend += fileList[i].getName();
                if (i != (fileList.length - 1)) {
                    toSend += " ";
                }
            }
            out.print(toSend);
            out.flush();
        } catch (IOException e) {

        }
    }

    private void updateLogs(String[] cmdParts, String logMessage){
        /*File serverLog = new File("Logs", "serverLogs.txt");
        if (!serverLog.exists()) { // Overwrites files
            serverLog.createNewFile();
        }
        File logFile = new File("Logs", cmdParts[3]);
        if (!logFile.exists()) { // Overwrites files
            logFile.createNewFile();
        }
        String toSend = "", line = "";
        BufferedReader in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
            toSend += line;
            toSend += "\n";
        }
        out.print(toSend);*/
>>>>>>> bac8ea78b2b20a61db15726db1efb2afdd0909ff
    }

    private void cmdUPLOAD(String fileName) throws IOException { // Handles UPLOAD command
        try {
            // open streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            // read saves the response
            String response;
            File newFile = new File("ServerStorage", fileName);
            if (!newFile.exists()) { // Overwrites files
                newFile.createNewFile();
            } else {
                newFile.delete();
                newFile.createNewFile();
            }
            PrintWriter fout = new PrintWriter(newFile);
            while ((response = in.readLine()) != null) {
                fout.println(response);
            }
            fout.close();
            // close the connection
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void cmdDOWNLOAD(String fileName) throws IOException { // Handles DOWNLOAD command, sends file as string
        String toSend = "", line = "";
        File file = new File(ROOT, fileName);
        BufferedReader in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
            toSend += line;
            toSend += "\n";
        }
        out.print(toSend);
        out.flush();
    }
}
