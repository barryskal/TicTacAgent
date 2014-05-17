package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPConnection {

	private Socket clientSocket;
	private Controller gameController;
	private int serverPort;

	public TCPConnection(int inPort, Controller inController) throws Exception {
		serverPort = inPort;
		gameController = inController;
		try {
			clientSocket = new Socket("localhost", inPort);
			gameController.setTCPConnection(this);
		} catch (IOException e) {
			throw new Exception("Unable to connect to server at port " + inPort);
		}
	}

	public void startServer() throws IOException {

		try {
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			while (gameController.isGameActive()) {
				String newLine = inFromServer.readLine();
				gameController.translateMessageFromServer(newLine);
				System.out.println("I'm here");
			}
			clientSocket.close();
		} catch (IOException e) {
			throw new IOException("Issue with TCP connection, exiting");
		} 

	}
	
	/**
	 * Sends a string to the game server. In all cases this should be a move consisting of a single character.
	 * @param outMove		The message to be sent to the server, e.g. "4"
	 * @throws IOException	Exception thrown if there is an issue with sending data to the server. 
	 */
	public void sendMoveToServer(String outMove) throws IOException 
	{
		if (Agent.debugMode)
			System.out.println("Message sent to server: " + outMove);
		
		try 
		{
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(outMove + '\n');
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new IOException("Unable to send messgae:" +  outMove + ", to server");
		}
	}

}
