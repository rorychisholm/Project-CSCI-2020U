package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    static int port = 8080; // Port Number
    static String hostName = "127.0.0.1"; // Return host address
    private BorderPane layout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Server Selection");
        boolean runServer;

        GridPane serverSelection = new GridPane();
        serverSelection.setPadding(new Insets(10, 10, 10, 10));
        serverSelection.setVgap(10);
        serverSelection.setHgap(10);

        Button runButton = new Button("Run Server");
        runButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e) {
            }
        });
        serverSelection.add(runButton, 1, 4);

        Button noRunButton = new Button("Don't Run Server");
        noRunButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e) {
            }
        });
        serverSelection.add(noRunButton, 1, 5);

        layout = new BorderPane();
        layout.setCenter(serverSelection);
        Scene selectionScene = new Scene(layout, 200, 200);
        primaryStage.setScene(selectionScene);
        primaryStage.show();

        int numberOfClients = 2; // Number of Clients(changing the number runs more clients at once)

        ClientConnectionServer server = new ClientConnectionServer(port); // Initializes Server Thread
        server.start(); // Runs Server Thread

        Stage[] clientStages = new Stage[numberOfClients]; // Make stage for as many clients as are running
        ClientConnection clientClass[] = new ClientConnection[numberOfClients]; // Makes array of Client Threads
        Scene scene[] = new Scene[numberOfClients]; // Makes array of scenes

        for (int i = 0; i < numberOfClients; i++) {
            clientStages[i] = new Stage(); // Makes new stage
            clientClass[i] = new ClientConnection(port, hostName, clientStages[i], i + 1); // Initializes client
            clientClass[i].start(); // Starts client thread
            clientClass[i].join(); // Waits until layout has been set for that thread before creating the scenes
            scene[i] = new Scene(clientClass[i].getLayout(), 600, 600);
            clientStages[i].setScene(scene[i]);
            clientStages[i].show();
            clientStages[i].setOnCloseRequest(e -> { // Checks that all clients are closed before close the server thread
                boolean closeFlag = true;
                for (int j = 0; j < numberOfClients; j++) {
                    if (clientClass[j].isAlive()) {
                        closeFlag = false;
                    }
                }
                if (closeFlag == true) {
                    server.quit(); // Closes server thread
                }
            });
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
