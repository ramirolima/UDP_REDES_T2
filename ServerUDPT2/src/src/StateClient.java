package src;

public class StateClient {
	
	String identity;
	String[] listPackets= new String[100000];
	int ack = 0;
	
	public StateClient(String identity) {
		this.identity = identity;		
	}
	
	public void addPacket(int index, String content) {
		listPackets[index] = content;
	}
	
	public boolean verifyAck(int index) {
		return (listPackets[index] != null && listPackets[index] != "");		
	}
	
	public void searchEmpty() {
		for(int i = ack; i<listPackets.length; i++) 
			if(!verifyAck(i)) {
				ack = i;
				break;
			}
	}
	
	public int getAck() {
		return ack;
	}
	
	public void setAck(int ack) {
		this.ack = ack;
	}
	
}
