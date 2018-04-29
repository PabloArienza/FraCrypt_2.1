package ejecutable;

import java.net.UnknownHostException;

import client.VentanaClient;
import controlador.Controlador;
import server.VentanaServer;

public class Pruebas {

	public static void main(String[] args) {

		VentanaServer s = new VentanaServer();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VentanaClient c = new VentanaClient();
		
		
		
		
/*		
		try {			
			Controlador servidor = new Controlador(true, 9999);
			Controlador cliente = new Controlador(false, 9999);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}

}
