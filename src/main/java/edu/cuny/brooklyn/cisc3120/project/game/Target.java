package edu.cuny.brooklyn.cisc3120.project.game;

import javafx.scene.paint.Color;

public class Target {
	private int x;
	private int y;
	private Color color;

	public Target(int x, int y) {
		this.x = x;
		this.y = y;
		this.color = Color.RED;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isTargetShot(Shot shot) {
		return x == shot.getX() && y == shot.getY();
	}
}
