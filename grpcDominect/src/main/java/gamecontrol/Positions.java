package gamecontrol;

import gameboard.Position;
public class Positions {
	private int x1, y1, x2, y2;
	

    public Positions(int x1, int y1, int x2, int y2) {
    	this.x1 = x1;
    	this.y1 = y1;
    	
    	this.x2 = x2;
    	this.y2 = y2;
    }
    
    public boolean equals(Positions other) {
        return other instanceof Positions
            && (other).getX1() == getX1()
            && (other).getY1() == getY1()
            && (other).getX2() == getX2()
            && (other).getY2() == getY2();
    }
    
    public boolean intersects(Positions other) {
        return other instanceof Positions
            && ((other).getX1() == getX2()
            && (other).getY1() == getY2())
            || ((other).getX2() == getX1()
            && (other).getY2() == getY1())
            || ((other).getX1() == getX1()
            && (other).getY1() == getY1())
            || ((other).getX2() == getX2()
            && (other).getY2() == getY2());
    }

    // GETTERS 
    public int  getX1(){return x1;}
    public int  getY1(){return y1;}
    
    public int  getX2(){return x2;}
    public int  getY2(){return y2;}
    
    // SETTERS
    public void setX1(int x) {
        this.x1 = x;
    }
    public void setY1(int y) {
        this.y1 = y;
    }
    public void setX2(int x) {
        this.x2 = x;
    }
    public void setY2(int y) {
        this.y2 = y;
    }
}
