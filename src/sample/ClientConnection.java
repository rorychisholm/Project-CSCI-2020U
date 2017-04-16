package sample;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.err;

/**
 * Created by 100560820 on 3/28/2017.
 */
public class ClientConnection extends Thread {
    private Socket socket;
    private BorderPane layout;
    private Stage primaryStage; // Stage being used
    private int port, cNum; // Port number and number of clients
    private String hostName, fileOpen;
    private TextArea textArea;
    private Timer timer;
    private Vector<KeyCode> keyTracker;
    private Vector<Integer> caretTracker;
    private Vector<Boolean> shiftTracker;


    public ClientConnection(int port, String hostName, Stage stage, int cNum) {
        this.port = port;
        this.hostName = hostName;
        this.primaryStage = stage;
        this.cNum = cNum;
        this.timer = new Timer();
        this.fileOpen = "TextFile.txt";
        this.keyTracker = new Vector();
        this.caretTracker = new Vector();
        this.shiftTracker = new Vector();
    }

    @Override
    public void run() {
        primaryStage.setTitle("Multi-person Text Editor - Client Number: " + cNum);
        ////////////////////////////////////////MENU BAR////////////////////////////////////////
        Menu fileMenu = new Menu("File");
        // NEW MENU ITEM
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newMenuItem.setOnAction(e -> clear());
        fileMenu.getItems().add(newMenuItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        // OPEN MENU ITEM
        MenuItem openMenuItem = new MenuItem("Open...");
        fileMenu.getItems().add(openMenuItem);
        openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        //openMenuItem.setOnAction(e -> openFile(primaryStage));
        fileMenu.getItems().add(new SeparatorMenuItem());
        // SAVE MENU ITEM
        MenuItem saveMenuItem = new MenuItem("Local Save");
        fileMenu.getItems().add(saveMenuItem);
        saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        //saveMenuItem.setOnAction(e -> saveFile());
        // SAVE AS MENU ITEM
        MenuItem saveAsMenuItem = new MenuItem("Local Save As...");
        fileMenu.getItems().add(saveAsMenuItem);
        //saveAsMenuItem.setOnAction(e -> saveAs(primaryStage));
        fileMenu.getItems().add(new SeparatorMenuItem());
        // EXIT MENU ITEM
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitMenuItem);
        exitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exitMenuItem.setOnAction(e -> ((Stage) textArea.getScene().getWindow()).close());

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        ////////////////////////////////////////END OF MENU BAR////////////////////////////////////////

        //Text Area
        textArea = new TextArea();

        ////////////////////////////////////////BUTTONS////////////////////////////////////////
        GridPane editArea = new GridPane();
        // ADD BUTTON
        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                codeCreater(keyTracker, caretTracker, shiftTracker);
            }
        });
        editArea.add(uploadButton, 0, 0);

        // DOWNLOAD BUTTON // Runs downloadFileCmd
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                cancelTimer();
            }
        });
        editArea.add(downloadButton, 1, 0);

        // UPDATE BUTTON // Manual update button, runs updateList function
        Button updateButton = new Button("Update");
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //updateList(clientList, serverList);
                Vector<String> temp = getUpdateCmd();
                /*
                for (int i = 0; i < temp.size(); i++) {
                    textArea.appendText(temp.get(i));
                }
                */
            }
        });
        editArea.add(updateButton, 2, 0);
        ////////////////////////////////////////END OF BUTTONS////////////////////////////////////////

        //Here the handler
        textArea.setOnKeyPressed((event) -> {
            if (event.getCode().isDigitKey() || event.getCode().isLetterKey() || event.getCode().isWhitespaceKey()) {
                System.out.println(textArea.getCaretPosition() + ", " + event.getCode().getName() + ", " + event.isShiftDown());
                caretTracker.add(textArea.getCaretPosition());
                shiftTracker.add(event.isShiftDown());
                keyTracker.add(event.getCode());
            }

        });
        layout = new BorderPane(); // sets layout
        layout.setTop(menuBar);
        layout.setCenter(textArea);
        layout.setBottom(editArea);
        //timerStart();
    }

    private void timerStart() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Vector<String> temp = getUpdateCmd();
                if (temp != null) {
                    for (int i = 0; i < temp.size(); i++) {
                        textArea.appendText(temp.get(i) + "\n");
                    }
                }
            }
        }, 0, 1000);
    }

    ////////////////////////////////////////MENU BAR FUNCTIONS////////////////////////////////////////
    public void clear() {
        textArea.setText("");
    }

    public void openFile(Stage stage) {
        /*try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            currentFilename = fileChooser.showOpenDialog(stage);
            //System.out.println(currentFilename.getName());
            load();
        } catch (NullPointerException ne) {
            System.out.println("No Selected File");
            //ne.printStackTrace();
        }*/
    }

    public void load() {
        /*try {
            BufferedReader in = new BufferedReader(new FileReader(currentFilename));// stores files
            ObservableList<StudentRecord> tempList = FXCollections.observableArrayList();
            String line; // reads file line by line
            while ((line = in.readLine()) != null) {
                String[] inText = line.split(","); // splits the string on the "," store in array of strings
                StudentRecord temp = new StudentRecord(inText[0], Double.parseDouble(inText[1]), Double.parseDouble(inText[2]), Double.parseDouble(inText[3]));
                tempList.add(temp);
            }
            table.setItems(tempList);
        } catch (IOException e) {
        }*/
    }

    public void saveFile() {
        /*try {
            if (currentFilename == null) {
                currentFilename = new File("StudentData.csv");
                currentFilename.createNewFile();
            }
            if (!currentFilename.exists() || currentFilename.canWrite()) { // if the out file given exists and is write-able
                PrintWriter fout = new PrintWriter(currentFilename); // new PrintWriter
                //Set<String> keys = wordCounts.keySet(); // makes a list of all the keys (words)
                Iterator<StudentRecord> tableIterator = table.getItems().iterator(); // makes an iteration to  keep track of location in the keys
                while (tableIterator.hasNext()) { // while the iterator can find a next word continue
                    StudentRecord key = tableIterator.next(); // sets to next word
                    //Student ID, Assignments, Midterm, Final Exam, Final Mark, Letter Grade
                    fout.println(key.getStudentID() + "," + key.getAssignment() + "," + key.getMidterm() + "," + key.getFinalExam()); //+ "," + key.getFinalMark() + "," + key.getLetterGrade()
                }
                fout.close(); //closes PrintWriter
            }
        } catch (IOException e) {
            System.out.println("IO Exception Thrown");
            //e.printStackTrace();
        }*/
    }

    public void saveAs(Stage stage) {
        /*try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            currentFilename = fileChooser.showSaveDialog(stage);
            saveFile();
        } catch (NullPointerException ne) {
            System.out.println("No Selected File");
        }*/
    }

    ////////////////////////////////////////COMMAND FUNCTIONS////////////////////////////////////////
    private synchronized Vector<String> getUpdateCmd() {
        try {
            socket = new Socket(hostName, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
            Date date = new Date();
            // Command Format: "CMD,DATE,CLIENTNUM,OPENFILENAME"
            String cmd = "UPDATE";
            cmd += "," + dateFormat.format(date);
            cmd += "," + cNum;
            cmd += "," + fileOpen;
            // Initializes sockets and in and out streams
            out.println(cmd); // Sends command
            out.flush(); // Flushes Printwriter


            String response;
            Vector<String> stringList = new Vector<>(); //Flexible Array
            if ((response = in.readLine()).equalsIgnoreCase("Found")) {
                //Gets code from vectors for key inputs, and send it to server
                out.println(codeCreater(keyTracker, caretTracker, shiftTracker));
                out.flush();
                /*while ((response = in.readLine()) != null) { // Reads response line by line
                    stringList.add(response);
                }*/
            }


            // Closes the connection
            socket.close();
            return stringList; // returns List
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOE " + cNum + ": " + err);
        }
        return null; // returns null if not
    }
    /*
    codeCreator ~Rory
    ---Done---
    - Makes added words into a Code
    ---To Do---
    - Needs Deletion Functionality
        - Backspaces only work if over written
        - Cannot Delete Text
    - Needs text shifting functionality
     */
    private String codeCreater(Vector<KeyCode> keys, Vector<Integer> caret, Vector<Boolean> shiftDown) {
        Map<Integer, String> map = new TreeMap<>();
        Vector<String> addText = new Vector<>();
        String temp, code;
        int max = 0, index = 0, tempI = 0;
        ;
        for (int i = 0; i < caret.size(); i++) {
            if (!shiftDown.get(i)) {
                temp = keys.get(i).impl_getChar().toLowerCase();
            } else {
                temp = keys.get(i).impl_getChar();
            }
            map.put(caret.get(i), temp);
            if (caret.get(i) > max) {
                max = caret.get(i);
            }
        }
        temp = "";
        for (int i = 0; i <= max; i++) {
            if (map.get(i) != null) {
                temp += map.get(i);
                //System.out.print(map.get(i));
                if (i == max) {
                    addText.add(tempI + "->" + (i) + ":\"" + temp + "\"");
                    //System.out.println(tempI + "->" + (i)+":\""+temp+"\"");
                    temp = "";
                }
            } else if ((i > 0) && (map.get(i - 1) != null)) {
                addText.add(tempI + "->" + (i) + ":\"" + temp + "\"");
                //System.out.println(tempI + "->" + (i)+":\""+temp+"\"");
                temp = "";
            }
            if ((i > 0) && (map.get(i - 1) == null)) {
                tempI = i;
            }
        }
        code = "ADD:[";
        for (int i = 0; i < addText.size(); i++) {
            code += addText.get(i);
            if (i < addText.size() - 1) {
                code += "|";
            }
        }
        code += "]";
        keyTracker.clear();
        caretTracker.clear();
        shiftTracker.clear();
        System.out.println(code);
        return code;
    }

    public synchronized void cancelTimer() {
        timer.cancel();
        timer.purge();
    }

    public BorderPane getLayout() { // Returns layout value used for scene
        return this.layout;
    }
}

    /*
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
        // Updates observable list and calls listFiles and getUpdateCmd
        System.out.println("Updating...");
        observClieList = listFiles();
        //observServList = getUpdateCmd();
        clientList.setItems(observClieList);
        serverList.setItems(observServList);
        System.out.println(" ...Done");
    }
    */

