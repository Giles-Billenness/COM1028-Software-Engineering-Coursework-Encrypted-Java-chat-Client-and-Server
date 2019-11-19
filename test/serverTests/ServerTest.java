package serverTests;
import org.junit.Test;

import server.Server;
public class ServerTest {

	  @Test
	  public void testServerCreation() {
		  Server s1 = new Server(443);
		  //there is variables initalized:
		  
		  //socket = new DatagramSocket(port);// specified the port so the server can receive a message
		  //Clients = new ArrayList<ClientInfo>();
		  //serverSecretKey = getAlphaNumericString(10);
		  
		  //no getters available to test these values but in other testing (in test plan) it was determined that the object creation was correct
		  
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortneg() {
		  Server s1 = new Server(-443);
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortzero() {
		  Server s1 = new Server(0);
	  }

}
