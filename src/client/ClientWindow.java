package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class is a GUI class created using the swing designer. It heavily makes
 * use of a backend class Client.java to perform the necessary
 * requirements/tasks.
 * 
 * @author Giles Billenness
 * @version 1.0
 */
public class ClientWindow {

	private JFrame frame;
	/**
	 * This is the message field used to take the users input for use in methods to
	 * send and process data.
	 */
	private JTextField messageField= null;
	/**
	 * This is the main textArea to contain the relevant information for the user
	 * and message content from the server
	 */
	private static JTextArea textArea = new JTextArea();// need access to this
	/**
	 * This is a textArea that could be used to store the currently connected users
	 */
	private static JTextArea usersTextArea = new JTextArea();// !!!!!!!!!!!
	/**
	 * This is the client variable used to create an instance of the backend
	 * Client.java class.
	 */
	private Client client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// makes the window look normal on desktop using windows ui scheme
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					ClientWindow window = new ClientWindow();
					window.frame.setVisible(true);

				} catch (Exception e) {// if an exception occurs print the stack trace.
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientWindow() {
		initialize();// call the initialize method.

		String name = JOptionPane.showInputDialog("Enter name");// allows the user to enter their custom name for chat
		// String serverip = JOptionPane.showInputDialog("Enter ip adress of the
		// server");//can be used to connect to any server ip
		String serverport = JOptionPane.showInputDialog("Enter port of the server");// can be used to connect to any //
																					// port
		// creates client instance with the input variables
		client = new Client(name, "localhost"/* serverip.toString() */, /* 443 */ Integer.parseInt(serverport));

		// Adds the client guide onto the main text area so the user can see it.
		textArea.append("Program guide:" + "\n" + "Welcome to the java chat client! \nServer commands are: \n"
				+ "[\\con:] to connect to the server \n" + "[\\dis:] to disconect from the server \n"
				+ "The window will show when a user connects,\ndisconects and sends a message,\ngiving the name of the user and the time it was sent \n"
				+ "Enter your message to be sent to the other users in the text box below and press the send button or press the enter key \n"
				+ "------------------------------------------------------------------------- \n");
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Chat Program");
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		// allows scrolling of messages by putting the text area in a scroll pane
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// prevents the user from sending empty messages and checks if enter is pressed
				// to submit the text
				if (!messageField.getText().equals("") && e.getKeyCode() == KeyEvent.VK_ENTER) {
					// sends the message to the backend client to be sent to the server
					client.send(messageField.getText());
					messageField.setText("");// resets text field after sending a message
				}

			}
		});

		panel.add(messageField);
		messageField.setColumns(30);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(e -> {
			if (!messageField.getText().equals("")) {// prevents the user from sending empty messages
				client.send(messageField.getText());
				messageField.setText("");// resets text field after sending a message
			}

		});
		panel.add(btnSend);
		frame.setLocationRelativeTo(null);// starts the window in the centre of the screen
		// used to run when the chat client window is closed to auto disconnect
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("WINDOW CLOSED");// logs window closed
				client.send("\\dis:");// sends disconnection message to server when the program is closed
			}
		});
	}

	/**
	 * This method is used to add information to the main text area for the user to
	 * see.
	 * 
	 * @param message
	 */
	public static void printToConsole(String message) {
		textArea.setText(textArea.getText() + message + "\n");// prints to main text area
		// has the scroll at the bottom of the text area when there are many messages
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

	/**
	 * This method is used to add information to the user text area.
	 * 
	 * @param message
	 */
	public static void printToUserArea(String message) {
		textArea.setText(usersTextArea.getText() + message + ", ");
	}

}
