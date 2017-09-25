/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package queue;

import taskGeneration.ITask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import taskGeneration.Job;

/**
 *
 * @author KIRAN
 */
public interface ISortedQueue extends Iterable<ITask> {
    	/**
	 * Add a task T to the task set
	 * 
	 * @param t
	 *            the task to add
	 * @return false if the task cannot be added (already a task with this
	 *         priority in the task set)
	 */
	public boolean addTask(ITask t);

	/**
	 * return a subset containing active tasks, ordered consistently with the
	 * scheduling policy used. Note that the ordering may be inconsistent with
	 * Task.equals();
	 * 
     * @param time
     
	 * @return the active tasks
	 */
	public SortedSet<ITask> getActiveTasks(long time);
        
     public ArrayList<ITask> getSortedSet();  

	public int getAllTasksCount();

	public SortedSet<ITask> getHeadSet(ITask task, boolean inclusive);

	public int getId();

	int setId(int id);

	public ITask getOne();

	boolean addTasks(List<ITask> t);
	boolean addTasks(ITask[] t);
	
	public ISortedQueue cloneTaskSet();
	
	public ISortedQueue newInstance();

	Iterator<ITask> iterator();
	
	//public String getName();
	

}

