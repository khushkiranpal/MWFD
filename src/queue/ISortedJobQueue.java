/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import taskGeneration.ITask;
import taskGeneration.Job;

/** INTERFACE FOR JOB QUEUE
 *
 * @author KIRAN
 */
public interface ISortedJobQueue {
      
	public boolean addJob( Job j);

	/**
	 * return a subset containing active jobs, ordered consistently with the
	 * scheduling policy used. Note that the ordering may be inconsistent with
	 * Task.equals();
	 * 
     * @param date DATE IS CURRENT TIME 
    
     
	 * @return the active jobs IN QUEUE
	 */
	public SortedSet<Job> getActiveJobs(long date);

	public int getAllJobCount();
   	
	 public boolean isEmpty();
	 
	 public Job first();
	public Job pollFirst();

	public long size();
	  public void remove(Job j);
	public int getId();

	int setId(int id);

	public Job getOne();

	 public Iterator<Job> iterator();
    
}
