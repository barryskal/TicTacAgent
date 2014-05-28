package main;

/**
 * 
 * This theory behind this program is to use a minimax algorithm with alpha-beta 
 * pruning to determine the best possible move for the computer player. 
 * 
 * The structure of the program is broken up in to several classes that describe 
 * the current state of the game. 
 * 
 * The Player class represents the overall logic of the computer player
 * The Game class is a representation of a game. There could be several instances
 * of the game class as each one would represent a different state on the 
 * minimax tree.
 * The Block class is a representation of an individual tic-tac-toe board, i.e. 
 * 1 of 9 in a game. 
 * The TCPConnection class represents the connection between this game and the 
 * game server
 * The Controller class acts as the intermediate body between the TCP connection 
 * (i.e. incoming and outgoing messages) and the Player
 * The PositionState enum is a representation of the state of each cell in a block. 
 * 
 * Searching the game to completion is difficult; therefore, a heuristic is used to 
 * determine which moves are beneficial. The basic idea behind this heuristic is 
 * to determine how many cells have been left in a state where the Player has
 * the upper hand. By mnaximising this value, the opponent will have fewer options
 * to continue play.
 * 
 * The overall search depth of this program is 13 moves. However, it does not 
 * achieve this depth immediately it is ramped up to this level as described in 
 * the Player class. 
 * 
 * To speed up the search, the heuristic values calculated for each Block 
 * configuration are stored in a hash table. This reduces by by allowing the 
 * program to look up the heuristic value rather than recalculate repeatedly.
 * 
 * @author Barry Skalrud
 *
 */
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
			Controller gameController = new Controller();
			gameController.setPlayer(new Player());
			TCPConnection server = new TCPConnection(port, gameController);
			server.startServer();
			
		} catch (Exception e) 
		{
				e.printStackTrace();
				System.out.println("Progran Error: " + e.getMessage());
		}
		
			
	}
		
}


