package benchmarking;

import java.text.DecimalFormat;
import java.util.Calendar;

public class CalculadoraFPS {

	private float FPS[];
	private int posicion = 0;
	private long ultimoTiempo = 0;
	
	public CalculadoraFPS() {
		FPS = new float[60];
		for(int i = 0; i < 60; i++) {
			FPS[i] = 0;
		}
		Calendar calendar = Calendar.getInstance();
		ultimoTiempo = calendar.getTimeInMillis();
	}
	
	public String framePintado() {
		Calendar calendar = Calendar.getInstance();
		long momentoActual = calendar.getTimeInMillis();
//		System.out.println(momentoActual);
		FPS[posicion] = 1000 / (float)(momentoActual - ultimoTiempo);
//		System.out.println(FPS[posicion]);
		ultimoTiempo = momentoActual;
		posicion++;
		if(posicion == 60) posicion = 0;
		return calculaLaMedia();
	}
	
	private String calculaLaMedia() {
		float media = 0;
		int i = 0;
		for(; i < 60; i++) {
			if(FPS[i] == 0) {
				break;
			}else {
				media +=FPS[i];
			}
		}
		media =  media / (i + 1);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);	
//		System.out.println(df.format(media));
		return df.format(media);
	}
}
