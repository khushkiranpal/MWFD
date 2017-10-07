/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package taskGeneration;

import java.util.Comparator;
import java.util.PriorityQueue;

import platform.Processor;

/**
 *
 * @author KIRAN
 */
public  class Task implements ITask {
        private long id;
	private final long C;
	private final long D;
	private final long T;
    private long arrival;
	private  long WCET_orginal;
	private long wcet;
	private double BCET;
	private double Best_CET;
	private double average_CET;
	private double ACET;
	//private final long wcee;
	private  long period;
	private  long deadline;
	private int priority;
	private int nextJobId = 1;
	private static long count=0;
    private Job lastExecutedJob;
    private boolean preemptive = true;
    private float u =0;
  	private int type=-1;  // 1 for heavy weight or -1 light weight
    private double Slack=0;
    private double ResponseTime;
   private boolean is_Schedulabe=true;
	private long finishTime=1;   //finishTime for mpn-EDf eq 4
	// ENERGY PARAMETERS 
	private double energy_consumed ;
	private double frequency;
	private double voltage;
	
	//PROCESSOR
		private Processor p, backupProcessor, primaryProcessor;  //to know primaryProcessor in case of backup task
	    private boolean primary;  // true for primary, false for secondary

	
	public Task(long arrival, long wcet, long period,long deadline, int priority) {
		
		this.C = wcet;
		this.D = deadline;
		this.T = period;
		this.arrival = arrival;
		this.wcet = wcet;
		this.WCET_orginal = wcet;
		this.period = period;
		this.deadline = deadline;
		this.priority = priority;
		//this.id  = id;
		this.id = ++count;
	}
	
	// FOR rms_DOUBLE Task(arrival,id, wcet,period, deadline,  priority,ACET,BCET );
	public Task(long arrival, long id, long wcet, long period,long deadline, int priority, double ACET, double BCET) {
		
		this.C = wcet;
		this.D = deadline;
		this.T = period;
		this.arrival = arrival;
		this.wcet = wcet;
		this.WCET_orginal = wcet;
		this.period = period;
		this.deadline = deadline;
		this.priority = priority;
		//this.id  = id;
		this.id = ++count;
		this.ACET = ACET;
		this.BCET = BCET;
		
	}

	// FOR rmsMWFD_RMS_EEPS Task(arrival,id, wcet,period, deadline,  priority,ACET,BCET );
		public Task(long arrival, long id,long wcet, long period,long deadline, int priority,double Slack,  double ACET, 
				double BCET	, Processor p, boolean primary,Processor backupProcessor) {
			
			this.C = wcet;
			this.D = deadline;
			this.T = period;
			this.arrival = arrival;
			this.wcet = wcet;
			this.WCET_orginal = wcet;
			this.period = period;
			this.deadline = deadline;
			this.priority = priority;
			this.id  = id;
			//this.id = ++count;
			this.ACET = ACET;
			this.BCET = BCET;
			this.Slack = Slack;
			
		}
	public Task(long arrival,long id, long wcet, long period,long deadline, int priority) {
		
		this.C = wcet;
		this.D = deadline;
		this.T = period;
		this.arrival = arrival;
		this.wcet = wcet;
		this.WCET_orginal = wcet;
		this.period = period;
		this.deadline = deadline;
		this.priority = priority;
		this.id  = id;
		//this.id = ++count;
	}
	
public Task(long arrival,long id, long wcet, long period,long deadline, int priority, float u) {
		
	this.C = wcet;
	this.D = deadline;
	this.T = period;
	this.arrival = arrival;
		this.wcet = wcet;
		this.WCET_orginal = wcet;
		this.period = period;
		this.deadline = deadline;
		this.priority = priority;
		this.id  = id;
		this.u=u;
		//this.id = ++count;
	}

     
/**
 * @return the primary
 */
public boolean isPrimary() {
	return primary;
}

/**
 * @param primary the primary to set
 */
public void setPrimary(boolean primary) {
	this.primary = primary;
}

	/**
 * @return the p
 */
public Processor getP() {
	return p;
}

/**
 * @param p the p to set
 */
public void setP(Processor p) {
	this.p = p;
}



	/**
 * @return the primaryProcessor
 */
public Processor getPrimaryProcessor() {
	return primaryProcessor;
}

/**
 * @param primaryProcessor the primaryProcessor to set
 */
public void setPrimaryProcessor(Processor primaryProcessor) {
	this.primaryProcessor = primaryProcessor;
}

	/**
 * @return the backupProcessor
 */
public Processor getBackupProcessor() {
	return backupProcessor;
}

/**
 * @param backupProcessor the backupProcessor to set
 */
public void setBackupProcessor(Processor backupProcessor) {
	this.backupProcessor = backupProcessor;
}

