package ejecutable;

import java.net.UnknownHostException;

import controlador.Controlador;

public class Pruebas {

	public static void main(String[] args) {
		try {
			Controlador servidor = new Controlador(true, 9999);
			Controlador cliente = new Controlador(false, 9999);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
