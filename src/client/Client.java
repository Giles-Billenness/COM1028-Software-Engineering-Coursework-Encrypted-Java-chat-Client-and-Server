package client;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

/**
 * This class is used as the backend of the user client. It details methods to
 * send, receive, encrypt, decrypt messages from and to the server. This class
 * makes use of the methods from the GUI class to add appropriate information to
 * the GUI.
 * 
 * @author Giles Billenness
 * @version 1.0
 */
public class Client {
	/**
	 * The variable is used to store the servers secret key for encryption
	 */
	private String serverSecretKey;
	/**
	 * This variable is used to store the clients secret key for decryption and
	 * forwarding to the server
	 */
	private String clientSecretKey;
	/**
	 * This variable is used to store the value of the secret key of type
	 * SecretKeySpec after being given a value by the set key method
	 */
	private static SecretKeySpec secretKey;
	/**
	 * This variable is used to store the byte array of the secret key used in the
	 * set key method
	 */
	private static byte[] key;

	/**
	 * This is used to create a socket to send the messages to the server
	 */
	private DatagramSocket socket = null;
	/**
	 * This variable is used to set the address of the client
	 */
	private InetAddress address = null;
	/**
	 * This variable is used to set the port of the client
	 */
	private int port = 0;
	/**
	 * This variable is used to set the name of the client user
	 */
	private String name = null;
	/**
	 * This array list is used to store the names of the users currently connected
	 */
	private ArrayList<String> users = null;
	/**
	 * This variable is used to give the status of the client listening for packets
	 */
	private static boolean running = false;

