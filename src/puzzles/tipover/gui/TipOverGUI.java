package puzzles.tipover.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.tipover.model.TipOverModel;
import util.Coordinates;
import util.Grid;
import util.Observer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * The TipOverGUI application is the UI for TipOver.
 *
 * @author Miguel Reyes
 * November 2021
 */
public class TipOverGUI extends Application
        implements Observer<TipOverModel, Object> {

    /**
     * GridPane used for the games board
     */
    private final GridPane gpane = new GridPane();
    /**
     * Backgrounds to highlight our tipper on the board.
     */
    private final Background tipperBackground = new Background(new BackgroundFill(Color.SALMON,
            CornerRadii.EMPTY, null));
    /**
     * Backgrounds to highlight our goalCrate on the board.
     */
    private final Background goalBackground = new Background(new BackgroundFill(Color.RED,
            CornerRadii.EMPTY, null));
    /**
     * Arrow images used as the display of our buttons that move the tipper
     */
    private final Image upArrow = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/up-arrow.png")));
    private final Image downArrow = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/down-arrow.png")));
    private final Image leftArrow = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/left-arrow.png")));
    private final Image rightArrow = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/right-arrow.png")));
    /**
     * The model for the view and controller.
     */
    private TipOverModel model;
    /**
     * Used to hold our buttons that will move the tipper
     */
    private BorderPane allArrows;
    /**
     * Used to hold the buttons associated with functions load, reload, and hint
     */
    private VBox menu;
    /**
     * Label that is used to update the player on the status of their move or loading files
     */
    private Label topLabel;
    /**
     * Stage that points to the main stage and allows us to resize the window everytime the GridPane(board) changes size.
     */
    private Stage stage;

    /**
     * main entry point launches the JavaFX GUI.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Helper function used by our Controller(controller). This will use File chooser for the user to select the file
     * they want to load in and start a new game.
     *
     * @throws Exception - In case of any file error. Not likely because the user will be prompted to select from the
     *                   correct directory.
     */
    public void chooseFile() throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Data");
        // We want the user to select files from our tipover directory which is a subdirectory of our data directory
        chooser.setInitialDirectory(new File("data/tipover"));
        File data = chooser.showOpenDialog(new Stage());

        // data will return null if the user ends up not selecting a file and instead exits the window. Therefore, we
        // just want to resume with the current game
        if (data != null) {
            this.model.load(data.toString());
            this.topLabel.setText("New file loaded.");
        }
    }


    // CONTROLLER

    /**
     * Helper function used by our Controller(controller) to reload the current game and start fresh.
     */
    public void reload() {
        this.model.reload();
        this.topLabel.setText("New file loaded.");
    }


    // VIEW

    /**
     * The display for our buttons as well as functionality. This is essentially our controller.
     */
    public void controller() {

        this.allArrows = new BorderPane();

        //Button not used - just for spacing
        Button centerSpace = new Button();
        centerSpace.setMinSize(45, 25);
        centerSpace.setDisable(true);
        centerSpace.setVisible(false);
        this.allArrows.setCenter(centerSpace);

        // Button to move one space in the north direction
        Button upArrow = new Button();
        upArrow.setGraphic(new ImageView(this.upArrow));
        upArrow.setOnAction(event -> this.model.move("NORTH"));
        this.allArrows.setTop(upArrow);
        BorderPane.setAlignment(upArrow, Pos.CENTER);

        // Button to move one space in the south direction
        Button downArrow = new Button();
        downArrow.setGraphic(new ImageView(this.downArrow));
        downArrow.setOnAction(event -> this.model.move("SOUTH"));
        this.allArrows.setBottom(downArrow);
        BorderPane.setAlignment(downArrow, Pos.CENTER);

        // Button to move one space in the west direction
        Button leftArrow = new Button();
        leftArrow.setGraphic(new ImageView(this.leftArrow));
        leftArrow.setOnAction(event -> this.model.move("WEST"));
        this.allArrows.setLeft(leftArrow);

        // Button to move one space in the east direction
        Button rightArrow = new Button();
        rightArrow.setGraphic(new ImageView(this.rightArrow));
        rightArrow.setOnAction(event -> this.model.move("EAST"));
        this.allArrows.setRight(rightArrow);


        this.menu = new VBox(5);
        this.menu.setAlignment(Pos.CENTER);
        // Button to load in a new file
        Button load = new Button("Load");
        load.setFont(new Font(20));
        load.setOnAction(event -> {
            try {
                this.chooseFile();
            } catch (Exception e) {
                this.topLabel.setText("Error: Could not load file. Try again.");
            }
        });
        // Button to reload the current game to its original start
        Button reload = new Button("Reload");
        reload.setFont(new Font(20));
        reload.setOnAction(event -> reload());

        // Button that will move the player in the correct direction one move at a time
        Button hint = new Button("Hint");
        hint.setFont(new Font(20));
        hint.setOnAction(event -> this.model.hint());
        this.menu.getChildren().addAll(load, reload, hint);

    }

    /**
     * Function that builds the main GridPane - used for our board.
     */
    public void gridDisplay() {
        int rowSize = this.model.getCurrentConfig().getBoard().getNRows();
        int colSize = this.model.getCurrentConfig().getBoard().getNCols();
        for (int row = 0; row < rowSize; ++row) {
            for (int col = 0; col < colSize; ++col) {
                // currentPos is used to check if we're on our goal or tipper position and if so we want to highlight
                // those labels.
                Coordinates currentPos = new Coordinates(row, col);
                Label piece = new Label(this.model.getCurrentConfig().getBoard().get(row, col));
                piece.setFont(new Font(35));
                if (currentPos.equals(this.model.getCurrentConfig().getTippersPos())) {
                    piece.setBackground(tipperBackground);
                }
                if (currentPos.equals(this.model.getCurrentConfig().getGoalCratePos())) {
                    piece.setBackground(goalBackground);
                }
                this.gpane.add(piece, col, row);
            }
        }
    }

    @Override
    public void start(Stage stage) {
        // A reference to our gui stage this allows for updating the windows size (Dynamic window)
        this.stage = stage;

        //Main border pane that holds the main GridPane(gpane), the top label(topLabel), and all buttons.
        BorderPane bmainPane = new BorderPane();

        //The label that displays the players' selection whether it's a move or loading/reloading a file
        this.topLabel = new Label("New file loaded.");
        this.topLabel.setFont(new Font(20));
        bmainPane.setTop(this.topLabel);

        //Initialize our board
        gridDisplay();
        bmainPane.setLeft(gpane);

        //Initialize our direction buttons as well as load, reload, and buttons
        controller();
        VBox functionalityBox = new VBox(50);
        functionalityBox.getChildren().addAll(this.allArrows, this.menu);
        bmainPane.setRight(functionalityBox);

        // Setting the main stage
        stage.setScene(new Scene(bmainPane));
        stage.setTitle("TipOver");
        stage.show();

    }

    /**
     * This function is called by the update function to update our GridPane that contains our current board.
     */
    public void updateBoard() {
        // Clears the current content in the GridPane so it is empty
        this.gpane.getChildren().clear();
        // Refills our Grid Pane using the updated configuration
        gridDisplay();
    }

    @Override
    public void update(TipOverModel tipOverModel, Object o) {
        // Handles all moves - will get overridden if dealing with hint calls or towers be tipped over.
        if (this.model.getResultMove()) {
            this.topLabel.setText("No crate or tower there.");
        } else {
            if (this.model.getCurrentConfig().isSolution()) {
                this.topLabel.setText("You Won!!");
            } else {
                this.topLabel.setText("");
            }
        }

        // Updates the label to display if the solver has reached the winning goal or the current configuration is
        // unsolvable
        if (o != null && o.equals("hint")) {
            if (this.model.getResultHint()) {
                this.topLabel.setText("Unsolvable board.");
            } else {
                this.topLabel.setText("");
            }
            if (this.model.getCurrentConfig().isSolution()) {
                this.topLabel.setText("I Won!!");
            }
        }

        // Will change label to display a tower has been tipped - works for both the user's and the solver's move
        if (o != null && o.equals("tower")) {
            this.topLabel.setText("A tower has been tipped over.");
        }

        // Update our board
        updateBoard();

        //Will resize the window if need be - the board will change size depending on the current configuration
        this.stage.sizeToScene();
    }

    @Override
    public void init() {
        // Read in the starting file and create our model
        try (BufferedReader inputReader = new BufferedReader(new FileReader(getParameters().getRaw().get(0)))) {
            String line = inputReader.readLine();
            String[] fields = line.split("\\s+");
            int boardsRow = Integer.parseInt(fields[0]);
            int boardsCol = Integer.parseInt(fields[1]);
            Grid<String> board = new Grid<>("", boardsRow, boardsCol);
            Coordinates tipperPos = new Coordinates(Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
            Coordinates goalCratePos = new Coordinates(Integer.parseInt(fields[4]), Integer.parseInt(fields[5]));
            int row = 0;
            while ((line = inputReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                fields = line.split("\\s+");
                for (int col = 0; col < fields.length; col++) {
                    board.set(fields[col], row, col);
                }
                row++;
            }
            this.model = new TipOverModel(board, tipperPos, goalCratePos);
            this.model.addObserver(this);
        } catch (IOException e) {
            System.out.println("""
                    ERROR: Program has ended due to incorrect file name/extension or input file may not exist.

                    Try again with correct file input.""");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("""
                    ERROR: Program has ended due to no command line arguments.

                    Try again with correct file input.""");
        }
    }
}
