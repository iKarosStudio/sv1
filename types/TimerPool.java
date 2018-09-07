package vidar.types;

import java.util.*;

public class TimerPool
{
	private Timer[] _timer;
	private int poolSize;
	private int timerIndex = 0;
	
	public TimerPool (int pool_size) {
		_timer = new Timer[pool_size];
		for (int p = 0; p < pool_size; p++) {
			_timer[p] = new Timer () ;
		}
		
		poolSize = pool_size;
	}
	
	public synchronized Timer getTimer () {
		if (timerIndex >= poolSize) {
			timerIndex = 0;
		}
		
		return _timer[timerIndex++];
	}
	
	public synchronized Timer getTimer (int t) {
		return _timer[t];
	}
}
