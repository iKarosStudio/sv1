package vidar.game.model.monster.ai;

import java.util.*;
import java.util.concurrent.*;

/* AI工作要求Queue */
public class MonsterAiQueue
{
	private static MonsterAiQueue instance;
	private static Queue<Runnable> taskQueue;
	
	public static MonsterAiQueue getInstance () {
		if (instance == null) {
			instance = new MonsterAiQueue ();
		}
		return instance;
	}
	
	public MonsterAiQueue () {
		System.out.printf ("AI Task queue initializing...") ;
		taskQueue = new ConcurrentLinkedQueue<Runnable> () ;
		System.out.printf ("success\n") ;
	}
	
	public int getQueueSize () {
		return taskQueue.size ();
	}
	
	public synchronized Queue<Runnable> getQueue () {
		return taskQueue;
	}
}
