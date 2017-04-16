package sample;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by 100560820 on 3/27/2017.
 */

// "127.0.0.1 is the loopback Internet protocol (IP) address also referred to as the localhost."
public class ClientConnectionHandler implements Runnable {
    public static String ROOT = "ServerStorage"; // Root for server storage
    private Socket socket;
    private PrintWriter out;
    BufferedReader sin;

    public ClientConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // opens streams
            sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream os = socket.getOutputStream();
            out = new PrintWriter(os);
            // Waits for command
            String request = null;
            while (request == null) {
                request = sin.readLine();
            }
            //Sorts command into array
            String[] requestParts = request.split(",");// CMD Uri

            // Deals with command
            String command = requestParts[0]; // Command Format: "CMD,DATE,CLIENTNUM,OPENFILENAME"
            if (command.equalsIgnoreCase("UPDATE")) {
                cmdUpdate(requestParts);
            } else if (command.equalsIgnoreCase("UPLOAD")) {
                // cmdUPLOAD(requestParts[1]);
            } else if (command.equalsIgnoreCase("DOWNLOAD")) {
                // cmdDOWNLOAD(requestParts[1]);
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
            String toSend = "test", fileChanges, lineF = "";
            File file = new File(ROOT, cmdParts[3]);
            if (!file.exists()) { // Overwrites files
                file.createNewFile();
            }
            BufferedReader fin = new BufferedReader(new FileReader(file));
            String newLineChar = System.getProperty("line.separator");
            out.print("Found" + newLineChar);
            out.flush();

            fileChanges = sin.readLine();
            updateLogs(cmdParts, fileChanges);

            // Be careful of whats inputted to the code doesn't mix up with the message
            String add = fileChanges.substring(fileChanges.indexOf("ADD:[") + 5, fileChanges.indexOf("]", fileChanges.indexOf("ADD:[")));
            while (add.contains("\"|")) {
                System.out.println("Adding: " + add.substring(0, add.indexOf("\"|")+1));
                addToFile(file, add.substring(0, add.indexOf("\"|")+1));

                add = add.substring(add.indexOf("\"|")+2 , add.length());
            }
            System.out.println("Adding: " + add);
            addToFile(file, add);

            /*
            while (lineF = fin.readLine()) != null){ // Edit to file
            }
            */
            fin.close();
            out.print(toSend);
            out.flush();
        } catch (IOException e) {
        }
    }

    private void updateLogs(String[] cmdParts, String logMessage) {
        try {
            //System.out.println("Testing");
            File serverLog = new File(ROOT + "/Logs", "ServerLogs.txt"); // Makes overall Log file for server
            // if the file doesn't exist, make it
            if (!serverLog.exists()) {
                serverLog.createNewFile();
            }
            String fileName;
            // Adds "-FileLog.txt" to file name
            if (cmdParts[3].contains(".")) {
                fileName = cmdParts[3].substring(0, cmdParts[3].lastIndexOf('.')) + "-FileLog.txt";
            } else {
                fileName = cmdParts[3] + "-FileLog.txt";
            }

            File logFile = new File(ROOT + "/Logs", fileName); // Personal File Log
            // if the file doesn't exist, make it
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            String message = "";
            for (int i = 0; i < cmdParts.length; i++) {
                message += cmdParts[i] + "_";
            }
            message += "LogMessage: " + logMessage;
            FileWriter fSout = new FileWriter(serverLog, true);
            FileWriter fout = new FileWriter(logFile, true);
            fSout.write(message + System.getProperty("line.separator"));
            fSout.close();
            fout.write(message + System.getProperty("line.separator"));
            fout.close();
        } catch (IOException e) {
        }
    }

    public void addToFile(File file, String addCode) throws IOException {
        if (!addCode.isEmpty()){
            System.out.println("Caret: " + addCode.substring(0,addCode.indexOf("-")));
            int caret = Integer.parseInt(addCode.substring(0,addCode.indexOf("-"))),caretIndex = 0;
            String message = addCode.substring(addCode.indexOf(":\"") + 2, addCode.length() - 1), line;
            Vector<String> newLines = new Vector<>();
            System.out.println("Message: " + message);
            BufferedReader in = new BufferedReader(new FileReader(file));
                while((line = in.readLine()) != null){
                    System.out.println("Caret: "+caret+", Index: "+caretIndex+", Length: "+(caretIndex+line.length()));
                    if((caretIndex <= caret) && (caret <= caretIndex+line.length())){
                        System.out.println("line: " + line);
                        line = line.substring(0, caret-caretIndex) + message + line.substring(caret-caretIndex, line.length());
                        System.out.println("new line: " + line);
                    }
                    newLines.add(line);
                    caretIndex += line.length()+1;
                }
                if (caretIndex <= caret){
                    newLines.add(message);
            }
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < newLines.size(); i++) {
                newLines.set(i,newLines.get(i).replace("\\n\\",System.getProperty("line.separator")));
                System.out.println("newLine["+i+"]: " + newLines.get(i));
                writer.write(newLines.get(i)+System.getProperty("line.separator"));
                writer.flush();
            }
            writer.close();
        }
    }

        /*
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
    }*/
}
