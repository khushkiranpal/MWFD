/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package taskGeneration;

import java.util.Random;


/**
 *
 * @author KIRAN
 */
public  class TaskGenerator implements ITaskGenerator {
      private final static long MAX_PERIOD = 10;
	private static int CURRENT_PRIORITY = 1;
	protected final Random random = new Random();
       
    /**
     *
     * @param utilization
     * @param deadlineModel
     * @return
     */
    public ITask generate(double utilization, int deadlineModel, long MAX_PERIOD) {
		long  start = 0;
		long period = nextInt(10, (int) MAX_PERIOD);
		long wcet = Math.max(1, (long) (period * utilization));
		long deadline = period; // implicit deadline
		if (deadlineModel < 0) { // constrained deadline
			deadline = nextInt((int) wcet, (int) period);
		} else if (deadlineModel > 0) { // arbitrary deadline
			deadline = nextInt(1, (int) MAX_PERIOD);
		}
		
		int priority = CURRENT_PRIORITY++;
		if (CURRENT_PRIORITY > 50)
			CURRENT_PRIORITY = 1;
                
         return newInstance(start, wcet, period, deadline, priority);
	}
    
    public ITask generate(double utilization, int deadlineModel) {
		long  start = 0;
		long period = nextInt(1, (int) MAX_PERIOD);
		long wcet = Math.max(1, (long) (period * utilization));
		long deadline = period; // implicit deadline
		if (deadlineModel < 0) { // constrained deadline
			deadline = nextInt((int) wcet, (int) period);
		} else if (deadlineModel > 0) { // arbitrary deadline
			deadline = nextInt(1, (int) MAX_PERIOD);
		}
		
		int priority = CURRENT_PRIORITY++;
		if (CURRENT_PRIORITY > 50)
			CURRENT_PRIORITY = 1;
                
         return newInstance(start, wcet, period, deadline, priority);
	}
    
    public ITask generateSporadic ( int deadlineModel, double utilization, double parameter, int periodDistribution )
    {
    	
    	long period = 0 ;
    	
  //  	UniformRealDistribution uniPeriod = new UniformRealDistribution (1,1000);
    	
    	long  start = 0;
    	    	
    	if (periodDistribution<0)
    		period = nextInt(1, (int) MAX_PERIOD);
    	System.out.println("period    "+period);
    	
		long wcet = Math.max(1, (long) (period * utilization));
//		System.out.println(" wcet     "+wcet);
		
		long deadline = period; // implicit deadline
		if (deadlineModel < 0) { // constrained deadline
			{
				deadline = nextInt((int) wcet, (int) period);
//				System.out.println("Deadline    "+deadline);
			}
		} else if (deadlineModel > 0) { // arbitrary deadline
			deadline = nextInt(1, (int) MAX_PERIOD);
	}
		
		int priority = CURRENT_PRIORITY++;
		if (CURRENT_PRIORITY > 50)
			CURRENT_PRIORITY = 1;
    	
    	return newInstance(start, wcet, period, deadline, priority);
    	
    }

                	
                public ITask newInstance(Object... params) {
		ITask iTask = null;

		long arrival;
		long wcet;
		//long wcee;
		long period;
		long deadline;
		int priority;
		
		
			arrival = Long.parseLong(params[0].toString());
			wcet = Long.parseLong(params[1].toString());
		//	wcee = Long.parseLong(params[2].toString());
			period = Long.parseLong(params[2].toString());
			deadline = Long.parseLong(params[3].toString());
			priority = Integer.parseInt(params[4].toString());
				iTask = new Task(arrival,wcet,period,deadline,priority);
		return iTask;
	}

	
        protected int nextInt(int from, int to) {
		return from + random.nextInt(to - from + 1);
                }

}
