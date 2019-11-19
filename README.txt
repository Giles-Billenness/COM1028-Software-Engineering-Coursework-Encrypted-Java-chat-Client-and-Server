Welcome to the encrypted java chat client and server!
The software will allow for communication between clients that is simple and easy to use.

===============================================================================

To use the software using a single computer, you will need to do the following:
1. Open the eclipse project.
2. Run the server using ChatServer.java that will create a server object and start running it (the port the server runs on can be changed by changing the value in the .java, it starts with it being "443").
3. Run a Client instance using ClientWindow.java that will create a client instance and window for the user.
4. Pick a username for your client instance and enter it into the prompt.
5. Hit the ok button or the enter key on your keyboard.
6. Pick a port for the server you want to connect to and enter it into the prompt (the default is "443" entered without "").
7. Hit the ok button or the enter key on your keyboard.
8. Once the server and client have been launched this way, the server and client should have exchanged keys for encryption and decryption.
9. The Client window will display the program info guide at the top of the window and a successful connection message received from the server.
10. Messages can be sent to the server and subsequently to other connected users by entering your message in the text box at the bottom of the window and hitting the send button or the enter key on your keyboard.
11. At this point, feel free to connect as many users as you wish by repeating the steps 3-7 and all users should see the messages sent after connection on their client window.
===============================================================================
To use the software with multiple computers:
The IP address in the ClientWindow.java (set to “localhost”) needs to be changed to the appropriate IP address of where the server is being hosted.
The code for a prompt on clientwindow launch for the IP address can be found commented out in the eclipse project.
The IP address might need to be parsed to the correct data type to get the software to work.
Development of the software was done entirely in the scope of a single computer and so there may still be issues/bugs when trying to use the software over multiple computers, although the software solution should be easily adapatable to work in this context.
===============================================================================

Thank you for using the software, and I hope it serves you well.
-Giles Billenness
