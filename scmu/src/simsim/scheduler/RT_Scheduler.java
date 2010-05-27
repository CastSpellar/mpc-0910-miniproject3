package simsim.scheduler;

import simsim.utils.*;

public class RT_Scheduler<T extends Task> extends VT_Scheduler<T> {

	protected RT_Scheduler() {
		super();
	}

	@SuppressWarnings("unchecked")
	Task schedule(Task t, double due) {
		assert !t.isCancelled && !t.isQueued;
		t.due = now() + Math.max(1e-9, due);
		synchronized (queue) {
			queue.add((T) t);
			t.isQueued = true;
			Threading.notifyOn(queue);
		}
		return t;
	}

	public double now() {
		return super.rt_now() ;
	}
	
	
	public void start() {
		Threading.newThread(this, false).start() ;		
	}
	
	@SuppressWarnings("unchecked")
	void reSchedule(Task t, double due) {
		assert !t.isCancelled;

		synchronized (queue) {
			if (t.isQueued)
				queue.remove(t);
			t.due = now() + Math.max(1e-9, due);

			queue.add((T) t);
			t.isQueued = true;
			Threading.notifyOn(queue);
		}
	}

	
	public void run() {
        System.err.println("Task scheduler starting...");

		final double MIN_WAIT = 1 ;
        while( ! stopped ) {                    
        	T task ;
            synchronized( queue ) {   
            	while( queue.isEmpty() )
            		Threading.waitOn( queue ) ;
            	
            	task = queue.peek() ;
            	double w = 1000 * (task.due - rt_now()) ;
            	if( w > MIN_WAIT ) {
            		Threading.waitOn( queue, (int)w ) ;
                    continue ;
                }
                task = queue.remove() ;
            }
            
            if( ! task.isCancelled ) {
            	try {
            		task.reset() ;
            		task.run() ;
                	task.reSchedule() ; 
            	} catch( Exception x ) { 
        			System.err.printf("Offending task cancelled...[%s]\n", task.getClass());
            		x.printStackTrace() ; }
            }
        }
        System.err.println("Task scheduler stopping...");
    }
}
