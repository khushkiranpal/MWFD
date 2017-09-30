/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package taskGeneration;


import java.util.PriorityQueue;

import platform.Processor;

/**
 *
 * @author KIRAN
 */
public interface ITask {
	

	/**
	 * @return the primary
	 */
	public boolean isPrimary() ;

	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(boolean primary) ;

		/**
	 * @return the p
	 */
	public Processor getP();
	/**
	 * @param p the p to set
	 */
	public void setP(Processor p) ;
	
	
	/**
 * @return the backupProcessor
 */
public Processor getBackupProcessor() ;

/**
 * @param backupProcessor the backupProcessor to set
 */
public void setBackupProcessor(Processor backupProcessor) ;
	/**
 * @return the c
 */
public long getC();
/**
 * @return the d
 */
public long getD();
/**
 * @return the t
 */
public long getT() ;

	/**
 * @param wCET_orginal the wCET_orginal to set
 */
public void setWCET_orginal(long wCET_orginal) ;

/**
 * @param period the period to set
 */
public void setPeriod(long period) ;
/**
 * @param deadline the deadline to set
 */
public void setDeadline(long deadline);
    
	//public boolean X = false;
		/**
		 * Gets the identifier of the task
		 * 
		 * @return task's id
		 */
		public long getId();

		/**
		 * Sets an identifier to the task
		 * 
		 * @param id
		 *            task's identifier to be set
		 */
		void setId(int id);
		public void setWcet(long wcet);
		
		/**
		 * @return the bCET
		 */
		public double getBCET() ;

		/**
		 * @param bCET the bCET to set
		 */
		public void setBCET(double bCET) ;

		/**
		 * @return the aCET
		 */
		public double getACET() ;

		/**
		 * @param aCET the aCET to set
		 */
		public void setACET(double aCET) ;
		/**
		 * Gets the first release time of the task
		 * 
		 * @return task's first release time
		 */
		
		public long getArrival();

    /**
     *
     * @param arrival
     */
    void setArrival(long arrival);

		/**
		 * Gets the worst-case execution time of the task
		 * 
		 * @return task's WCET
		 */
		public long getWcet();

		public long getWCET_orginal() ;

			/**
		 * @param wcet the wcet to set
		 */
	//	public void setWcet( wcet) ;
	//	public void setWcet(long wcet);

		/**
		 * Gets the relative period of the task
		 * 
		 * @return task's period
		 */
		public long getPeriod();

		/**
		 * Gets the relative deadline of the task
		 * 
		 * @return task's deadline
		 */
		public long getDeadline();

		/**
		 * Gets the priority of the task
		 * 
		 * @return task's priority
		 */
		public int getPriority();
		
		public  Job activateRMS(long time) ;
		public  Job activate_MWFD_RMS_EEPS(long time);
		/**
		 * Sets the priority of the task
		 * 
		 * @param priority
		 *            task's priority to be set
		 */
		public void setPriority(int priority);
                /**
		 * Duplicates the task's object
		 * 
		 * @return clones task
		 */
		public ITask cloneTask();
		 public ITask cloneTask_MWFD_RMS_EEPS();
		
		  public ITask cloneTask_RMS_double() ;
           //     public long getLaxity();

		/**
		 * Returns the active jobs of the task
		 * 
		 * @return task's active jobs
		 */
		public PriorityQueue<Job> getActiveJobs();

		/**
		 * Checks if the task has active jobs
		 * 
		 * @return true if there are active jobs, false otherwise
		 */
		public boolean isActive();

		/**
		 * Activates a job of this task with a release time of 'time' units
		 * 
		 * @param time
		 *            time units
		 */
		
		public  Job activateRMS_energy_ExecTime(long time);
		public Job activate(long time);

		/**
		 * Gets the current job of the task, i.e. the first job that has not been
		 * completed
		 * 
		 * @return task's current job
		 */
		public Job getCurrentJob();


		/**
	 * @return the best_CET
	 */
	public double getBest_CET();

	/**
	 * @param best_CET the best_CET to set
	 */
	public void setBest_CET(double best_CET) ;

