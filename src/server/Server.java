package server;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used to define the methods and behaviours of the server that
 * provides routing of messages from client to client.
 * 
 * @author Giles Billenness
 * @version 1.0
 */
public class Server {
	/**
	 * This variable is used to store the servers key string
	 */
	private static String serverSecretKey = null;
	/**
	 * This variable is used to store the value of the secret key of type
	 * SecretKeySpec after being given a value by the set key method
	 */
	private static SecretKeySpec secretKey = null;
	/**
	 * This variable is used to store the byte array of the secret key used in the
	 * set key method
	 */
	private static byte[] key = null;
	/**
	 * This variable is used to store the socket used in sending and receiving
	 * packets to and from the clients.
	 */
	private static DatagramSocket socket = null;
	/**
	 * This variable is used to give the status of when the server is using its
	 * listen method to receive packets.
	 */
	private static boolean running = false;
	/**
	 * This List is used to store instances(objects) of the ClientInfo class, for
	 * easy lookup and use of the contained attributes.
	 */
	private static ArrayList<ClientInfo> Clients = null;// array list of the clients
	/**
	 * This variable is used to keep track of the most recently used unique client
	 * id.
	 */
	private static int clientId = 0;

	public Server(int port) throws IllegalArgumentException {
		super();
		if (port <= 0) {
			throw new IllegalArgumentException("Port needs to be a real port number");
		}
		try {
			socket = new DatagramSocket(port);// specified the port so the server can receive a message
			Clients = new ArrayList<ClientInfo>();
			// logs the start of the server
			serverSecretKey = getAlphaNumericString(10);// gets random secret key for server
			System.out.println("The servers secret key string is: " + serverSecretKey);// logging
		} catch (Exception e) {// if an exception occurs

			e.printStackTrace();// prints the stack trace to find the source of errors
		}

	}

	/**
	 * This method is called on server startup to initialise variables and start the
	 * listen method enabling the serving of clients.
	 * 
	 * @param port
	 */
	public void start(int port) throws IllegalArgumentException {// starts the server
		if (port <= 0) {
			throw new IllegalArgumentException("Port needs to be a real port number");
		}
		try {
			running = true;// keeps the listen method running
			listen();// starts the listen method
			// logs the start of the server
			System.out.println("Server started on port: " + port + " and ip adress: " + socket.getLocalAddress());

		} catch (Exception e) {// if an exception occurs

			e.printStackTrace();// prints the stack trace to find the source of errors
		}
	}

	/**
	 * This method is used to send the messages to the appropriate clients using the
	 * send method. Encryption is also to be done here using the clients public key
	 * that is stored on connection.
	 * 
	 * @param message
	 */
	private static void broadcast(String message) throws NullPointerException {// send msg to every connected client
		if (message == null) {
			throw new NullPointerException("Message cannot be empty");
		}
		for (ClientInfo info : Clients) {// goes through all clients
			if (!message.substring(0, 5).equals("user:")) {// if it isn't a con/dis message
				System.out.println("Going to encrypt this message: " + message);
				System.out.println("With the client secret key: " + info.getSecretKey());
				System.out.println("encrypted message: " + encrypt(message, info.getSecretKey()) + " ip: "
						+ info.getAddress().getHostAddress() + " port: " + info.getPort());
				send(encrypt(message, info.getSecretKey()), info.getAddress(), info.getPort());
			} else {// else its a con/dis message
				System.out.println("message: " + message + " ip: " + info.getAddress().getHostAddress() + " port: "
						+ info.getPort());
				send(message, info.getAddress(), info.getPort());// and sends them a message
			}

		}
	}

