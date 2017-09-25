/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package queue;

import taskGeneration.ITask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author KIRAN
 */
public class SortedQueuePeriod implements ISortedQueue{
    private final TreeSet<ITask> tasks;
	private int id;

	protected Comparator<ITask> comparator;

	protected SortedQueuePeriod(Comparator<ITask> compar) {
		this.tasks = new TreeSet<ITask>(compar);
		comparator = compar;
	}
	
	public SortedQueuePeriod(){
		this(new Comparator<ITask>() {
			public int compare(ITask t1, ITask t2) {
				long comp = (t1.getPeriod() - t2.getPeriod());
				if(comp==0)
					comp = t1.getDeadline() - t2.getDeadline();
				if(comp==0)
					comp = t1.getPriority() - t2.getPriority();
				if(comp==0)
					comp =  (t1.getWCET_orginal() - t2.getWCET_orginal());
				
				if(comp==0)
					comp =  (t1.getArrival() - t2.getArrival());

				if (comp==0)
					comp = (t1.getId()-t2.getId());
				return (int) comp;
			}
		});
	}

    public boolean addTask(ITask t) {
		boolean b = tasks.add(t);
		return b;
	}
    public boolean addTasks(List<ITask> tasks) {
		for (ITask t : tasks) {
			addTask(t);
		}
		return true;
	}
	
    public boolean addTasks(ITask[] t) {
		return addTasks(Arrays.asList(t));
	}
	
    public SortedSet<ITask> getActiveTasks(long date) {
		SortedSet<ITask> activeTasks = new TreeSet<ITask>(comparator);
		for (ITask t : tasks)
			if (t.isActive())
				activeTasks.add(t);
		return activeTasks;
		
	}
    
    public ArrayList<ITask> getSortedSet()
    {
    	return (new ArrayList<ITask>(tasks)) ;
    }
    

    public Iterator<ITask> iterator() {
		return tasks.iterator();
	}

    public int getAllTasksCount() {
		return tasks.size();
	}

    public SortedSet<ITask> getHeadSet(ITask task, boolean inclusive) {
		return tasks.headSet(task, inclusive);
	}

    public int getId() {
		return id;
	}

    public int setId(int id) {
		return this.id = id;
	}

    /**
     *
     * @return
     */
    public ITask getOne() {
		return tasks.first();
	}

    /**
     *
     * @return
     */
    public ISortedQueue cloneTaskSet() {
		ISortedQueue set = new SortedQueuePeriod(comparator);
		for (ITask task : tasks) {
			set.addTask(task.cloneTask());
		}
		return  set;
	}

    /**
     *
     * @return
     */
    public ISortedQueue newInstance() {
		return new SortedQueuePeriod(comparator);
	}

	    
}