	/**
	 * @return the average_CET
	 */
	public double getAverage_CET() ;
	/**
	 * @param average_CET the average_CET to set
	 */
	public void setAverage_CET(double average_CET) ;
		
		
		/**
		 * Checks if the task missed its deadline by checking its current job at a
		 * certain instant of time
		 * 
		 * @param time
		 *            time instant
		 * @return true if the task missed its deadline, false otherwise
		 */
		public boolean checkDeadlineMissed(long time);

		/**
		 * Checks if the active job is not the last executed one
		 * 
		 * @return true if the active job is not the last executed one, false
		 *         otherwise
		 */
		public boolean lastExecutedJobHasCompleted();

		/**
		 * Gets the remaining time cost of task's current job
		 * 
		 * @return task's remaining time cost
		 */
		public long getRemainingCost();

		/**
		 * Gets the absolute deadline of the current job of the task according to
		 * the value of the boolean 'nextPeriod'
		 * 
		 * 
		 * @param time
		 *            current instant of time
		 * @param nextPeriod
		 *            if true, next period of the task is calculated. If false,
		 *            current period is calculated
		 * @return task's next deadline
		 */
		public long getNextDeadline(long time, boolean nextPeriod);

		/**
		 * Gets the next activation time of the task
		 * 
		 * @param time
		 *            current time
		 * @return task's next activation time
		 */
		public long getNextActivation(long time);

		/**
		 * Checks if a new job of task can be activated at 'time' instant of time
		 * 
		 * @param time
		 *            current time
		 * @return true if it is activation time, false otherwise
		 */
		public boolean isActivationTime(long time);

		/**
		 * Gets the absolute deadline of the next job of the task
		 * 
		 * @param time
		 *            current instant of time
		 * @return task's next absolute deadline
		 */
		public long getNextAbsoluteDeadline(long time);

		/**
		 * Gets the absolute deadline of the previous job of the task
		 * 
		 * @param time
		 *            current instant of time
		 * @return task's previous absolute deadline
		 */
		public long getPreviousAbsoluteDeadline(long date);

		/**
		 * Gets task's last executed job
		 * 
		 * @return last executed job
		 */
		public Job getLastExecutedJob();

		/**
		 * Sets last executed job of the task
		 * 
		 * @param lastExecutedJob
		 *            the last executed job to set
		 */
		public void setLastExecutedJob(Job lastExecutedJob);
		
		public void addactivatedjob(Job j);

		/**
		 * Gets the type of the task (simple, subtask, graph, ...)
		 * 
		 * @return task's type
		 */
	//	public String getType();
		public void setPreemptive(boolean x);
		
		public boolean getIsPreemptive();
		public void setType(int type);
		public int getType();
		  /**
		 * @return the u
		 */
		public float getU() ;
		/**
		 * @param u the u to set
		 */
		public void setU(float u);
		/**
		 * @return the slack
		 */
		public double getSlack();
		/**
		 * @param slack the slack to set
		 */
		public void setSlack(double slack);

		/**
		 * @return the responseTime
		 */
		public double getResponseTime() ;

		/**
		 * @param w the responseTime to set
		 */
		public void setResponseTime(double w) ;
		/**
		 * @return the is_Schedulabe
		 */
		public boolean isIs_Schedulabe() ;

		/**
		 * @param is_Schedulabe the is_Schedulabe to set
		 */
		public void setIs_Schedulabe(boolean is_Schedulabe) ;
		/**
		 * @return the finishTime for mpn-EDf eq 4
		 */
		public long getFinishTime();

		/**
		 * @param finishTime the finishTime to set
		 */
		public void setFinishTime(long finishTime) ;
		
	
		
		/**
		 * @return the energy_consumed
		 */
		public double getEnergy_consumed() ;

		/**
		 * @param energy_consumed the energy_consumed to set
		 */
		public void setEnergy_consumed(double energy_consumed) ;

		/**
		 * @return the frequency
		 */
		public double getFrequency() ;

		/**
		 * @param frequency the frequency to set
		 */
		public void setFrequency(double frequency) ;

		/**
		 * @return the voltage
		 */
		public double getVoltage() ;

		/**
		 * @param voltage the voltage to set
		 */
		public void setVoltage(double voltage) ;

		public  Job activateRMS_energy(long time);

		public void setWcet(Double valueOf);
}
