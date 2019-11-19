package server;

import java.net.InetAddress;

/**
 * This class is used to define the attributes of the client objects created for
 * each connected client. These attributes are used to serve the messages
 * between the clients.
 * 
 * @author Giles Billenness
 * @version 1.0
 */
public class ClientInfo {// stores the clients data for each client
	/**
	 * Variable to store the ip address of the client
	 */
	private InetAddress address = null;
	/**
	 * Variable to store the port used for the client
	 */
	private int port = 0;
	/**
	 * Variable to store the username of the client
	 */
	private String name = null;
	/**
	 * Variable to store the unique id of the client
	 */
	private int id = 0;
	/**
	 * Variable to store the public key of the client
	 */
	public String secretKey = null;

	/**
	 * Constructor to create ClientInfo objects.
	 * 
	 * @param name
	 * @param id
	 * @param address
	 * @param port
	 * @param secretKey
	 */
	public ClientInfo(String name, int id, InetAddress address, int port, String secretKey)
			throws NullPointerException, IllegalArgumentException {
		super();
		if (name == null) {
			throw new NullPointerException("username cannot be null");
		} else if (id < 0) {
			throw new IllegalArgumentException("id needs to be a valid number");
		} else if (address == null) {
			throw new NullPointerException("address cannot be null");
		} else if (port <= 0) {
			throw new IllegalArgumentException("port needs to be a valid number");
		} else if (secretKey == null) {
			throw new NullPointerException("the public key cant be null");
		} else {
			this.address = address;
			this.port = port;
			this.name = name;
			this.id = id;
			this.secretKey = secretKey;
		}
	}

	/**
	 * 
	 * @return The public key of a ClientInfo object
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * 
	 * @return The ip address of a ClientInfo object
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * 
	 * @return The port of a ClientInfo object
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @return The username of a ClientInfo object
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The id of a ClientInfo object
	 */
	public int getId() {
		return id;
	}

}
