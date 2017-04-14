package sample;

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
            while (request == null){
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
        /*y {
            File serverLog = new File("Logs", "ServerLogs.txt"); // Makes overall Log file for server
            if (!serverLog.exists()) { // Overwrites files
                serverLog.createNewFile();
            }
            String fileName;
            if(cmdParts[3].contains(".")){
                fileName = cmdParts[3].substring(0, cmdParts[3].lastIndexOf('.')) + "-FileLog.txt";
            }else{
                fileName = cmdParts[3] + "-FileLog.txt";
            }
            File logFile = new File("Logs", fileName);
            if (!logFile.exists()){ // Overwrites files
                logFile.createNewFile();
            }
            FileWriter fo = new FileWriter(logFile,true);
            fout.println();

            out.print(toSend);
        }catch(IOException e){

        }*/
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
