package clientTests;
import org.junit.Test;

import client.Client;
public class ClientTest {

	  @Test
	  public void testClientCreation() {
	
		  Client c1 = new Client("name", "localhost", 443 );
		  //attributes initalised
		
		  //this.name = name;// sets the name
		  //this.address = InetAddress.getByName(address);// converts address string to Inetaddress
		  //this.port = port;// sets the port
		  //socket = new DatagramSocket();// creates a new datagram socket
		  //this.users = new ArrayList<String>();// sets up connected users array list on client side
		  //clientSecretKey = getAlphaNumericString(10);// sets a random secret key using the random string method
		  
		  //no getters available to test these values but in other testing (in test plan) it was determined that the object creation was correct
	  }
	  @Test (expected = NullPointerException.class)
	  public void testNullPointerName(){
		  Client c1 = new Client(null, "localhost", 443 );
	  }

	  @Test (expected = NullPointerException.class)
	  public void testNullPointerAddress(){
		  Client c1 = new Client("name", null, 443 );
	  }
	  
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortneg(){
		  Client c1 = new Client("name", "localhost", -10 );
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortzero(){
		  Client c1 = new Client("name", "localhost", 0 );
	  }
}
