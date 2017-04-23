package sample;
/*

William Rory Chisholm, Jeremy Chong

-Text editor automatically opens up the TextFile.txt; clients are able to recieve a file from the Server Storage, and send data to javafx ui.
-By typing text into the editor, and hitting "Update", what is in the text area will be saved.
-This will also generate a change log, called TextFile-FileLog.
-The files can be found in the ServerStorage folder.
-If the files do not exist, they are created.
-The change log tracks the key and position via carat positions as well as what was added and deleted; something like "add 'abc' to position '3'.
-Deletion is somewhat buggy - must be deleted with backspace, and struggles with deleting lines.
-Intended functionality was to synchronize multiple clients by constantly sending over the changelog.
-Changelog constantly updates whenever the textfile does, and by sending locations as well as changes, they can be made.
-by doing so, changes could be implemented in real time via the timer.


*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    static int port = 8080; // Port Number
    static String hostName = "127.0.0.1"; // Return host address
    int closingFlags;

    @Override
    public void start(Stage primaryStage) throws Exception

    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        int numberOfClients = 1; // Number of Clients(changing the number runs more clients at once)
        closingFlags = 0;
        ClientConnectionServer server = new ClientConnectionServer(port); // Initializes Server Thread
        server.start(); // Runs Server Thread

        Stage[] clientStages = new Stage[numberOfClients]; // Make stage for as many clients as are running
        ClientConnection clientClass[] = new ClientConnection[numberOfClients]; // Makes array of Client Threads
        Scene scene[] = new Scene[numberOfClients]; // Makes array of scenes

        for (int i = 0; i < numberOfClients; i++)
        {
            clientStages[i] = new Stage(); // Makes new stage
            clientClass[i] = new ClientConnection(port, hostName, clientStages[i], i + 1); // Initializes client
            clientClass[i].start(); // Starts client thread
            clientClass[i].join(); // Waits until layout has been set for that thread before creating the scenes
            scene[i] = new Scene(clientClass[i].getLayout(), 600, 600);
            clientStages[i].setScene(scene[i]);
            clientStages[i].show();
            // Checks that all clients are closed before close the server thread
            clientStages[i].setOnCloseRequest(e -> {
                closingFlags++;
                for (int j = 0; j < numberOfClients; j++) {
                    if((clientClass[j].getLayout()) == (((Stage)e.getSource()).getScene().getRoot())){
                        clientClass[j].cancelTimer();
                        //System.out.println("Client: " + clientClass[j].getClientNum());
                    }
                }
                if (closingFlags >= numberOfClients) {
                    server.quit(); // Closes server thread
                }
            });
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
