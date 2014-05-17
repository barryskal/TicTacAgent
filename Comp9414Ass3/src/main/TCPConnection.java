package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPConnection implements Runnable {
	
	private Socket clientSocket;
	private Controller gameController;
	
	public TCPConnection(int inPort, Controller inController) throws Exception 
	{
		gameController = inController;
		try {
			clientSocket = new Socket("localhost", inPort);
		} catch (IOException e) {
			throw new Exception("Unable to connect to server at port " + inPort);
		}
	}
	
	public void run()
	{
		try {
			while (true) {
				if (Thread.interrupted())
				{
					// respond to interrupted action
					break;
				}
				
				gameController.translateMessageFromServer(readLineFromServer().readLine());				
			}
			
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		
	}

	private BufferedReader readLineFromServer() throws IOException {
		try 
		{
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			return inFromServer;
		}
		catch (IOException e) 
		{
			throw new IOException("Issue with TCP connection, exiting");
		}
	}
	
	
	
	
}