	/**
	 * This is the constructor for Client objects
	 * @param name
	 * @param address
	 * @param port
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public Client(String name, String address, int port) throws NullPointerException, IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException("Username cannot be null");
		} else if (address == null) {
			throw new NullPointerException("address cannot be null");
		} else if (port <= 0) {
			throw new IllegalArgumentException("port needs to be a valid value");
		}
		try {
			this.name = name;// sets the name
			this.address = InetAddress.getByName(address);// converts address string to Inetaddress
			this.port = port;// sets the port

			socket = new DatagramSocket();// creates a new datagram socket
			this.users = new ArrayList<String>();// sets up connected users array list on client side

			clientSecretKey = getAlphaNumericString(10);// sets a random secret key using the random string method
			System.out.println("The client secret key is: " + clientSecretKey);

			running = true;// keeps the listener running
			listen();// starts listening for messages from the server
			send("\\con:");// auto connects to the server

		} catch (Exception e) {// if exception then print stack trace
			e.printStackTrace();
		}

	}

	/**
	 * This method is to send packets to the server, determining if they need to be
	 * encrypted or not if they are server commands
	 * 
	 * @param message
	 */
	public void send(String message) throws NullPointerException {// (String message, InetAddress address, int port)
		if (message == null) {
			throw new NullPointerException("message cannnot be null");
		}
		try {
			if (!message.startsWith("\\")) {// if the message isnt a command append the relevant info and encrypt
				// gives the name of the person who sent a message and the time it was sent
				message = name + " - [" + LocalDate.now() + "] [" + LocalTime.now() + "] : " + message;
				System.out.println("Going to encrypt this message: " + message);
				System.out.println("With the server secret key: " + serverSecretKey);
				message = encrypt(message, serverSecretKey);
				System.out.println("The encrypted message is: " + message);
			} else if (message.startsWith("\\")) {// if the message is a command add the name and public key onto the
													// end
				message = message + name + ";" + clientSecretKey + "**k**";// k identifier to find end of public key
				System.out.println("client secret key sent in con/dis message");
				// commands are not encrypted
			}
			message += "\\e";// adds end of message identifier
			byte[] data = message.getBytes(); // puts into byte array of size of the message converting it to byte array
			// puts byte array into packet
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);// sets up the packet with the
																							// clients(server's) info
			socket.send(packet);// sends the packet off
			System.out.println("Sent message to: " + address.getHostAddress() + port);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a listen method used to receive packets from the server.
	 */
	private void listen() {// contains thread to wait for messages to arrive to do stuff
		Thread listenThread = new Thread("ChatProgram Listener") {// used due to socket.recive so it doesn't stop the
																	// current thread until it gets a message
			@Override
			public void run() {
				try {
					while (running) {// while the server is running
						byte[] data = new byte[1024];// creates a byte array of size 1024
						DatagramPacket packet = new DatagramPacket(data, data.length);// writes to the data array
						socket.receive(packet);// puts data into the packet when receiving

						String message = new String(data);// converts the byte array into a message of type string
						message = message.substring(0, message.indexOf("\\e"));// if there is a \e then it will be
																				// marked as the end of the message to
																				// save data
						System.out.println("Message reccived from server: " + message);
						// if the message is a user connection message
						if (message.substring(0, 5).equals("user:") && message
								.substring(message.indexOf(","), message.indexOf(",") + 12).equals(", Connected!")) {
							System.out.println("goes in loop should add to users");
							// adds the username to the list of users
							users.add(message.substring(4, message.indexOf(",")));
							System.out.println("Currently connected users:");
							for (String user : users) {
								System.out.println(user);
							}
							// ClientWindow.printToUserArea(message.substring(4,
							// message.indexOf(",")-1));//ability to have a connected users textarea
							// takes out the server public key
							serverSecretKey = message.substring(message.indexOf("!") + 1);
							message = message.substring(0, message.indexOf("!") + 1);// gets rid of the server public
																						// key from message
							System.out.println(message);
							System.out.println("Here is the server public key reccived at the client: "
									+ serverSecretKey/* ServerpubkeyString */);
							ClientWindow.printToConsole(message);// prints your message to the textarea
							// if its a disconnection message, remove the user from the list
						} else if (message.substring(0, 5).equals("user:")
								&& message.substring(message.indexOf(",")).equals(", disconected!")) {
							System.out.println("goes in loop should remove user");
							for (String user : users) {
								if (message.substring(0, 5).equals(user)) {
									int index = users.indexOf(user);
									users.remove(index);
								}
							}
							System.out.println("Currently connected users:");
							for (String user : users) {
								System.out.println(user);
							}
							ClientWindow.printToConsole(message);// prints your message to the textarea
						} else

						// Message management
						// if the message isn't a command which it should be, then it puts it on the gui
						if (!isCommand(message, packet) && !message.substring(0, 5).equals("user:")) {
							// decrpyt message here using client private key
							System.out.println("Going to decrypt this message: " + message);
							System.out.println("With the client secret key: " + clientSecretKey);
							message = decrypt(message, clientSecretKey);
							System.out.println("The decrypted message is: " + message+ " and was reccived at : "+ " - [" + LocalDate.now() + "] [" + LocalTime.now() + "] : ");
							// print message
							ClientWindow.printToConsole(message);// prints your message to the textarea
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		listenThread.start();
	}

	/**
	 * This method is used to determine if a message is a command or not command
	 * list: \dis: disconnects from server \con: connects to server
	 * 
	 * @param message
	 * @param packet
	 * @return
	 */
	private static boolean isCommand(String message, DatagramPacket packet) throws NullPointerException {
		if (message == null) {
			throw new NullPointerException("message cannnot be null");
		} else if (packet == null) {
			throw new NullPointerException("packet cannnot be null");
		} else {
			if (message.startsWith("\\con:")) {

				return true;
			} else if (message.startsWith("\\dis:")) {
				return true;
			}
			return false;
		}
	}

	/**
	 * This method is used to make the client stop listening for messages/packets
	 */
	public static void stop() {// stops the server without closing the program
		running = false;
	}

	// ENCRYPTION METHODS
	/**
	 * This method is used inside the encryption and decryption methods to set
	 * SecretKeySpec secretkey's value from a string.
	 * 
	 * @param myKey
	 */
	public static void setKey(String myKey) {
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
	public static String encrypt(String stringToEncrypt, String secret) {
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
	 * @param stringToDecrypt
	 * @param secret
	 * @return decrypted string
	 */
	public static String decrypt(String stringToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
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
	public static String getAlphaNumericString(int n) {
		// chose a Character random from this String of all characters
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
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
