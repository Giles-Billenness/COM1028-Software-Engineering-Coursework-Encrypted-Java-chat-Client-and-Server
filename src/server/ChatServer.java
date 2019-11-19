package server;

/**
 * This class is used to start the server using a main method and the
 * Server.java class.
 * 
 * @author Giles Billenness
 * @version 1.0
 */
public class ChatServer {

	public static void main(String[] args) {
		Server s1 = new Server(443);// creates server object
		s1.start(443);// used to start the server with the given port
		// can start multiple servers with different ports for different channels as you
		// can enter port number at client startup
	}

}
