package client;

import java.awt.Color;
import java.util.ArrayList;

public class ConjuntoDePuntos {
	
	private int iD;
	private Color color;
	private ArrayList<Punto> listaDePuntos;
	
	public ConjuntoDePuntos(int iD) {
		this.iD = iD;
		this.color = new Color(iD % 256, (4 * iD) % 256, (8 * iD) % 256);
		listaDePuntos = new ArrayList<Punto>();
	}
	
	public void addPunto(Punto nuevo) {
		listaDePuntos.add(nuevo);
	}
	
	public ArrayList<Punto> getLista(){
		return listaDePuntos;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getID() {
		return iD;
	}
	
	public int getSize() {
		return listaDePuntos.size();
	}
	
	public Punto getPunto(int i){
		return listaDePuntos.get(i);
	}
}
