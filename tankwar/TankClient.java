package tankwar;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class TankClient extends Frame {
	
	/**
	*	This class serves as the main window of tank games
	*	@author Ayu Purnama Virgiana
	*
	*/

	public static final int GAME_WIDTH = 800; 
	public static final int GAME_HEIGHT = 600;

	Tank tank = new Tank(50, 50, true, this);
	Tank enemyTank = new Tank(100, 100, false, this);
	Wall w = new Wall(200, 400, 200, 10, this), 
			w2 = new Wall(600, 200, 10, 200, this);
	Blood b = new Blood();
	
	List<Tank> tanks = new ArrayList<Tank>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	
	Image offScreenImage = null;
	
	public void paint(Graphics g) {
		g.drawString("missiles count: " + missiles.size(), 10, 50);
		g.drawString("explodes count: " + explodes.size(), 10, 70);
		g.drawString("tanks count: " + tanks.size(), 10, 90);
		g.drawString("tank life: "+ tank.getLife(), 10, 110);
		
		w.draw(g);
		w2.draw(g);
		for(int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(tank);
			m.hitWall(w);
			m.hitWall(w2);
			if (!m.isLive()) {
				missiles.remove(m);
			} else {
				m.draw(g);
			}
		}
		
		for(int i = 0; i < explodes.size(); i++) { 
			Explode e = explodes.get(i); 
			e.draw(g);
		}

		tank.draw(g);
		tank.eat(b);
		tank.collidesWithWall(w);
		tank.collidesWithWall(w2);
		tank.collidesWithTanks(tanks);
		b.draw(g);
		for(int i = 0; i < tanks.size(); i++) {
			Tank tk = tanks.get(i);
			if(tanks.get(i).isLive()) {
				tk.collidesWithWall(w);
				tk.collidesWithWall(w2);
				tk.collidesWithTanks(tanks);
				tanks.get(i).draw(g);
			} else {
				tanks.remove(tk);
			}
			
		}

	}
	
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics(); 
		Color c = gOffScreen.getColor(); 
		gOffScreen.setColor(Color.GREEN); 
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT); 
		gOffScreen.setColor(c); 
		print(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void launchFrame() {
		this.setLocation(300, 50);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar"); 
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				System.exit(0);
			}
		});
		setResizable(false);
		this.setBackground(Color.GREEN); 
		this.addKeyListener(new KeyMonitor()); 
		setVisible(true);
		
		for(int i = 0; i < 10; i++) {
			tanks.add(new Tank(50 + 40 * (i + 1), 50, false, this));
		}

		new Thread(new PaintThread()).start();
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}
	
	private class PaintThread implements Runnable {

		public void run() {
			while(true) { 
				repaint(); 
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class KeyMonitor extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			tank.KeyReleased(e);
		}
		public void keyPressed(KeyEvent e) {
			tank.KeyPressed(e);
		}
	}

}
