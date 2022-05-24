package puzzles.lunarlanding.gui;

import javafx.application.Application;
import javafx.geometry.Orientation;
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
import puzzles.lunarlanding.model.LunarLandingModel;
import util.Coordinates;
import util.Observer;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The LunarLandingGUI application is the UI for LunarLanding.
 *
 * @author Bill Stephen
 * November 2021
 */
public class LunarLandingGUI extends Application
        implements Observer< LunarLandingModel, Object > {

    String[] filename = new String[]{""};
    String lastCommand = "";
    String movePieceName;
    Coordinates movePieceCoords;
    Stage mainStage;

    /**
     * Main grid pane of the game
     */
    private GridPane grid_pane;

    /**
     * Flow pane containing reset, undo and cheat buttons.
     */
    private FlowPane button_commands_flowpane;

    /**
     * Grid Pane containing UP(North), RIGHT(East), DOWN(South), LEFT(West) buttons.
     */
    private GridPane buttonDirGridPane;

    /**
     * Label for top (File Loaded, Illegal Move, Unsolvable , YOU WON!, I WON!).
     */
    private Label announcement_label =new Label();

    /** A definition of gray for the background */
    private static final Background GRAY =
            new Background( new BackgroundFill(Color.GRAY, null, null));
    /** Image files for pieces (Lander, Explorer, Robot)*/
    private final Image lander = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/lander.png")));
    private final Image explorerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/explorer.png")));
    private final Image blue = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-blue.png")));
    private final Image green = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-green.png")));
    private final Image orange = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-orange.png")));
    private final Image purple = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-purple.png")));
    private final Image white = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-white.png")));
    private final Image yellow = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/robot-yellow.png")));

    /**
     * Maps piece to image of explorer or robot. Misc gets assigned White
     * @param pieceName - Name of piece
     * @return new Image
     */
    private ImageView setPieceImage(String pieceName){
        if(pieceName.equals("E")){
            return new ImageView( explorerImage );
        }
        if(pieceName.equals("B")){
            return new ImageView( blue );
        }
        if(pieceName.equals("G")){
            return new ImageView( green );
        }
        if(pieceName.equals("O")){

            return new ImageView( orange );
        }
        if(pieceName.equals("P")){
            return new ImageView( purple );
        }
        if(pieceName.equals("Y")){
            return new ImageView( yellow );
        }
        return  new ImageView( white );
    }

    /**
     * Model for the view and controller.
     */
    private LunarLandingModel model;

    @Override
    public void init(){
        List<String> args = getParameters().getRaw();
        filename[0] = args.get(0);
        this.model = new LunarLandingModel(args.get(0));
        this.model.addObserver(this);

    }

    /**
     * Updates the grid pane, Updating piece positions and clears top label
     */
    public void updateGrid(){
        this.announcement_label.setText(null);
        this.grid_pane.getChildren().clear();
        int sizeX = this.model.getBoard().getNCols();
        int sizeY = this.model.getBoard().getNRows();
        Coordinates goal = this.model.getGoalLunar();
        int goalX = goal.col();
        int goalY = goal.row();
        String tempName;
        Button goalButton = new Button();
        goalButton.setGraphic(new ImageView(lander));
        goalButton.setDisable(true);
        this.grid_pane.add(goalButton, goalX, goalY);
        for (int row = 0; row < sizeY; ++row) {
            for (int col = 0; col < sizeX; ++col) {
                Coordinates tempPiece = new Coordinates(row, col);
                if(this.model.getAllPieces().containsValue(tempPiece)){
                    Button temp = new Button();
                    for(Map.Entry<String, Coordinates> entry : this.model.getAllPieces().entrySet()){
                        if (Objects.equals(entry.getValue(), tempPiece)) {
                            tempName = entry.getKey();
                            temp.setGraphic(setPieceImage(tempName));
                            if(tempPiece.equals(this.model.getGoalLunar())){
                                temp.setOpacity(0.5);
                            }
                            String finalTempName = tempName;
                            temp.setOnAction(event -> updateSelectedPiece(finalTempName, tempPiece));
                            this.grid_pane.add(temp, col, row);
                        }
                    }
                } else {
                    Button blank = new Button();
                    blank.setGraphic(new ImageView(white));
                    blank.setOpacity(0);
                    this.grid_pane.add(blank, col, row);
                    blank.setDisable(true);
                    this.grid_pane.setBackground(GRAY);
                }
            }
        }
    }

    /**
     * Creates a grid pane to be displayed to the user. The Grid pane contains N buttons. Each button represents a piece
     * on the board. Fake buttons are placed on the board when a piece isn't at that position to maintain a square
     * or rectangle format. When clicked the piece will updateSelectedPiece to be used by "go".
     */
    public void gridDisplay() {
        this.grid_pane = new GridPane();
        int sizeX = this.model.getBoard().getNCols();
        int sizeY = this.model.getBoard().getNRows();
        Coordinates goal = this.model.getGoalLunar();
        int goalX = goal.col();
        int goalY = goal.row();
        String tempName;
        Button goalButton = new Button();
        goalButton.setGraphic(new ImageView(lander));
        goalButton.setDisable(true);
        this.grid_pane.add(goalButton, goalX, goalY);
        for (int row = 0; row < sizeY; ++row) {
            for (int col = 0; col < sizeX; ++col) {
                Coordinates tempPiece = new Coordinates(row, col);
                if(this.model.getAllPieces().containsValue(tempPiece)){
                    Button temp = new Button();
                    for(Map.Entry<String, Coordinates> entry : this.model.getAllPieces().entrySet()){
                        if (Objects.equals(entry.getValue(), tempPiece)) {
                            tempName = entry.getKey();
                            temp.setGraphic(setPieceImage(tempName));
                            if(tempPiece.equals(this.model.getGoalLunar())){
                                temp.setOpacity(0.5);
                            }
                            String finalTempName = tempName;
                            temp.setOnAction(event -> updateSelectedPiece(finalTempName, tempPiece));
                            this.grid_pane.add(temp, col, row);
                        }
                    }
                } else {
                    Button blank = new Button();
                    blank.setGraphic(new ImageView(white));
                    blank.setOpacity(0);
                    this.grid_pane.add(blank, col, row);
                    blank.setDisable(true);
                    this.grid_pane.setBackground(GRAY);
                }
            }
        }
    }

    /**
     * Updates the selected piece the user wants to move
     * @param name - Name of selected piece
     * @param coords - Coords of selected piece
     */
    public void updateSelectedPiece(String name, Coordinates coords){
        this.movePieceName = name;
        this.movePieceCoords = coords;
    }

    /**
     * Updated top label
     * @param ann_label - Label to be updated
     */
    public void updateLabel(Label ann_label){
        this.announcement_label = ann_label;
    }

    /**
     * Flow pane creation to be used on the main borderpane, contains buttons load, reload and hint.
     * -On press load will launch a file chooser windows, when entered a new puzzle will be loaded
     * -On press reload will reset the current puzzle
     * -On press hint will automatically move a piece on the board that closer to the solution. If the board is
     *  unsolvable or already solved the top label will be updated.
     */
    public void flowPaneDisplay() {
        this.button_commands_flowpane = new FlowPane(Orientation.VERTICAL);
        Button loadButton = new Button("LOAD");
        Button reloadButton = new Button("RELOAD");
        Button hintButton = new Button("HINT");
        button_commands_flowpane.getChildren().addAll(loadButton, reloadButton, hintButton);
        button_commands_flowpane.setAlignment(Pos.CENTER_RIGHT);

        loadButton.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            File defaultDirectory = new File("data/lunarlanding");
            fileChooser.setInitialDirectory(defaultDirectory);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(mainStage);
            if (selectedFile != null) {
                filename[0] = selectedFile.toString();
            }
            this.model = new LunarLandingModel(filename[0]);
            this.model.addObserver( this );
            updateGrid();
            mainStage.sizeToScene();
        });

        reloadButton.setOnAction((event) -> {
            this.model = new LunarLandingModel(filename[0]);
            this.model.addObserver( this );
            updateGrid();
        });

        hintButton.setOnAction((event) -> {
            int hintResult = this.model.hint();
            lastCommand = ("hint");
            if (hintResult == 0) {
                this.announcement_label.setText("Unsolvable board");
            }
            if(hintResult == 1){
                this.announcement_label.setText("Current board is already solved");
            }
            updateLabel(this.announcement_label);
        });
    }

    /**
     * Grid pane creation to be used on the main borderpane, contains buttons up, down, left and right.
     * On press of a button will move the piece on the board, if it's an illegal move the top label will be updated.
     */
    public void gridPaneDirectionDisplay() {
        this.buttonDirGridPane = new GridPane();
        Button upButton = new Button("↑");
        Button rightButton = new Button("→");
        Button downButton = new Button("↓");
        Button leftButton = new Button("←");
        buttonDirGridPane.add(upButton, 1, 0);
        buttonDirGridPane.add(rightButton, 2, 1);
        buttonDirGridPane.add(downButton, 1, 2);
        buttonDirGridPane.add(leftButton, 0, 1);
        button_commands_flowpane.setAlignment(Pos.TOP_RIGHT);

        upButton.setOnAction((event) -> {
            lastCommand = ("go");
            this.model.go("north", movePieceCoords, movePieceName);
            if(this.model.getLegalMove()){
                this.announcement_label.setText("Illegal move");
            }
            updateLabel(this.announcement_label);
        });

        rightButton.setOnAction((event) -> {
            lastCommand = ("go");
            this.model.go("east", movePieceCoords, movePieceName);
            if(this.model.getLegalMove()){
                this.announcement_label.setText("Illegal move");
            }
            updateLabel(this.announcement_label);
        });

        downButton.setOnAction((event) -> {
            lastCommand = ("go");
            this.model.go("south", movePieceCoords, movePieceName);
            if(this.model.getLegalMove()){
                this.announcement_label.setText("Illegal move");
            }
            updateLabel(this.announcement_label);
        });

        leftButton.setOnAction((event) -> {
            lastCommand = ("go");
            this.model.go("west", movePieceCoords, movePieceName);
            if(this.model.getLegalMove()){
                this.announcement_label.setText("Illegal move");
                }
            updateLabel(this.announcement_label);
        });
    }

    /**
     * Create GUI, containing a borderpane, label in the top right, a gridpane center containg N buttons, a flowpane
     * right center containing 3 buttons and a gridpane top right containting 4 buttons.
     */
    @Override
    public void start( Stage stage ) {
        mainStage = stage;
        stage.setTitle( "Lunar Landing" );
        BorderPane main_border_pane = new BorderPane();
        gridDisplay();
        main_border_pane.setCenter(this.grid_pane);
        this.announcement_label.setText("File loaded");
        this.announcement_label.setFont(new Font(18));
        main_border_pane.setTop(this.announcement_label);
        this.announcement_label.setAlignment(Pos.TOP_LEFT);
        BorderPane sidePane = new BorderPane();
        flowPaneDisplay();
        sidePane.setCenter(this.button_commands_flowpane);
        gridPaneDirectionDisplay();
        sidePane.setTop(this.buttonDirGridPane);
        main_border_pane.setRight(sidePane);
        stage.setScene(new Scene(main_border_pane));
        stage.sizeToScene();



        stage.show();
    }

    /**
     * Update the grid & checks to see if the board was solved by the BFS or User
     * @param lunarLandingModel
     * @param o
     */
    @Override
    public void update( LunarLandingModel lunarLandingModel, Object o ) {
        updateGrid();
        if((this.model.getGoalLunar().row() == this.model.getExplorer().row()) &&
                (this.model.getGoalLunar().col() == this.model.getExplorer().col())){
            if(!lastCommand.equals("hint")) {
                this.announcement_label.setText("YOU WON!");
            }else{
                this.announcement_label.setText("I WON!");
            }
            updateLabel(this.announcement_label);
        }
    }

    public static void main( String[] args ) {
        Application.launch( args );
    }
}
