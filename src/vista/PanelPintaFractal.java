package vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import fractal.ConjuntoDePuntos;

/**
 * Dibuja los conjuntos de puntos seleccionados para el tratamiento de los datos
 * 
 * @author PABLO ARIENZA
 * @version 10.07.2017
 */
public class PanelPintaFractal extends JPanel {

	private ArrayList<ConjuntoDePuntos> listaDeConjuntos;
	private int ancho, alto;
	
	/**
	 * Constructor de la clase
	 * 
	 * @param listaDeConjuntos la lista de listas de puntos
	 * @param ancho la anchura del panel
	 * @param alto la altura del panel
	 */
	public PanelPintaFractal(ArrayList<ConjuntoDePuntos> listaDeConjuntos, int ancho, int alto){
		this.listaDeConjuntos = listaDeConjuntos;
		this.ancho = ancho;
		this.alto = alto;
	}//fin del constructor
	
	/**
	 * Método que sobreescribe el de la clase de la que hereda
	 */
	@Override
	public void paint(Graphics g){
		Graphics2D g2D = (Graphics2D)g;
		//pinta el fondo blanco
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, ancho, alto);
		Colores color = new Colores(listaDeConjuntos.size());
		for(ConjuntoDePuntos c : listaDeConjuntos){
			//selecciona un color
			g2D.setColor(color.seleccionaColor());
			for(int i = 0; i < c.getTamLista(); i++){
				int[] coordenadas = c.getPunto(i).getCoordenadas();
				g2D.fillRect(coordenadas[0], coordenadas[1], 1, 1);
			}
		}
		g2D.scale(160, 120);
	}//fin del método
}// fin PanelPintaFractal
