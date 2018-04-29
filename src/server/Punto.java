package server;


public class Punto {

	private int x, y;
	
	public Punto(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int[] getCoordenadas() {
		int[] coordendas = {x, y};
		return coordendas;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
