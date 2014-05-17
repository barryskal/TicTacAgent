package main;

public class Agent {

	public static boolean debugMode = false;
	
	
	public static void main(String[] args) {
		if (args.length < 2 || !args[0].equals("-p")) {
			System.out.println("Usage: Agent -p port");
		}
		
		
		int port = Integer.valueOf(args[1]);
		
		if (args.length > 2 && args[2].equals("-d"))
			debugMode = true;
			
		
		try {
			Controller gameController = new Controller(port);
			gameController.setPlayer(new Player(gameController));
			TCPConnection server = new TCPConnection(port, gameController);
			Thread TCPServerThread = new Thread(server);
			gameController.setTCPConnectionThread(TCPServerThread);
			TCPServerThread.start();
			
		} catch (Exception e) 
		{
				System.out.println(e.getMessage());
		}
		
		System.out.println("Game Complete, exit");
			
	}
		
}


