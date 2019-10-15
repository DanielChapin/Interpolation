package daniel.interpolation.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Interpolation extends JFrame {

	Canvas canvas = new Canvas();
	
	static final int FPS = 60;
	
	final Dimension gridSize = new Dimension(10, 10);
	
	double[] heights;
	int[] directions;
	
	double movementSpeed = 0.01d;
	
	public static void main(String[] args) {
		new Interpolation();
	}
	
	Interpolation() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 800);
		setLocationRelativeTo(null);
		setTitle("Interpolation");
		setBackground(Color.WHITE);
		setResizable(false);
		
		add(canvas);
		
		setVisible(true);
		canvas.createBufferStrategy(3);
		
		heights = new double[gridSize.width * gridSize.height];
		directions = new int[heights.length];
		
		for (int i = 0; i < heights.length; i++) {
			heights[i] = (int) (Math.random() * 100) / 100d;
			directions[i] = (heights[i] > 0.5) ? -1 : 1;
		}
		
		startLoop();
	}
	
	void startLoop() {
		long now = System.nanoTime(), lastLoop = now, loopTime = 1000000000 / FPS;
		while (true) {
			if ((now = System.nanoTime()) - lastLoop > loopTime) {
				update();
				render();
				lastLoop = now;
			}
		}
	}
	
	void update() {
		for (int i = 0; i < heights.length; i++) {
			if (heights[i] >= 1 || heights[i] <= 0)
				directions[i] *= -1;
			heights[i] += movementSpeed * directions[i];
		}
	}
	
	void render() {
		BufferStrategy bs = canvas.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (int x = 0; x < gridSize.width - 1; x++) {
			for (int y = 0; y < gridSize.height - 1; y++) {
				int dx = canvas.getWidth() / (gridSize.width - 1);
				int dy = canvas.getHeight() / (gridSize.height - 1);
				for (int x_ = 0; x_ < dx; x_++) {
					for (int y_ = 0; y_ < dy; y_++) {
						double height = bilerp(getHeight(x, y), getHeight(x + 1, y), getHeight(x, y + 1), getHeight(x + 1, y + 1), (double) x_ / dx, (double) y_ / dy);
						g.setColor(Color.getHSBColor((float) height, 1f, 1f));
//						g.setColor(new Color((float) height, (float) height, (float) height));
//						g.setColor(new Color((int) (255 * height)));
						g.fillRect(x * dx + x_, y * dy + y_, 1, 1);
					}
				}
			}
		}
		g.dispose();
		bs.show();
	}
	
	double bilerp(double h00, double h10, double h01, double h11, double x, double y) {
		return lerp(lerp(h00, h10, x), lerp(h01, h11, x), y);
	}
	
	double lerp(double v0, double v1, double t) {
		return t * (v1 - v0) + v0;
	}
	
	double getHeight(int x, int y) {
		return heights[x + y * gridSize.width];
	}

}