	/**
	 * This method is used to send packets containing messages out to clients.
	 * 
	 * @param message
	 * @param address
	 * @param port
	 */
	private static void send(String message, InetAddress address, int port)
			throws NullPointerException, IllegalArgumentException {// send to individual clients
		if (message == null) {
			throw new NullPointerException("Message cant be null");
		} else if (address == null) {
			throw new NullPointerException("the ipaddress cant be null");
		} else if (port <= 0) {
			throw new IllegalArgumentException("Port needs to be a real port number");
		}

		try {
			message += "\\e";// adds an end identifier to the message text to be sent
			byte[] data = message.getBytes(); // puts into byte array of size of the message converting to byte array
			// puts byte array into packet and sets up the packet with the clients info
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			socket.send(packet);// sends the packet off
			System.out.println("Sent message to: " + address.getHostAddress() + "/" + port);// msg logging

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is used to listen for packets being sent by clients calling
	 * appropriate methods to deal with server commands and new connections.
	 */
	private static void listen() {// contains thread to wait for messages to arrive to do stuff
		Thread listenThread = new Thread("ChatProgram Listener") {// used due to socket.recive so it doesn't stop the
																	// current thread until it gets a message
			@Override
			public void run() throws NullPointerException {
				try {
					while (running) {// while the server is running
						byte[] data = new byte[1024];// creates a byte array of size 1024
						DatagramPacket packet = new DatagramPacket(data, data.length);// writes to the data array
						socket.receive(packet);// puts data into the packet when receiving
						if (data.length == 0) {// safety check if message content is missing.
							throw new NullPointerException("Message cannot be empty");
						}
						String message = new String(data);// converts the byte array into a message of type string
						message = message.substring(0, message.indexOf("\\e"));// if there is a \e then it will be
																				// marked as the end of the message to
																				// save data

						// Message management
						boolean clientexists = false;// client assumed to be not connected until proven otherwise
						// check if message is sent by a client connected to the server before sending a
						// message
						for (ClientInfo client : Clients) {
							// if the message is from a currently connected client
							if (packet.getAddress().toString().equals(client.getAddress().toString())) {
								clientexists = true;// if the client is connected
								break;// gets out of the loop
							}
							clientexists = false;// otherwise client remains not connected
						}
						// if the message isnt a command but an actual message and client is connected
						if ((!isCommand(message, packet)) && clientexists) {
							// Decrypt the message from the client here using the server key.
							System.out.println("Going to decrypt this message: " + message);// logging
							System.out.println("With the server secret key: " + serverSecretKey);// logging
							message = decrypt(message, serverSecretKey);
							System.out.println("The decrypted message is: " + message);// logging
							broadcast(message);// then it broadcasts the message to all users, within the broadcast its
												// encrypted again.
						}

					}

				} catch (Exception e) {// if an exception occurs
					e.printStackTrace();// print the stack trace
				}
			}
		};
		listenThread.start();// runs the thread on the run method
	}

	/**
	 * This method is used to determine if a message is a command and if its a
	 * command then deal with it appropriately. command list: \dis: disconnects from
	 * server, \con: connects to server
	 * 
	 * @param message
	 * @param packet
	 * @return boolean (if the message is a command)
	 */
	private static boolean isCommand(String message, DatagramPacket packet) throws NullPointerException {
		if (message == null) {
			throw new NullPointerException("message content cant be empty");
		} else if (packet == null) {
			throw new NullPointerException("Packets cannot be null");
		}
		if (message.startsWith("\\con:")) {// if its a connection message "\con:name;key"
			String name = message.substring(message.indexOf(":") + 1, message.indexOf(";")); // takes name out of the
																								// message
			String pkey = message.substring(message.indexOf(";") + 1, message.indexOf("**k**")); // takes client key out
																									// of message as
																									// type string
			System.out.println("The clients secret key string is: " + pkey.toString());// logging
			// creates an entry for the user in the array. with the clients key
			Clients.add(new ClientInfo(name, clientId++, packet.getAddress(), packet.getPort(), pkey));// id++ makes it
																										// so we always
																										// have unique
																										// id for each
																										// user
			System.out.println("new user created username:" + name);// logging
			broadcast("user: " + name + ", Connected!" + serverSecretKey + "\\e");// sends message to every connected
																					// client that someone joined
																					// containing the server public key
			for (ClientInfo currentclients : Clients) {// logs the connected users to console
				System.out.println("Currently connected clients: " + currentclients.getName());// logging
			}
			return true;// if the message is a connection command
		} else if (message.startsWith("\\dis:")) {// disconnects from server - removes from the clients to be served
													// array
			String name = message.substring(message.indexOf(":") + 1, message.indexOf(";"));// gets name from message
			int index = 0;// sets the index
			for (ClientInfo clientlist : Clients) {// finds the client from the list of clients
				if (clientlist.getName().equals(name)) {
					index = Clients.indexOf(clientlist);// gets the index of that client
					break;// when it finds the user, exit loop
				}
			}
			broadcast("user: " + name + ", disconected!" + "\\e");// sends disconnection message to all users
			Clients.remove(index);// removes the client from the list
			for (ClientInfo currentclients : Clients) {// logs the connected users to console
				System.out.println("Currently connected clients: " + currentclients.getName());// logging
			}
			return true;// if the message is a disconnection command
		}
		return false;// if the message isn't a command
	}

	/**
	 * This method stops the server from running its listen method and so prevents
	 * it from serving clients.
	 */
	public static void stop() {// stops the server without closing the program
		running = false;
	}

	/**
	 * This method is used inside the encryption and decryption methods to set
	 * SecretKeySpec secretkey's value from a string.
	 * 
	 * @param myKey
	 */
	public static void setKey(String myKey) throws NullPointerException {
		if (myKey == null) {
			throw new NullPointerException("Key string cant be null");
		}
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to encrypt a string using AES using a secret key of type
	 * string
	 * 
	 * @param stringToEncrypt
	 * @param secret
	 * @return
	 */
	public static String encrypt(String stringToEncrypt, String secret) throws NullPointerException {
		if (stringToEncrypt == null) {
			throw new NullPointerException("message string cant be null");
		} else if (secret == null) {
			throw new NullPointerException("secret key cant be null");
		}
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * This method is used to decrypt an AES encrypted string using the secretkey.
	 * 
	 * @param strToDecrypt
	 * @param secret
	 * @return decrypted string
	 */
	public static String decrypt(String strToDecrypt, String secret) throws NullPointerException {
		if (strToDecrypt == null) {
			throw new NullPointerException("message string cant be null");
		} else if (secret == null) {
			throw new NullPointerException("secret key cant be null");
		}
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method is used to generate a random string of alphanumeric characters
	 * used for the secret key used in AES encryption.
	 * 
	 * @param n = length of string wanted
	 * @return a random string of alphanumeric characters
	 */
	static String getAlphaNumericString(int n) throws IllegalArgumentException {
		if (n <= 0) {
			throw new IllegalArgumentException("The value for the length of string needs to be a whole number >=1");
		}
		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"; // all
																												// characters
		// create StringBuilder of custom size
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());
			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

}
