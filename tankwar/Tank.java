package tankwar;

import java.awt.*;
import java.util.Random;
import java.awt.event.*;

public class Tank {
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;

	public static final int WIDTH = 30; 
	public static final int HEIGHT = 30;

	private boolean live = true; 
	private boolean good = true;
	private BloodBar bb = new BloodBar();
	
	public boolean isLive() {
		return live;
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
	TankClient tc = null;

	int x, y, oldX, oldY;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	int step = 0;
	int life = 100;
	private static Random r = new Random();
	Direction[] dirs = Direction.values();
	int rn = r.nextInt(dirs.length);
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	private boolean bL = false, 
			bU = false, 
			bR = false, 
			bD = false;

	enum Direction {L, LU, U, RU, R, RD, D, LD, STOP};

	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.D;
	
	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, TankClient tc) {
		this(x, y, good);
		this.tc = tc;
	}

	public void draw(Graphics g) {
		Color c = g.getColor(); 
		if(good) {
			g.setColor(Color.RED);
			bb.draw(g);
		}
		else {
			g.setColor(Color.BLUE);
		}
		
		if(!live) {
			if(!good) { 
				tc.tanks.remove(this);
			}
			return;
		}


		g.fillOval(x, y, WIDTH, HEIGHT); 
		g.setColor(c);
		
		switch(ptDir) {
			case L:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
				break;						
			case LU:						
				g.drawLine(x + Tank.WIDTH / 2, 
					y + Tank.HEIGHT / 2, x, y);						
				break;						
			case U:						
				g.drawLine(x + Tank.WIDTH / 2, 
					y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);						
				break;						
			case RU:						
				g.drawLine(x + Tank.WIDTH / 2, 
					y + Tank.HEIGHT	/ 2, x + Tank.WIDTH, y);						
				break;						
			case R:						
				g.drawLine(x + Tank.WIDTH / 2 ,
					y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT / 2);
				break; 
			case RD:
				g.drawLine(x + Tank.WIDTH /2 , y + Tank.HEIGHT / 2, 
					x + Tank.WIDTH, y + Tank.HEIGHT);
				break; 
			case D:
				g.drawLine(x + Tank.WIDTH /2 , y + Tank.HEIGHT / 2, 
					x + Tank.WIDTH /2, y + Tank.HEIGHT);
				break; 
			case LD:
				g.drawLine(x + Tank.WIDTH /2 , y + Tank.HEIGHT / 2, 
					x, y + Tank.HEIGHT);
				break; 
			case STOP:
				break;
		}

		move();
	}
	
	void move() {
		oldX = x;
		oldY = y;
		switch(dir) {
			case L:
				x -= XSPEED;
				break;
			case LU:
				x -= XSPEED; 
				y -= YSPEED;
				break; 
			case U:
				y -= YSPEED;
				break; 
			case RU:
				x += XSPEED; 
				y -= YSPEED;
				break; 
			case R:
				x += XSPEED;
				break; 
			case RD:
				x += XSPEED; 
				y += YSPEED;
				break; 
			case D:
				y += YSPEED;
				break; 
			case LD:
				x -= XSPEED; 
				y += YSPEED;
				break; 
			case STOP:
				break;
		}
		
		if(this.dir != Direction.STOP) {
			this.ptDir = this.dir;
		}
		
		if(x < 0) 
			x = 0;
		if(y < 25) 
			y = 25;
		if(x + Tank.WIDTH > TankClient.GAME_WIDTH) 
			x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT > TankClient.GAME_HEIGHT) 
			y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		
		if (!good) {
			if (step == 0) {
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}
			step--;

			if (r.nextInt(40) > 38)
				this.fire();
		}
	}


	public void KeyPressed(KeyEvent e) {
		int key = e.getKeyCode(); 
		switch(key) {
			case KeyEvent.VK_F2:
				if(!this.live) { 
					this.live = true; 
					this.life = 100;
				}
				break;
			case KeyEvent.VK_LEFT: 
				bL = true;
				break;
			case KeyEvent.VK_UP: 
				bU = true; 
				break;
			case KeyEvent.VK_RIGHT: 
				bR = true;
				break;
			case KeyEvent.VK_DOWN: 
				bD = true;
				break;
			case KeyEvent.VK_SPACE: //shooting fire by click space on keyboard
				fire();
				break;
		}
		locateDirection();
	}
	

	public void KeyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
			case KeyEvent.VK_A: //shooting super fire by click A on keyboard
				superFire();
				break;
			case KeyEvent.VK_LEFT: 
				bL = false;
				break;
			case KeyEvent.VK_UP: 
				bU = false; 
				break;
			case KeyEvent.VK_RIGHT: 
				bR = false;
				break;
			case KeyEvent.VK_DOWN: 
				bD = false;
				break;
		}
		locateDirection();
	}

	void locateDirection() {
		if(bL && !bU && !bR && !bD) 
			dir = Direction.L;
		else if(bL && bU && !bR && !bD) 
			dir = Direction.LU; 
		else if(!bL && bU && !bR && !bD) 
			dir = Direction.U; 
		else if(!bL && bU && bR && !bD) 
			dir = Direction.RU; 
		else if(!bL && !bU && bR && !bD) 
			dir = Direction.R; 
		else if(!bL && !bU && bR && bD) 
			dir = Direction.RD; 
		else if(!bL && !bU && !bR && bD) 
			dir = Direction.D; 
		else if(bL && !bU && !bR && bD) 
			dir = Direction.LD; 
		else if(!bL && !bU && !bR && !bD) 
			dir = Direction.STOP;

	}
	
	public Missile fire() {
		if(!live) return null;
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.WIDTH / 2;
		Missile m = new Missile(x, y, ptDir, tc, this.good); 
		tc.missiles.add(m);
		return m;
	}
	
	public Missile fire(Direction shootDir) {
		if(!live) return null;
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.WIDTH / 2;
		Missile m = new Missile(x, y, shootDir, tc, this.good); 
		tc.missiles.add(m);
		return m;
	}
	
	private void superFire() {
		Direction[] dirs = Direction.values();
		for(int i = 0; i < 8; i++) { 
			fire(dirs[i]);
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean collidesWithWall(Wall w) {
		if(this.getRect().intersects(w.getRect()) && this.live) {
			this.stay(); 
			return true;
		}
		return false;
	}
	
	public boolean collidesWithTanks(java.util.List<Tank> tanks) {
		for(int i = 0; i < tanks.size(); i++) { 
			Tank t = tanks.get(i);
			if(this != t) {
				if(this.live && t.isLive() 
				&& this.getRect().intersects(t.getRect())) { 
					t.stay();
					this.stay(); 
					return true;
				}
			}
		}
		return false;
	}
	
	private void stay() { 
		x = oldX;
		y = oldY;
	}
	
	private class BloodBar {
		public void draw(Graphics g) { 
			Color c = g.getColor(); 
			g.setColor(Color.RED);
			g.drawRect(x, y - 10, WIDTH, 10); 
			int w = WIDTH * life / 100; 
			g.fillRect(x, y - 10, w, 10); 
			g.setColor(c);
		}
	}
	
	public boolean eat(Blood b) {
		if(this.live && b.isLive() &&
		this.getRect().intersects(b.getRect())) { 
			this.life = 100; 
			b.setLive(false);
			return true;
		}
		return false;
	}
}
