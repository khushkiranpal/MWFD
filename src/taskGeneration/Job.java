
package taskGeneration;

import java.util.ArrayList;
import java.util.PriorityQueue;

import platform.Processor;

/**
 * @author kiran
 *
 */
public class Job {
	
	
	/**
	 * A job should only be obtain by method Task.activate()
	 * @param id
	 * @param time 
	 * @param wcet
	 * @param d
	 */
	public Job(JobId id, long time, long remainingTimeCost, long wcet, long d, boolean preemptive, int type) { 
		jobId= id;
		this.remainingTimeCost = remainingTimeCost;
		remainingTime = wcet;
		this.deadline= d;
		this.activationDate = time;
		absoluteDeadline = d;
		isPreemptive= preemptive;
		this.type = type;
		
		
	}
	public Job(JobId id, long activationDate,long remainingTimeCost, long wcet, long absoluteDi, long period) { 
		jobId= id;
		this.remainingTimeCost = remainingTimeCost;
		remainingTime = wcet;
		this.deadline= absoluteDi;
		this.activationDate = activationDate;
		absoluteDeadline = absoluteDi;
		this.period = period;
		
	}
	
	public Job(JobId id, long activationDate2,long remainingTimeCost, long wcet, long absoluteDeadline2, long period, double freq, long promotionTime) { 
		jobId= id;
		this.remainingTimeCost = remainingTimeCost;
		remainingTime = wcet;
		this.deadline= absoluteDeadline2;
		this.activationDate = activationDate2;
		absoluteDeadline = absoluteDeadline2;
		this.period = period;
		this.frequency = freq;
		this.promotionTime= promotionTime;
	}
        
//	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period, frequency, (long)(Slack+ time), BCET, ACET,Best_CET,average_CET);
    
	
	public Job(JobId id, long activationDate2,long remainingTimeCost, long wcet, long absoluteDeadline2, long period, 
			double freq, long promotionTime, double BCET, double ACET, double Best_CET, double average_CET ) { 
		jobId= id;
		this.remainingTimeCost = remainingTimeCost;//(long)average_CET; ////////////////remainingTimeCost;//
		remainingTime = wcet;//(long)ACET;  ////////////////wcet;//
		this.deadline= absoluteDeadline2;
		this.activationDate = activationDate2;
		absoluteDeadline = absoluteDeadline2;
		this.period = period;
		this.frequency = freq;
		this.promotionTime= promotionTime;
		this.BCET =  (long)BCET;
		this.ACET =  (long)ACET;
		this.Best_CET = Best_CET;
		this.average_CET= average_CET;
		
	}
	
	
    
//	Job job = new  Job(jobId, time, WCET_orginal, wcet, time + deadline, period, frequency, (long)(Slack+ time), BCET, ACET,Best_CET,average_CET);
 //for MWFD
	
	public Job(JobId id, long activationDate2,long remainingTimeCost, long wcet, long absoluteDeadline2, long period, 
			double freq, long promotionTime, double BCET, double ACET, double Best_CET, double average_CET
			, Processor p, boolean primary,Processor backupProcessor) { 
		jobId= id;
		this.remainingTimeCost = remainingTimeCost;//(long)average_CET; ////////////////remainingTimeCost;//
		remainingTime = wcet;//(long)ACET;  ////////////////wcet;//
		this.deadline= absoluteDeadline2;
		this.activationDate = activationDate2;
		absoluteDeadline = absoluteDeadline2;
		this.period = period;
		this.frequency = freq;
		this.promotionTime= promotionTime;
		this.BCET =  (long)BCET;
		this.ACET =  (long)ACET;
		this.Best_CET = Best_CET;
		this.average_CET= average_CET;
		this.p = p;
		this.primary = primary;
		this.backupProcessor= backupProcessor;

		
	}
	private long remainingTimeCost; //original wcet
	private long remainingTime ;  //  wcet/freq
	private long remainingEnergyCost;
	JobId jobId;
	private int priority;
	private long promotionTime;
	private long period;
	private final long activationDate;
	private final long absoluteDeadline;
	private long deadline; // temporary or tentative deadline for heavy tasks
	private long finishTime;
	private long startTime;
	private long endTime;
	public  int NoOfPreemption=0;
	public boolean isActive; //NOT USED
	public boolean isPreempted= false;
	private boolean isPreemptive;
	// ENERGY PARAMETERS
	private double energy_consumed ;
	private double frequency;
	private double voltage;
	private double extended_exec_time;
	private boolean completionSuccess=false;
	private boolean faulty= false;
	private double BCET;
	private double ACET;
	private double Best_CET;
	private double average_CET;
	
