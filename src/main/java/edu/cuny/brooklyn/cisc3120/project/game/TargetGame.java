package edu.cuny.brooklyn.cisc3120.project.game;

import edu.cuny.brooklyn.cisc3120.project.game.gui.GameUI;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TargetGame {
	private final static char TARGET_INDICATOR_ON_BOARD = 'X';
	private static Logger logger = LoggerFactory.getLogger(TargetGame.class);
	private final int GAME_TARGET_AREA_WIDTH = 40;
	private final int GAME_TARGET_AREA_HEIGHT = 40;

	private GameBoard gameBoard; // having its own dimension: cells
	private GameUI gameUI; // having its own dimension: cells to characters
	private Random rng;
	private Target target;

	public TargetGame(Stage primaryStage) {
		gameBoard = new GameBoard(GAME_TARGET_AREA_HEIGHT, GAME_TARGET_AREA_WIDTH);
		gameUI = new GameUI(gameBoard, primaryStage);
		rng = new Random();
	}

	public void play() {
		setNewTarget();
		gameUI.setPostShotActionHandler(() -> {
			setNewTarget();
		});
		gameUI.show();
	}

	private void setNewTarget() {
		target = getRandomTarget();
		addTargetToBoard(target);
		gameUI.addTargetToUI(target);
	}

	private Target getRandomTarget() {
		int x = rng.nextInt(GAME_TARGET_AREA_WIDTH);
		int y = rng.nextInt(GAME_TARGET_AREA_HEIGHT);
		Target target = new Target(x, y);
		logger.debug("Target: " + x + "," + y);
		return target;
	}

	private void addTargetToBoard(Target target) {
		gameBoard.setCell(target.getX(), target.getY(), TARGET_INDICATOR_ON_BOARD);
	}

	public interface PostShotAction {
		public void postAction();
	}

}
