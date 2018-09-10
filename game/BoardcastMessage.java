package vidar.game;

import vidar.server.process_server.*;

/*
 * 遊戲內的定時廣播訊息
 */
public class BoardcastMessage extends Thread implements Runnable
{
	private static BoardcastMessage instance;
	private static Vidar vidar;
	
	public void run () {
		String message = String.format ("循環廣播訊息測試") ;
		//System.out.println ("[伺服器廣播]" + BoardcastMessage) ;
		vidar.boardcastToAllPc (new SystemMessage (message).getRaw ());
	}
	
	public static BoardcastMessage getInstance () {
		if (instance == null) {
			instance = new BoardcastMessage () ;
		}
		return instance;
	}
	
	public BoardcastMessage () {
		vidar = Vidar.getInstance () ;
	}
}
