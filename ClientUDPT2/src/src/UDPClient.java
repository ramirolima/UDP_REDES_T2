package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.Arrays;

public class UDPClient {
		
	public static void main(String args[]) throws Exception {
		
		String[] listPackets = new String[100000];
		DatagramSocket clientSocket = new DatagramSocket();	
		clientSocket.setSoTimeout(3000);
		InetAddress IPAddress = InetAddress.getByName("localhost");
		sendMessage("connect",clientSocket,IPAddress);	
		listeningMessage(0, ";connect", clientSocket, IPAddress);
		
		File file = new File("/home/r1dev/dev/JavaWorkspace/ClientUDPT2/inputFile.txt");		
        byte[] bytes = Files.readAllBytes(file.toPath());
        
        int lengthPack = 1;        
        if(bytes.length >= 1000) 
        	lengthPack = (int) Math.ceil(bytes.length/1000.0);
		        
        int contIndex = 0;
        int cont = 0;
        int n = 0;        
        int contAck = 0;        
        String ack ="";
    	String msg = "";    	   
    	
    	for(int i =0; i<lengthPack*1000; i+=1000) {
    		
        	n = (int) Math.pow(2, cont);
        	
        	for(int j = 0; j<n; j++) { 
        		if(contIndex == lengthPack)
        			break;       			
        		
        		byte[] partialPack = Arrays.copyOfRange(bytes, i, i+999);
        		msg = "ehusguri;"+contIndex+";"+new String(partialPack);
        		sendMessage(msg,clientSocket,IPAddress);
        		listPackets[contIndex] = msg;

        		String[] response = listeningMessage(cont, msg, clientSocket, IPAddress).split(";");
        		cont = Integer.parseInt(response[1]);
        		
        		if(response[0].trim().equals(ack)) {
        			cont = 0;
        			contAck++;
        		}
				else {					
					contAck=0;					
					ack = response[0].trim();
				}
        		
        		if(contAck >=3) 
        			sendMessage(listPackets[Integer.parseInt(ack.split("ack")[1])],clientSocket,IPAddress);
        		
        		contIndex++;
        	}
        	
        	if(contIndex == lengthPack) {
        		sendMessage("disconnect",clientSocket,IPAddress);
    			break;
        	}
        	
        	cont++;        	
        }
	}
	
	public static void sendMessage(String message, DatagramSocket clientSocket, InetAddress IPAddress) throws IOException {
		byte[] sendData = new byte[1024];		
		String data = message;					   
		sendData = data.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 3000);
		clientSocket.send(sendPacket);		
	}
	
	public static String listeningMessage(int cont, String  msg, DatagramSocket clientSocket, InetAddress IPAddress) throws IOException, InterruptedException {		
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		while(true) {
			try {			
				clientSocket.receive(receivePacket);
				break;
			} catch (SocketTimeoutException e) {				
				sendMessage(msg,clientSocket, IPAddress);
				cont = 0;
				System.out.println("Reenviando pacote: "+ msg.split(";")[1]);
			}
		}
		
		String dataResponse = new String(receiveData, 0, receivePacket.getLength());		
		System.out.println(dataResponse);
		return dataResponse+";"+cont;
	}
}