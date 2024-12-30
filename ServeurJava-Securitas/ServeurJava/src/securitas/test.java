package securitas;

import java.io.IOException;

public class test {
	public static void main(String[] args) throws IOException, InterruptedException {
		String serialPort = "COM7";
		int baudRate = 9600;
		
		ArduinoConnect connectArduino = new ArduinoConnect(serialPort, baudRate);
		
		System.out.println(connectArduino.ecouteLiaisonSerie());
		Thread.sleep(1000);
		
		connectArduino.writeLiaisonSerie("accordouverte#####00");
		
		System.out.println(connectArduino.ecouteLiaisonSerie());
		
		
		connectArduino.close();
	}

}
