/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import taskGeneration.Job;

/**  SORT THE JOBS ACCORDING TO DEADLINE. IF DEADLINES OF BOTH TASKS ARE EQUAL THAN 
 *   SORT ACCORDING TO TASKID AND FURTHER BY REMAINING WCET
 *
 * @author KIRAN
 */
public class SortedJobQueue implements ISortedJobQueue {
     private final TreeSet<Job> job;  // QUEUE OF JOBS INITIALIZED IN CTOR
     private int id;
     
     /**
     * COMPARATOR DEFINED FOR SORTING OF JOBS IN QUEUE
     */
    private final Comparator<Job> comparator = new Comparator<Job>() {
    	 public int compare(Job j1, Job j2) {
			int cmp =  (int) (j1.getPeriod()-j2.getPeriod());
			if(cmp==0)
				 cmp = 	(int) (j1.getDeadline()-j2.getDeadline());
			if(cmp==0)
			    cmp =  (int) (j1.getAbsoluteDeadline()-j2.getAbsoluteDeadline());
			if(cmp==0)
				cmp= (int) (j2.getRemainingTime() - j1.getRemainingTime());// due to least  laxity of job having larger execution time 
			if (cmp==0)														// if D=9, R1=2, R2 = 5, so R2 must have higher priority having laxity
				cmp= (int)(j1.getTaskId()-j2.getTaskId());					// larger 9-5=4 than 9-2=7.
			return cmp;
		}
	  };
        
	  /**
	 *   CTOR CREATING  QUEUE BASED ON COMPARATOR
	 */
	public SortedJobQueue(){
		this.job = new TreeSet<Job>(comparator);  // CREATES A TREESET OR SORTED QUEUE BASED ON COMPARATOR DEFINED ABOVE 
	}
   
	/**
	 *   RETURNS JOB AT THE FRONT AND DELETE IT
	 */
	public Job pollFirst()
        {
          return job.pollFirst();
        }
	 
	 /**
	 * @return first element  of list
	 */
	public Job first()
	 {
		 return job.first();
	 }
	
	
	
      public boolean isEmpty()
      {
    	  return job.isEmpty();
      }
	
	/** 
	 *  RETURNS THE SIZE OF JOB QUEUE
	 */
	public long size()
        {
        	return job.size();
        }

    /**
     *
     * @return ITERATOR TO QUEUE OF JOBS
     */
    public Iterator<Job> iterator() {
		return job.iterator();
                	}
        
     /**
      * ADDS THE JOB TO CURRENT QUEUE AT THE PROPER LOCATION WITH ASSIGNED PRIORITY
     */
    public boolean addJob( Job j) {
		return job.add(j);
	}
        
     public SortedSet<Job> getActiveJobs(long date) {
		SortedSet<Job> activeJobs = new TreeSet<Job>(comparator);
		for (Job j : job)
			if (j.isActive(date))
				activeJobs.add(j);
		return activeJobs;
	}
        
     public int getAllJobCount() {
		return job.size();
	}
   
        
     public int getId() {
		return id;
	}

     public int setId(int id) {
		return this.id = id;
	}
        
     /* (non-Javadoc)
     * @see queue.ISortedJobQueue#getOne()
     */
    public Job getOne() {
		return job.first();
	}
    
    public void remove(Job j)
    {
    	job.remove(j);
    }
}
