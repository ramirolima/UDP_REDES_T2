package src;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Listener extends Thread {

	private DatagramSocket clientSocket;		
	String[] listRetrasmition = new String[100000];
	
	public Listener(DatagramSocket socket) {
		clientSocket = socket;
	}
	
	public void run() {

		int cont=0;		
		String ack="";
		while (!Thread.currentThread().isInterrupted()) {
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket response = new DatagramPacket(buffer, buffer.length);
				clientSocket.receive(response);
				String dataResponse = new String(buffer, 0, response.getLength());
				System.out.println(dataResponse);
				if(dataResponse.trim().equals(ack)) 
					cont++;
				else {
					cont=0;
					ack = dataResponse.trim();
				}
				
				//if cont >=3
				//reenviar pacote
				
			} catch (IOException e) {
			}
		}
	}
}
