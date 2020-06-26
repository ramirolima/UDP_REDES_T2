package src;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {

	public static void main(String args[]) throws Exception {

		boolean run = true;

		List<StateClient> listConn= new ArrayList<StateClient>();

		DatagramSocket serverSocket = new DatagramSocket(3000);
		System.out.print("\nServer listening in port " + serverSocket.getLocalPort() + "\n");

		while (run) {		
			String msgOut = "";
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			String[] data = new String(receivePacket.getData()).split(";");
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			switch(data[0].trim()) {
			case "ehusguri":
				for(StateClient client : listConn){
					if(client.identity.equals(IPAddress.toString()+":"+port)) {
						client.addPacket(Integer.parseInt(data[1]), data[2]);
						if(client.verifyAck(client.getAck())) 
							client.searchEmpty();
						
						System.out.println("Mensagem "+data[1]+" recebido");
						msgOut = "ack"+client.getAck();							
						break;
					}
				}
				break;
			case "connect":					
				listConn.add(new StateClient(IPAddress.toString()+":"+port));
				System.out.println("Cliente "+IPAddress.toString()+":"+port+" conectado");
				msgOut = "Conexão estabelecida com sucesso!";
				break;				
			case "disconnect":
				for(StateClient client : listConn){
					if(client.identity.equals(IPAddress.toString()+":"+port)) {
						listConn.remove(client);
						break;
					}
				}
				System.out.println("Cliente "+IPAddress.toString()+":"+port+" desconectado");
				msgOut = "Conexão finalizada!";
				break;
			}

			byte[] responseData = new byte[1024];
			responseData = msgOut.getBytes();
			DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, IPAddress, port);
			serverSocket.send(responsePacket);
		}
	}
}
