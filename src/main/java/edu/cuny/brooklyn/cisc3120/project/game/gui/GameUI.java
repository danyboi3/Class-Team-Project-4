package edu.cuny.brooklyn.cisc3120.project.game.gui;

import edu.cuny.brooklyn.cisc3120.project.game.GameBoard;
import edu.cuny.brooklyn.cisc3120.project.game.Shot;
import edu.cuny.brooklyn.cisc3120.project.game.Target;
import edu.cuny.brooklyn.cisc3120.project.game.TargetGame.PostShotAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class GameUI {
	final static String APP_TITLE = "CISC 3120 Fall 2017: TargetGame";

	private final static int PADDING = 20;
	private final static int INIT_TARGET_CANVAS_WIDTH = 400;
	private final static int INIT_TARGET_CANVAS_HEIGHT = 400;
	private final static int INIT_MAIN_SCENE_WIDTH = 600;
	private final static int INIT_MAIN_SCENE_HEIGHT = 500;
	private final static int maxTries = 5;

	private TextField xGuessedTextField;
	private TextField yGuessedTextField;

	private int tryCount = 0;

	private int won = 0;
	private int lost = 0;
	private int totalTries = 0;
	private int min = 0;
	private int max = 0;

	private Stage primaryStage;
	private GameBoard gameBoard;
	private Canvas targetCanvas;
	private Target target;
	private EventHandler<ActionEvent> shootingActionHandler;
	private PostShotAction postShotAction;

	public GameUI(GameBoard board, Stage stage) {
		gameBoard = board;
		primaryStage = stage;

		HBox hboxShooting = buildKeyboardInputBox();

		HBox hboxMain = buildMainBox();

		VBox vboxMain = new VBox();
		vboxMain.setPadding(new Insets(PADDING));
		vboxMain.getChildren().addAll(hboxMain, hboxShooting);

		Scene scene = new Scene(vboxMain, INIT_MAIN_SCENE_WIDTH, INIT_MAIN_SCENE_HEIGHT);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(scene);
	}

	public void addTargetToUI(Target target, boolean update) {
		if (update) {
			this.target = target;
		}
		double width = targetCanvas.getWidth();
		double height = targetCanvas.getHeight();
		double cellWidth = width / gameBoard.getWidth();
		double cellHeight = height / gameBoard.getHeight();
		double xPos = cellWidth * target.getX();
		double yPos = cellHeight * target.getY();

		GraphicsContext gc = targetCanvas.getGraphicsContext2D();
		gc.setFill(target.getColor());
		gc.fillRect(xPos, yPos, cellWidth, cellHeight);
	}


	public void show() {
		primaryStage.show();
	}

	public void setPostShotActionHandler(PostShotAction postShotAction) {
		this.postShotAction = postShotAction;
	}

	private HBox buildKeyboardInputBox() {
		xGuessedTextField = new TextField(Integer.toString((int) gameBoard.getWidth() / 2));
		xGuessedTextField.setOnMouseClicked((MouseEvent e) -> {
			xGuessedTextField.selectAll();
		});
		yGuessedTextField = new TextField(Integer.toString((int) gameBoard.getHeight() / 2));
		yGuessedTextField.setOnMouseClicked((MouseEvent e) -> {
			yGuessedTextField.selectAll();
		});
		Button btnShoot = new Button("Shoot!");
		postShotAction = null;
		shootingActionHandler = (ActionEvent e) -> {
			int shotX = Integer.parseInt(xGuessedTextField.getText());
			int shotY = Integer.parseInt(yGuessedTextField.getText());
			Shot shot = new Shot(shotX, shotY);
			handleShotAction(target, shot);
		};
		btnShoot.setOnAction(shootingActionHandler);

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(PADDING));
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		hbox.getChildren().addAll(xGuessedTextField, yGuessedTextField, btnShoot);

		return hbox;
	}

	private VBox buildSideBar() {
		VBox vboxSideBar = new VBox();
		StackPane shootingPane = buildShootingPane();
		StackPane vboxStatistics = buildStatisticsBox();

		vboxSideBar.getChildren().addAll(shootingPane, vboxStatistics);

		vboxSideBar.setPadding(new Insets(0, 0, 0, 15));

		return vboxSideBar;
	}

	private StackPane buildStatisticsBox() {
		StackPane parent = new StackPane();
		VBox statisticsBox = new VBox();

		Text title = new Text("Game Statistics");
		title.setTextAlignment(TextAlignment.CENTER);

		Text stats = new Text();

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				String rounded = "";

				if (won + lost != 0) {
					double average = ((double) totalTries / (won + lost));

					rounded = String.format("%.2f", average);
				}

				stats.setText("\n\nWon: " + won + "\nLost: " + lost + "\nAverage: " + rounded + "\nBest: " + (min == 0 ? "" : min) + "\nWorst: " + (max == 0 ? "" : max));
			}
		}, 0, 100);

		statisticsBox.getChildren().addAll(title, stats);

		parent.getChildren().addAll(statisticsBox);

		parent.setPadding(new Insets(15, 0, 0, 0));
		parent.setMinHeight(250);

		statisticsBox.setBackground(new Background(new BackgroundFill(Color.WHITE,
				CornerRadii.EMPTY, Insets.EMPTY)));

		return parent;
	}

	private StackPane buildShootingPane() {
		StackPane shootingPane = new StackPane();

		Canvas canvas = new Canvas(150, 150);

		canvas.setStyle("");

		canvas.setOnMouseMoved(event -> {
			GraphicsContext context = canvas.getGraphicsContext2D();

			context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

			context.beginPath();
			context.moveTo(0, event.getY());
			context.lineTo(150, event.getY());
			context.stroke();
			context.beginPath();
			context.moveTo(event.getX(), 0);
			context.lineTo(event.getX(), 150);
			context.stroke();
		});

		canvas.setOnMouseClicked(event -> {
			int x = (int) Math.round(event.getX() / 150 * 40);
			int y = (int) Math.round(event.getY() / 150 * 40);

			xGuessedTextField.setText(Integer.toString(x));
			yGuessedTextField.setText(Integer.toString(y));

			handleShotAction(target, new Shot(x, y));
		});

		shootingPane.getChildren().add(canvas);

		shootingPane.setBackground(new Background(new BackgroundFill(Color.WHITE,
				CornerRadii.EMPTY, Insets.EMPTY)));

		return shootingPane;
	}

	private HBox buildMainBox() {
		targetCanvas = new Canvas(INIT_TARGET_CANVAS_WIDTH, INIT_TARGET_CANVAS_HEIGHT);
		StackPane canvasHolder = new StackPane();
		canvasHolder.setMaxWidth(Double.MAX_VALUE);
		canvasHolder.setBackground(new Background(new BackgroundFill(Color.WHITE,
				CornerRadii.EMPTY, Insets.EMPTY)));
		canvasHolder.getChildren().add(targetCanvas);

		VBox vboxSideBar = buildSideBar();

		HBox hbox = new HBox();
		hbox.getChildren().addAll(canvasHolder, vboxSideBar);

		return hbox;
	}

	private void reset() {
		GraphicsContext gc = targetCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, 400, 400);
	}

	private void handleShotAction(Target target, Shot shot) {
		++tryCount;

		if (target.isTargetShot(shot)) {
			Alert alert = new Alert(AlertType.INFORMATION, "You shot the target!", ButtonType.CLOSE);
			alert.setTitle(APP_TITLE + ":Target Shot");
			alert.setHeaderText("Shot!");
			alert.showAndWait();
			reset();
			if (postShotAction != null) {
				postShotAction.postAction();
			}
			totalTries += tryCount;
			++won;

			if (min == 0 || min > tryCount) {
				min = tryCount;
			}

			if (max == 0 || max < tryCount) {
				max = tryCount;
			}

			tryCount = 0;
			return;
		} else {
			Alert alert = new Alert(AlertType.INFORMATION, "Missed!", ButtonType.CLOSE);
			alert.setTitle(APP_TITLE + ":Target Missed");
			alert.setHeaderText("You missed the target!");
			alert.showAndWait();

			Target shotTarget = new Target(shot.getX(), shot.getY());
			shotTarget.setColor(Color.BLACK);
			addTargetToUI(shotTarget, false);
		}

		if (tryCount == maxTries) {
			Alert alert = new Alert(AlertType.INFORMATION, "You lost!", ButtonType.CLOSE);
			alert.setTitle(APP_TITLE + ":Game over");
			alert.setHeaderText("Game Over!");
			alert.showAndWait();
			reset();
			if (postShotAction != null) {
				postShotAction.postAction();
			}
			totalTries += tryCount;
			tryCount = 0;
			++lost;
		}
	}
}