	 private Processor p, backupProcessor;
	 private int type;   //IF TASK TYPE IS HEAVY WEIGHT OR LIGHT WEIGHT
	 private boolean primary; // true for primary, false for secondary
	 
	 
	 
	 
	 
	 
	 
	/* *//**
	 * @return the p
	 *//*
	public Processor getP() {
		return p;
	}
	*//**
	 * @param p the p to set
	 *//*
	public void setP(Processor p) {
		this.p = p;
	}*/
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
	 * @return the faulty
	 */
	public boolean isFaulty() {
		return faulty;
	}
	/**
	 * @param faulty the faulty to set
	 */
	public void setFaulty(boolean faulty) {
		this.faulty = faulty;
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
	 * @return the completionSuccess
	 */
	public boolean isCompletionSuccess() {
		return completionSuccess;
	}
	/**
	 * @param completionSuccess the completionSuccess to set
	 */
	public void setCompletionSuccess(boolean completionSuccess) {
		this.completionSuccess = completionSuccess;
	}
	/**
		 * @return the period
		 */
		public long getPeriod() {
			return period;
		}

		/**
		 * @param period the period to set
		 */
		public void setPeriod(long period) {
			this.period = period;
		}
	 
	 
	 // VARIOUS LISTS
	 /**
		 *  list of processors on which job has executed
		 */
	 private ArrayList<Processor> proc_list = new ArrayList<Processor>();
		
	 /**
		 * all start times of slots when job preempted
		 */
		PriorityQueue<Long> startTimes = new PriorityQueue<Long>();
		/**
		 * all end times of slots when job preempted
		 */
		PriorityQueue<Long> endTimes = new PriorityQueue<Long>();
		/**
		 * when job preempted add start time of slot
		 * @param time
		 */
	 
		
		
	/**
	 * @return the finishTime
	 */
	public long getFinishTime() {
		return finishTime;
	}
	
	/**
	 * @return the promotionTime
	 */
	public long getPromotionTime() {
		return promotionTime;
	}
	/**
	 * @param promotionTime the promotionTime to set
	 */
	public void setPromotionTime(long promotionTime) {
		this.promotionTime = promotionTime;
	}
	/**
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	
	/**
	 * @return the p
	 */
	public Processor getProc() {
		return p;
	}

	/**
	 * @param p the p to set
	 */
	public void setProc(Processor p) {
		this.p = p;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}


	
	/**
	 * get the list of processors on which job has executed
	 * @return the proc_list
	 */
	public ArrayList<Processor> getProc_list() {
		return proc_list;
	}

	/**
	 * add the processor on which job is executing
	 * @param p
	 */
	public void addProc(Processor p)
	{ 
		proc_list.add(p);
	}
	
	public void addStartTime(Long time)
	{
		startTimes.add(time);
	}
	
	/**
	 * @return all start times
	 */
	public PriorityQueue<Long> getStartTimes()
	  {
		  return startTimes;
	  }

	/**
	 * add end time of execution slot
	 * @param time
	 */
	public void addEndTime(Long time)
	{
		endTimes.add(time);
	}
	
	/**
	 * get list of all end times
	 * @return
	 */
	public PriorityQueue<Long> getEndTimes()
	  {
		  return endTimes;
	  }
	
	
        
       	/**
       	 *  first start time of job
       	 * 	@return startTime 
       	 */
    	public long getStartTime()
        {
            return startTime;
        }
        
        /**
         *  get last end time
         * @return
         */
        public long getEndTime()
        {
           return endTime;
        }
        
        /**
         * set start time
         * @param time
         */
        public void setStartTime(long time)
        {
            startTime = time;
        }
        
    
        /**
         *  set end time
         * @param time
         */
        public void setEndTime(long time)
        {
            endTime = time;
        } 
        
        
    
       /**
     * @return job id
     */
    public long getJobId() 
       	{
    	   return jobId.getJobId();
       	}
        
        /**
         * @return task id by which job was generated
         */
        public long getTaskId()
        {
        	return jobId.getTaskId();
        }
	
        /**
         * @return remainingTimeCost
         */
        public long getRomainingTimeCost(){
        	return remainingTimeCost;
        }
	
        /**
         * @return remainingTime
         */
        public long getRemainingTime(){
        	return remainingTime;
        }
        
        /**
         * @return 
         */
        public long getRomainingEnergyCost(){
		return remainingEnergyCost;
        }
	 
        /**
         * set wcet
         * @param time
         */
        public void setRomainingTimeCost(long time)
        {
		 remainingTimeCost = time;
        }
        
        
        /**
         * set remaining time of job
         * @param time
         */
        public void setRemainingTime(long time)
        {
		 remainingTime = time;
        }
      	
   
	
        /**
         * @param energy 
         */
        public void consumeEnergy(long energy){
		remainingEnergyCost -= energy;
		assert remainingEnergyCost >= 0 : "jobId ; "+jobId+" : energyCost < 0";  
        }

	/**
	 * @return the activationDate
	 */
        /**
         * @return
         */
        public long getActivationDate() {
		return activationDate;
        }

	/**
	 * @return the absoluteDeadline
	 */
	public long getAbsoluteDeadline() {
		return absoluteDeadline;
	}
	
	
	/**
	 * @return job
	 */
	public Job cloneJob(){
	//	return  new Job(jobId, activationDate, remainingTimeCost, remainingTime, absoluteDeadline, isPreemptive,type);
    	return new  Job(jobId, activationDate, remainingTimeCost, remainingTime, absoluteDeadline,
    			period, frequency, (long)promotionTime,BCET,ACET,Best_CET, average_CET);

	}
	public Job cloneJob_MWFD_RMS_EEPS(){
		//	return  new Job(jobId, activationDate, remainingTimeCost, remainingTime, absoluteDeadline, isPreemptive,type);
	    	return new  Job(jobId, activationDate, remainingTimeCost, remainingTime, absoluteDeadline,
	    			period, frequency, (long)promotionTime,BCET,ACET,Best_CET, average_CET,p,primary, backupProcessor);

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
	/** return true if job has not started still
     *
     * @param time
     * @return 
     */
   
    public boolean isActive(long time)
        {
            return time < this.getActivationDate();
                       
        }
    
    public void setPreemptive(boolean x)
	{
		isPreemptive= x;
	}
	
	public boolean getIsPreemptive()
	{
		return isPreemptive;
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
		 * @return the deadline
		 */
		public long getDeadline() {
			return deadline;
		}

		/**
		 * @param deadline the deadline to set
		 */
		public void setDeadline(long deadline) {
			this.deadline = deadline;
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
		 * @return the extended_exec_time
		 */
		public double getExtended_exec_time() {
			return extended_exec_time;
		}

		/**
		 * @param extended_exec_time the extended_exec_time to set
		 */
		public void setExtended_exec_time_at_freq( double frequency) {
			this.extended_exec_time = (double)remainingTimeCost/frequency;
		}
		
}
