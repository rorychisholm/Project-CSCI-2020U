package sample;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Vector;


/**
 * Created by 100560820 on 3/28/2017.
 */
public class ClientConnection extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BorderPane layout;
    private Stage primaryStage; // stage being used
    private ObservableList<String> observServList, observClieList; // List for storing file names int he storage
    private int port, cNum;// port number and number of clients
    private String hostName;
    private String clientStorageRoot;// root file for client storage

    public ClientConnection(int port, String hostName, Stage stage, int cNum) {
        this.port = port;
        this.hostName = hostName;
        this.primaryStage = stage;
        this.cNum = cNum;
        this.clientStorageRoot = "clientStorage";
    }

    @Override
    public void run() {
        primaryStage.setTitle("Assignment02 - Client Number: " + cNum);
        //menu
        MenuBar bar = new MenuBar();//bar to hold menus

        Menu fileMenu = new Menu("File"); //make File menu

        bar.getMenus().add(fileMenu); // add to bar


        //Text Area
        TextArea textArea = new TextArea();

        ListView<String> clientList = new ListView<String>(); // ListView for client storage
        clientList.setEditable(true);

        ListView<String> serverList = new ListView<String>(); // ListView for server storage
        serverList.setEditable(true);

        ////////////////////////////////////////BUTTONS////////////////////////////////////////
        GridPane editArea = new GridPane();

        // ADD BUTTON
        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (clientList.getEditingIndex() != -1) {// returns index of selected list value and if its not nothing
                    System.out.println("Uploading...");
                    uploadFileCmd(observClieList.get(clientList.getEditingIndex()));
                    System.out.println(" ...Done Uploading");
                }
                updateList(clientList, serverList);
            }
        });
        editArea.add(uploadButton, 0, 0);

        // DOWNLOAD BUTTON // Runs downloadFileCmd
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //System.out.println("Editing Index: " + serverList.getEditingIndex());
                if (serverList.getEditingIndex() != -1) { // returns index of selected list value and if its not nothing
                    System.out.println("Downloading...");
                    downloadFileCmd(observServList.get(serverList.getEditingIndex()));
                    System.out.println(" ...Done Downloading");
                }
                updateList(clientList, serverList);
            }
        });
        editArea.add(downloadButton, 1, 0);

        // UPDATE BUTTON // Manual update button, runs updateList function
        Button updateButton = new Button("Update");
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //updateList(clientList, serverList);
                Vector<String> temp = sendDIRCmd();
                for (int i = 0; i < temp.size();i++){
                    textArea.appendText(temp.get(i));
                }
            }
        });
        editArea.add(updateButton, 2, 0);
        ////////////////////////////////////////END OF BUTTONS////////////////////////////////////////

        updateList(clientList, serverList); // Calls update list upon loading
        SplitPane fileView = new SplitPane(clientList, serverList); // Makes SplitPane dividing the 2 list views
        fileView.setDividerPositions(0.50);

        layout = new BorderPane(); // sets layout
        layout.setTop(bar);
        layout.setBottom(editArea);
        //layout.setCenter(fileView);
        layout.setCenter(textArea);
    }

    public synchronized Vector<String> sendDIRCmd() { //sends DIR command, receives list of files in server storage
        try {
            // Initializes sockets and in and out streams
            Vector<String> stringList = new Vector<>(); //Flexible Array
            Socket socket = new Socket(hostName, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("DIR"); // Sends command
            out.flush(); // Flushes printwriter
            String response; //
            while ((response = in.readLine()) != null){// Reads response line by line
                stringList.add(response);
            }
            // Closes the connection
            out.close();
            in.close();
            socket.close();
            return stringList; // returns List
        }catch (IOException e) {}
        return null; // returns null if not
    }

    public void uploadFileCmd(String fileName) { //sends UPLOAD command
        try {
            // Initializes sockets and in and out streams
            socket = new Socket(hostName, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            // Sends request
            out.println("UPLOAD " + "/" + fileName);
            out.flush();

            String toSend = "", line;
            File file = new File(clientStorageRoot, fileName); //Local Opens file
            BufferedReader fin = new BufferedReader(new FileReader(file));// reads local file
            while ((line = fin.readLine()) != null) {
                toSend += line;
                toSend += "\n";
            }
            // Sends local file as String
            out.print(toSend);
            out.flush();
            // Closes streams
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {}
    }

    public  void downloadFileCmd(String fileName) { //sends DOWNLOAD command
        try {
            // Initializes sockets and in and out streams
            Socket socket = new Socket(hostName, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            // sends command
            out.println("DOWNLOAD " + "/" + fileName);
            out.flush();
            // read the response
            String response;
            File newFile = new File("ClientStorage", fileName);
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
        } catch (IOException e) {}
    }

    public ObservableList<String> listFiles() { // Lists files on local side returns the observable list
        File clientStorage = new File("ClientStorage");
        if (!clientStorage.isDirectory()) { // Makes storage if there Isn't one
            clientStorage.mkdir();
        }
        ObservableList<String> tempList = FXCollections.observableArrayList();
        File fileList[] = clientStorage.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            tempList.add(fileList[i].getName());
        }
        return tempList;
    }

    public void updateList(ListView<String> clientList, ListView<String> serverList) {
        // Updates observable list and calls listFiles and sendDIRCmd
        System.out.println("Updating...");
        observClieList = listFiles();
        //observServList = sendDIRCmd();
        clientList.setItems(observClieList);
        serverList.setItems(observServList);
        System.out.println(" ...Done");
    }

    public BorderPane getLayout() { // Returns layout value used for scene
        return this.layout;
    }
}
