package simsim.scheduler;

import java.util.*;
import java.util.concurrent.*;

import simsim.utils.*;

/**
 * The core engine of the simulation engine. The Scheduler manages a priority
 * queue of tasks, issuing them when simulation time reaches their time of
 * execution.
 * 
 * New tasks and re-scheduled tasks are inserted into the priority queue and
 * sorted according to the task execution deadlines. In each iteration, the
 * scheduler picks the next task to execute and advances simulation time
 * accordingly. Therefore, simulation time advances in discrete steps and is
 * decoupled from real time. Depending on the number of tasks in the queue and
 * the time spent in their execution, simulation time can run faster or slower
 * than real time.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class VT_Scheduler<T extends Task> implements Runnable {

	static VT_Scheduler<?> Scheduler;
	protected boolean stopped = false;

	protected VT_Scheduler() {
		Scheduler = this;
		queue = new PriorityQueue<T>();
	}

	/**
	 * Stops the simulator, preventing further tasks to be executed...
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * Starts the simulator and begins executing tasks in order.
	 */
	public void start() {
		Threading.newThread( this, true).start() ;
		Threading.waitOn( this ) ;
	}

	/**
	 * Returns the number of simulation seconds that elapsed since the
	 * simulation started.
	 * 
	 * @return The number of simulation seconds that elapsed since the
	 *         simulation started.
	 */
	public double now() {
		return now ;
	}

	/**
	 * Returns the number of realtime seconds that elapsed since the simulation
	 * started.
	 * 
	 * @return The number of realtime seconds that elapsed since the simulation
	 *         started.
	 */
	public double rt_now() {
		return (System.nanoTime() - rt0) * 1e-9;
	}

	/**
	 * Cancels all tasks in the scheduler queue, effectively ending the
	 * simulation.
	 */
	public void cancelAll() {
		for (Task i : queue)
			i.cancel();
	}

	/**
	 * Inserts a new task in the scheduler queue with a given execution
	 * deadline.
	 * 
	 * @param t
	 *            The task to be scheduled.
	 * @param due
	 *            The execution deadline of the task, relative to the current
	 *            simulation time.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Task schedule(Task t, double due) {
		assert !t.isCancelled && !t.isQueued;
		t.due = now() + Math.max(1e-9, due);
		queue.add((T) t);
		t.isQueued = true;

		return t;
	}

	/**
	 * Re-inserts a task back into the scheduler queue with an updated execution
	 * deadline.
	 * 
	 * @param t
	 *            The task to be re-scheduled.
	 * @param due
	 *            The new execution deadline of the task, relative to the
	 *            current simulation time.
	 */
	@SuppressWarnings("unchecked")
	void reSchedule(Task t, double due) {
		assert !t.isCancelled;

		if (t.isQueued)
			queue.remove(t);
		t.due = now() + Math.max(1e-9, due);

		queue.add((T) t);
		t.isQueued = true;
	}

	/*
	 * (non-Javadoc) The main loop of the scheduler. In each iteration the next
	 * task is picked for execution. Additionally, from time to time, a call is
	 * made to the Gui to check if there are any pending graphical elements that
	 * need to be rendered. Gui (backbuffer) rendering is thread-safe if it is
	 * done by the (current) thread running in the scheduler.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			mainLoop() ;			
		} catch( KillThreadException x) {}

		if( stopped )
			Threading.notifyOn(this) ;
	}

	protected void mainLoop() {
		while (!queue.isEmpty() && !stopped) {
			processNextTask();
		}
		stopped = true;		
	}
	/**
	 * 
	 * Resumes the execution of one of the ready threads (previously blocked in
	 * a network I/O operation) or picks the next task to execute.
	 * 
	 */
	protected void processNextTask() {

		threadManager.relieve();

		T next = queue.remove();
		if (next == null || next.isCancelled)
			return;

		now = next.due;
		rt1 = System.nanoTime();		
		try {
			next.reset();
			next.run();
			next.reSchedule();
		} catch (Exception x) {
			System.err.println("Offending task cancelled...");
			x.printStackTrace();
		}
	}

	public boolean isStopped() {
		return stopped;
	}

	protected static double now = 0;
	protected final Queue<T> queue;
	protected double rt1 = System.nanoTime(), rt0 = System.nanoTime();
	protected final ThreadManager threadManager = new ThreadManager();

	public Token getToken() {
		return threadManager.newToken();
	}

	/**
	 * This class manages the threads used in the simulation. The invariant
	 * observed is that only one scheduler thread is executing at a given time.
	 * Therefore, there is no need for synchronization of data structures.
	 * 
	 * The simulation uses additional threads for the GUI, but all the drawing
	 * is actually done to a back buffer image by the scheduler thread currently
	 * running.
	 * 
	 * This manager is required to implement blocking network operations. When a
	 * blocking io operation is performed within a task, the current (scheduler)
	 * thread is blocked until the following read is ready, in the mean time,
	 * the scheduler picks a previously blocked thread to continue execution or
	 * picks other tasks for execution.
	 * 
	 * @author Sérgio Duarte (smd@di.fct.unl.pt)
	 * 
	 */
	protected class ThreadManager {

		private LinkedList<Token> spareThreads = new LinkedList<Token>();
		private LinkedList<Token> readyThreads = new LinkedList<Token>();
		private LinkedList<Token> waitingThreads = new LinkedList<Token>();

		Token newToken() {
			return new Token();
		}

		void relieve() {
			if (readyThreads.size() > 0) {
				if( spareThreads.size() < 2 ) {
					Token t = new Token();
					spareThreads.addLast(t);
					readyThreads.removeFirst().release();
					t.acquireUninterruptibly();
				} else {
					readyThreads.removeFirst().release();
					throw new KillThreadException() ;
				}
			}
		}

		private void release(Token token) {
			readyThreads.addLast(token);
			waitingThreads.remove(token);
		}

		private void acquire(Token token) {
			waitingThreads.add(token);
			if (readyThreads.size() > 0)
				readyThreads.removeFirst().release();
			else if (spareThreads.size() > 0)
				spareThreads.removeFirst().release();
			else {
				Threading.newThread(Scheduler, true).start();
			}
			token.acquireUninterruptibly();
		}

		public String toString() {
			int total = 1 + readyThreads.size() + waitingThreads.size() + spareThreads.size();
			return String.format("Threads: (T:%d)<R:%d/W:%d/S:%d>\n", total, readyThreads.size(), waitingThreads.size(), spareThreads.size());
		}
	}

	@SuppressWarnings("serial")
	public class Token extends Semaphore {

		Token() {
			super(0);
		}

		public void block() {
			threadManager.acquire(this);
		}

		public void unblock() {
			threadManager.release(this);
		}
	}
}

class KillThreadException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
