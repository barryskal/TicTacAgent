package main;

import java.io.IOException;


/**
 * The Controller class determines which action to take based on the messages received from the game server
 * @author Barry Skalrud
 *
 */
public class Controller {

	private Player currentPlayer;
	private boolean activeGame = true;
	private TCPConnection tcpConnection;
	
	/**
	 * Cretaes a new Controller object
	 */
	public Controller() {}
	
	/**
	 * Sets the TCPConnection object to be used by the controller for sending messages 
	 * to the game server
	 * @param inConnection 	A TCPConnection object that the controller is to use 
	 */
	public void setTCPConnection(TCPConnection inConnection)
	{
		tcpConnection = inConnection;
	}
	
	
	public void setPlayer(Player inPlayer)
	{
		currentPlayer = inPlayer;
	}
	
	/**
	 * Returns whether the game is currently in progress or it has already been won / lost. 
	 * @return	A boolean value identifying whether this game is still in progress. 
	 */
	public boolean isGameActive()
	{
		return activeGame;
	}
	
	/**
	 * Takes a message received from the server and performs the appropriate action. 
	 * @param inMessage The message received from the server
	 * @throws IOException Thrown if there is an issue connecting to the game server.
	 */
	public void translateMessageFromServer(String inMessage) throws IOException 
	{
		if (Agent.debugMode)
			System.out.println("Message from server: '" + inMessage + "'");
		
		if (inMessage.contains("start")) 
		{
			startTheGame(inMessage);
		}
		else if (inMessage.contains("second_move"))
		{
			respondToSecondMove(inMessage);
		}
		else if (inMessage.contains("third_move"))
		{
			respondToThirddMove(inMessage);
		}
		else if (inMessage.contains("next_move"))
		{
			respondToOpponentMove(inMessage);
		}
		else if (inMessage.contains("win") || inMessage.contains("loss"))
		{
			System.out.println(inMessage);
		}
		else if (inMessage.contains("end"))
		{
			endGameKillServer();
		}
		else
		{
			// ignore all other messages
		}
	}

	/**
	 * Using the start command from the server, this method will parse the command to find out what mark is being 
	 * used for this player. It will then start a new game and determine whether this player has the first move.  
	 * @param startMessage The start message from the server. 
	 */
	private void startTheGame(String startMessage) {
		currentPlayer.initiateGame(parseStartCommand(startMessage));
	}
	
	/**
	 * Parses the start command to determine the representation of this player. 
	 * @param inMessage		The String received from the server containing the start command
	 * @return				A positionState value identifying the representation of this player
	 */
	private PositionState parseStartCommand(String inMessage) {
		char representingCharacter = inMessage.charAt(6);
		PositionState playerState = PositionState.getPositionFromGivenValue(representingCharacter);
		return playerState;
	}
	
	/**
	 * Takes the second move message from the server (i.e. the opponent has made the first move) 
	 * and updates the game according to the instructions. Then calls a method to decide 
	 * the next move.
	 * @param secondMoveMessage The second move message, should be of the form second_move(BlockNumber,PositionNumber).
	 * @throws IOException Thrown if there is an issue connecting to the game server.
	 */
	private void respondToSecondMove(String secondMoveMessage) throws IOException
	{
		int blockNumber = Integer.valueOf(secondMoveMessage.substring(12, 13));
		int positionNumber = Integer.valueOf(secondMoveMessage.substring(14, 15));
		
		currentPlayer.makeOpponentMove(blockNumber, positionNumber);
		
		decideOnNextMoveAndSendToServer();
	}
	
	
	/**
	 * 
	 * Takes the third move message from the server and updates the game according to the instructions. Then calls a method to decide 
	 * the next move.
	 * @param thirdMoveMessage The third move message, should be of the form 
	 * third_move(thisPlayersInitialBlockNumber,thisPlayersInitialPositionNumber, opponentsMove).
	 * @throws IOException Thrown if there is an issue connecting to the game server.
	 */
	private void respondToThirddMove(String thirdMoveMessage) throws IOException
	{
		int thisPlayersInitialBlockNumber = Integer.valueOf(thirdMoveMessage.substring(11, 12));
		int thisPlayersInitialPositionNumber = Integer.valueOf(thirdMoveMessage.substring(13, 14));
		
		int opponentsBlockNumber = thisPlayersInitialPositionNumber;
		int opponentsPositionNumber = Integer.valueOf(thirdMoveMessage.substring(15,16));
		
		
		currentPlayer.updateBoardWithPlayerMove(thisPlayersInitialBlockNumber, thisPlayersInitialPositionNumber);
		currentPlayer.makeOpponentMove(opponentsBlockNumber, opponentsPositionNumber);
		
		decideOnNextMoveAndSendToServer();
	}
	
	/**
	 * Takes the next move message from the server and places it on the board as appropriate. Then calls a method to decide 
	 * the next move.
	 * @param moveMessage The position of the opponent's move in the current block. Represented as next_move(position).
	 */
	private void respondToOpponentMove(String moveMessage) throws IOException
	{
		int position = Integer.valueOf(String.valueOf(moveMessage.charAt(10)));
		currentPlayer.makeOpponentMove(position);
		
		decideOnNextMoveAndSendToServer();
	}

	/**
	 * Requests the next move from the player and sends that to the game server
	 */
	private void decideOnNextMoveAndSendToServer() {
		int nextPosition = currentPlayer.decideNextMove();
		
		tcpConnection.sendMoveToServer(String.valueOf(nextPosition));
	}
	
	
	/**
	 * Sets a flag which identifies that this game has ended. 
	 */
	private void endGameKillServer() 
	{
		activeGame = false;
	}
	
	
	
	
}
