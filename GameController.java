package cs333;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class GameController {

	@FXML
	private AnchorPane root;
	@FXML
	private Pane gamePane;
	@FXML
	private Rectangle player;
	@FXML
	private Label scoreLabel;
	@FXML
	private Label finalScoreLabel;
	@FXML
	private Pane overlay; // VBox in FXML but Pane works for visibility

	private final List<Rectangle> enemies = new ArrayList<>();
	private AnimationTimer timer;

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean gameOver = false;

	private double playerSpeed = 6.0;
	private double enemyMinSpeed = 2.5;
	private double enemyMaxSpeed = 6.0;
	private long lastSpawnNs = 0L;
	private long spawnIntervalNs = 300_000_000L; // ~0.3 s
	private long startTimeNs;

	@FXML
	public void initialize() {
		Platform.runLater(() -> root.requestFocus());
		startGameLoop();
	}

	private void startGameLoop() {
		gameOver = false;
		overlay.setVisible(false);
		scoreLabel.setText("0");
		enemies.clear();
		gamePane.getChildren().removeIf(n -> n != player);
		startTimeNs = System.nanoTime();
		lastSpawnNs = 0L;

		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update(now);
			}
		};
		timer.start();
	}

	private void update(long now) {
		double px = player.getLayoutX();
		if (leftPressed)
			px -= playerSpeed;
		if (rightPressed)
			px += playerSpeed;
		px = Math.max(0, Math.min(px, gamePane.getWidth() - player.getWidth()));
		player.setLayoutX(px);

		if (lastSpawnNs == 0L || now - lastSpawnNs >= spawnIntervalNs) {
			spawnEnemy();
			lastSpawnNs = now;
			spawnIntervalNs = Math.max(120_000_000L, (long) (spawnIntervalNs * 0.985));
			enemyMaxSpeed = Math.min(12.0, enemyMaxSpeed + 0.05);
		}

		Iterator<Rectangle> it = enemies.iterator();
		while (it.hasNext()) {
			Rectangle e = it.next();
			double speed = (double) e.getUserData();
			e.setLayoutY(e.getLayoutY() + speed);

			if (e.getBoundsInParent().intersects(player.getBoundsInParent())) {
				endGame();
				return;
			}

			if (e.getLayoutY() > gamePane.getHeight() + 40) {
				it.remove();
				gamePane.getChildren().remove(e);
			}
		}

		long elapsedNs = System.nanoTime() - startTimeNs;
		int score = (int) Math.floor((elapsedNs / 1_000_000_000.0) * 10.0);
		scoreLabel.setText(Integer.toString(score));
	}

	private void spawnEnemy() {
		double width = ThreadLocalRandom.current().nextDouble(24, 64);
		double x = ThreadLocalRandom.current().nextDouble(0, gamePane.getWidth() - width);
		Rectangle enemy = new Rectangle(width, 18);
		enemy.setArcWidth(6);
		enemy.setArcHeight(6);
		enemy.setStyle("-fx-fill: #ef4444;");
		enemy.setLayoutX(x);
		enemy.setLayoutY(-24);
		double speed = ThreadLocalRandom.current().nextDouble(enemyMinSpeed, enemyMaxSpeed);
		enemy.setUserData(speed);
		enemies.add(enemy);
		gamePane.getChildren().add(enemy);
	}

	private void endGame() {
		if (gameOver)
			return;
		gameOver = true;
		if (timer != null)
			timer.stop();
		finalScoreLabel.setText("Your score: " + scoreLabel.getText());
		overlay.setVisible(true);
	}

	@FXML
	private void onKeyPressed(javafx.scene.input.KeyEvent e) {
		if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A)
			leftPressed = true;
		if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D)
			rightPressed = true;
		if (e.getCode() == KeyCode.R && gameOver)
			startGameLoop();
	}

	@FXML
	private void onKeyReleased(javafx.scene.input.KeyEvent e) {
		if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A)
			leftPressed = false;
		if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D)
			rightPressed = false;
	}
}
