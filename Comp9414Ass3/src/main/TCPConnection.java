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

	
	public TCPConnection(int inPort, Controller inController)  {
		serverPort = inPort;
		gameController = inController;
		gameController.setTCPConnection(this);
	}

	/**
	 * Starts the TCP server, which connects to the port provided when the TCPConnection
	 * object was created. 
	 * @throws IOException Exception thrown if there is an IO issue with the server. 
	 */
	public void startServer() throws IOException {

		try 
		{
			clientSocket = new Socket("localhost", serverPort);
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			while (gameController.isGameActive()) {
				String newLine = inFromServer.readLine();
				gameController.translateMessageFromServer(newLine);
			}
		} 
		catch (IOException e) 
		{
			throw new IOException("Issue with TCP connection, exiting");
		} 
		finally
		{
			if (clientSocket != null)
				clientSocket.close();
			
		}

	}
	
	/**
	 * Sends a string to the game server. In all cases this should be a move consisting of a single character.
	 * @param outMove		The message to be sent to the server, e.g. "4"
	 * @throws IOException	Exception thrown if there is an issue with sending data to the server. 
	 */
	public void sendMoveToServer(String outMove)  
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
			System.out.println("Unable to send messgae:" +  outMove + ", to server");
		}
	}

}