	/**
 * @return the best_CET
 */
public double getBest_CET() {
	return Best_CET;
}

/**
 * @param best_CET the best_CET to set
 */
public void setBest_CET(double best_CET) {
	Best_CET = best_CET;
}

/**
 * @return the average_CET
 */
public double getAverage_CET() {
	return average_CET;
}

/**
 * @param average_CET the average_CET to set
 */
public void setAverage_CET(double average_CET) {
	this.average_CET = average_CET;
}

	/**
 * @return the c
 */
public long getC() {
	return C;
}

/**
 * @return the d
 */
public long getD() {
	return D;
}

/**
 * @return the t
 */
public long getT() {
	return T;
}

	/**
 * @param wCET_orginal the wCET_orginal to set
 */
public void setWCET_orginal(long wCET_orginal) {
	WCET_orginal = wCET_orginal;
}

/**
 * @param period the period to set
 */
public void setPeriod(long period) {
	this.period = period;
}

/**
 * @param deadline the deadline to set
 */
public void setDeadline(long deadline) {
	this.deadline = deadline;
}

	/**
 * @return the bCET
 */
public double getBCET() {
	return BCET;
}

/**
 * @param bCET the bCET to set
 */
public void setBCET(double bCET) {
	BCET = bCET;
}

/**
 * @return the aCET
 */
public double getACET() {
	return ACET;
}

/**
 * @param aCET the aCET to set
 */
public void setACET(double aCET) {
	ACET = aCET;
}

	/**
 * @return the wCET_orginal
 */
public long getWCET_orginal() {
	return WCET_orginal;
}

	/**
	 * @param wcet the wcet to set public void setWcet(long wcet);
	 */
	public void setWcet(long wcet) {
		this.wcet = wcet;
	}
	
	public void setWcet(Double wcet) {
		this.wcet = wcet.longValue();
	}

	/**
 * @param wcet the wcet to set
 */


	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
        public long getArrival() {
		return arrival;
	}
	public void setArrival(long arrival){
		this.arrival = arrival;
	}
	
	public long getWcet() {
		return wcet;
	}

	public long getPeriod() {
		return period;
	}

