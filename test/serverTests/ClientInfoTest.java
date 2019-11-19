package serverTests;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import java.net.InetAddress;
import java.net.UnknownHostException;

import server.ClientInfo;

public class ClientInfoTest {


	  @Test
	  public void testClientInfoCreation() throws UnknownHostException {
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo("name",1 ,a ,443 , "secretKey");
		  assertEquals("name", C1.getName());
		  assertEquals(1, C1.getId());
		  assertEquals(a, C1.getAddress());
		  assertEquals(443, C1.getPort());
		  assertEquals("secretKey", C1.getSecretKey());
	  }
	  @Test (expected = NullPointerException.class)
	  public void testNullPointerName()throws UnknownHostException{
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo(null,1 ,a ,443 , "secretKey");
	  }
	  
	  @Test (expected = NullPointerException.class)
	  public void testNullPointerAddress()throws UnknownHostException{
		  
		  ClientInfo C1 = new ClientInfo("name",1 ,null ,443 , "secretKey");
	  }
	  @Test (expected = NullPointerException.class)
	  public void testNullPointerKey()throws UnknownHostException{
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo("name",1 ,a ,443 , null);
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionID() throws UnknownHostException{
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo("name",-1 ,a ,443 , null);
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortneg() throws UnknownHostException{
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo("name",1 ,a ,-443 , null);
	  }
	  @Test (expected = IllegalArgumentException.class)
	  public void testIllegalArgumentExceptionPortzero() throws UnknownHostException{
		  InetAddress a = InetAddress.getByName("localhost");
		  ClientInfo C1 = new ClientInfo("name",1 ,a ,0 , null);
	  }
}
