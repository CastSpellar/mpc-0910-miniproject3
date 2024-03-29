package simsim.scheduler;


/**
 * 
 * This is one of classes for creating asynchronous periodic tasks in the
 * simulator.
 * 
 * Typically this class will be used to create anonymous classes by overriding
 * run() to place the code to be executed at given times, in the future.
 * 
 * Periodic tasks repeat execution with a given period (frequency) until
 * canceled.
 * 
 * They can be re-scheduled (often within run()) to execute again at a later time.
 * 
 * Periodic tasks can be canceled to prevent them from executing any further.
 * 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class PeriodicTask extends Task {

    protected double period ;
    protected double jitter ;
	/**
	 * Creates a "named" task that is automatically scheduled to run with a
	 * given frequency/period. When a node is disposed all of its "named" tasks
	 * are canceled as well.
	 * 
	 * @param owner
	 *            The node that issued this task
	 * @param due
	 *            The number of seconds before this task executes for the first
	 *            time.
	 * @param period
	 *            The period of this task.
	 */
	public PeriodicTask(TaskOwner owner, double due, double period) {
		this( owner, due, period, 0 ) ;
	}

	/**
	 * Creates a "named" task that is automatically scheduled to run with a
	 * given frequency/period. When a node is disposed all of its "named" tasks
	 * are canceled as well.
	 * 
	 * @param owner
	 *            The node that issued this task
	 * @param due
	 *            The number of seconds before this task executes for the first
	 *            time.
	 * @param period
	 *            The period of this task.
	 *            
	 * @param jitter
	 *            The jitter introduced to the period of this task measured as a fraction.
	 */
	public PeriodicTask(TaskOwner owner, double due, double period, double jitter) {
		super(owner, due);
		this.jitter = jitter ;
		this.period = period ;
	}
	
	protected void reSchedule() {
		if (! wasReScheduled && !isCancelled) {
			super.reSchedule( Math.max(0, period ));
			wasReScheduled = false ;
		}
	}
}