	public long getDeadline() {
		return deadline;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setPreemptive(boolean x)
	{
		preemptive= x;
	}
	
	public boolean getIsPreemptive()
	{
		return preemptive;
	}
       
        public ITask cloneTask() {
		return new Task(arrival,id, C,  T, D,  priority);
	}
        
        public ITask cloneTask_RMS_double() {
    		return new Task(arrival,id, WCET_orginal,period, deadline,  priority,ACET,BCET );
    	}
    
        public ITask cloneTask_MWFD_RMS_EEPS() {
    		return new Task(arrival,id, WCET_orginal,period, deadline,  priority,Slack,ACET,BCET,p,primary, backupProcessor );
    	}
        
	private final PriorityQueue<Job> activeJobs = new PriorityQueue<Job>(2,
			new Comparator<Job>() {
				public int compare(Job j1, Job j2) {
					return (int) (j1.getActivationDate() - j2.getActivationDate());
				}
			});
	
	public void addactivatedjob(Job j)
	{
		activeJobs.add(j);
	}


	//@Override
	public PriorityQueue<Job> getActiveJobs() {
		return activeJobs;
	}

	public boolean isActive() {
		return !getActiveJobs().isEmpty();
	}
        
       

	public  Job activate(long time) {
                JobId jobId = new JobId(this.getId(),nextJobId++);
         //       System.out.println("in task     "+"job id "+jobId.getJobId()+"  task id  " + jobId.getTaskId());
		 Job job = new  Job(jobId, time, WCET_orginal,wcet, time + deadline,preemptive, type);
		//getActiveJobs().add(job);
                //return job;
		return job;
	}

	public  Job activateRMS(long time) {
        JobId jobId = new JobId(this.getId(),nextJobId++);
        //       System.out.println("in task     "+"job id "+jobId.getJobId()+"  task id  " + jobId.getTaskId());
        	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period);
        		//getActiveJobs().add(job);
        		//return job;
        		return job;
		}
	public  Job activateRMS_energy(long time) {
        JobId jobId = new JobId(this.getId(),nextJobId++);
        //       System.out.println("in task     "+"job id "+jobId.getJobId()+"  task id  " + jobId.getTaskId());
        	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period, frequency, (long)(Slack+ time));
        		//getActiveJobs().add(job);
        		//return job;
        		return job;
		}
	
	//
	public  Job activateRMS_energy_ExecTime(long time) {
        JobId jobId = new JobId(this.getId(),nextJobId++);
        //       System.out.println("in task     "+"job id "+jobId.getJobId()+"  task id  " + jobId.getTaskId());
        	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period, frequency, (long)(Slack+ time), 
        			BCET, ACET,Best_CET,average_CET);
        		//getActiveJobs().add(job);
        		//return job;
        		return job;
		}
	
	public  Job activate_MWFD_RMS_EEPS(long time) {
        JobId jobId = new JobId(this.getId(),nextJobId++);
        //       System.out.println("in task     "+"job id "+jobId.getJobId()+"  task id  " + jobId.getTaskId());
        	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period, frequency, (long)(Slack+ time), 
        			BCET, ACET,Best_CET,average_CET, p,primary, backupProcessor,primaryProcessor);
        		//getActiveJobs().add(job);
        		//return job;
        		return job;
		}
	
	public  Job getCurrentJob() {
		return getActiveJobs().peek();
	}

	public boolean checkDeadlineMissed(long time) {
		 Job job = getCurrentJob();
		if (job == null)
			return false;
		if (job.getRomainingTimeCost() > 0 && time >= job.getAbsoluteDeadline())
			return true;

		return false;
	}

	public boolean lastExecutedJobHasCompleted() {
		return getLastExecutedJob() != getActiveJobs().peek();

	}

	public long getRemainingCost() {
		if (getCurrentJob() != null)
			return getCurrentJob().getRomainingTimeCost();
		return 0;
	}

	public long getNextDeadline(long time, boolean nextPeriod) {
		if (time <  arrival)
			return  arrival + deadline;

		long currentPeriod = (time -  arrival) / period;

//		if (nextPeriod && getCurrentJob() != null
//				&& getCurrentJob().getRomainingTimeCost() == 0)
		if(nextPeriod && getRemainingCost()==0)
			currentPeriod++;

		return  arrival + currentPeriod * period + deadline;
	}

	public long getNextActivation(long time) {
		if (time <  arrival)
			return  arrival;
		return  arrival + (time / period) * period + period;
	}

	public boolean isActivationTime(long time) {
		if (time <  arrival)
			return false;
		return (time -  arrival) % period == 0;
	}

	public long getNextAbsoluteDeadline(long time) {
		long currentPeriod = (time -  arrival) / period;
		return  arrival + currentPeriod * period + deadline;
	}

	public long getPreviousAbsoluteDeadline(long time) {
		long currentPeriod = time / period;
		long t;
		do {
			if (currentPeriod < 0)
				return -1;
			t = arrival + currentPeriod * period + deadline;
			currentPeriod--;
		} while (t >= time);

		return t;
	}

	public  Job getLastExecutedJob() {
		return lastExecutedJob;

	}

	public void setLastExecutedJob( Job lastExecutedJob) {
		this.lastExecutedJob = lastExecutedJob;

	}
	 /**
		 * @return the type
		 */
		public int getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(int type) {
			this.type = type;
		}

		  /**
		 * @return the u
		 */
		public float getU() {
			return u;
		}
		/**
		 * @param u the u to set
		 */
		public void setU(float u) {
			this.u = u;
		}
		/**
		 * @return the slack
		 */
		public double getSlack() {
			return Slack;
		}
		/**
		 * @param slack the slack to set
		 */
		public void setSlack(double slack) {
			Slack = slack;
		}

		/**
		 * @return the responseTime
		 */
		public double getResponseTime() {
			return ResponseTime;
		}

		/**
		 * @param responseTime the responseTime to set
		 */
		public void setResponseTime(double responseTime) {
			ResponseTime = responseTime;
		}

		/**
		 * @return the is_Schedulabe
		 */
		public boolean isIs_Schedulabe() {
			return is_Schedulabe;
		}

		/**
		 * @param is_Schedulabe the is_Schedulabe to set
		 */
		public void setIs_Schedulabe(boolean is_Schedulabe) {
			this.is_Schedulabe = is_Schedulabe;
		}
		/**
		 * @return the finishTime for mpn-EDf eq 4
		 */
		public long getFinishTime() {
			return finishTime;
		}
       
		
		
		
		/**
		 * @return the energy_consumed
		 */
		public double getEnergy_consumed() {
			return energy_consumed;
		}

		/**
		 * @param energy_consumed the energy_consumed to set
		 */
		public void setEnergy_consumed(double energy_consumed) {
			this.energy_consumed = energy_consumed;
		}

		/**
		 * @return the frequency
		 */
		public double getFrequency() {
			return frequency;
		}

		/**
		 * @param frequency the frequency to set
		 */
		public void setFrequency(double frequency) {
			this.frequency = frequency;
		}

		/**
		 * @return the voltage
		 */
		public double getVoltage() {
			return voltage;
		}

		/**
		 * @param voltage the voltage to set
		 */
		public void setVoltage(double voltage) {
			this.voltage = voltage;
		}

		/**
		 * @param finishTime the finishTime to set
		 */
		public void setFinishTime(long finishTime) {
			this.finishTime = finishTime;
		}

		
}
