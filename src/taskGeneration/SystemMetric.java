/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskGeneration;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import queue.ISortedQueue;
import queue.SortedQueuePeriod;

/**
 *
 * @author KIRAN
 */
public class SystemMetric {
    private static final Comparator<ITask> comparator = new Comparator<ITask>() {
		public int compare(ITask t1, ITask t2) {
			return (int) (t1.getPriority() - t2.getPriority());
		}
	};

	/**
	 * 
	 * @param taskset
	 * @return
	 */
	public static double density(Iterable<ITask> taskset){
		double u = 0;
		double C,D;
		for (ITask task : taskset) {
			C = task.getWcet();
			D = task.getDeadline();
			u += C/D; 
		}
		return u;
	}
	/**
	 * 
	 * @param taskset
	 * @return
	 */
	public static double utilisation(Iterable<ITask> taskset){
		double u = 0;
		double C,T;
		for (ITask task : taskset) {
			C = task.getWcet();
			T = task.getPeriod();
			u += C/T; 
		}
		return u;
	}

	public static double utilisation(ITask[] taskset){
		double u = 0;
		double C,T;
		for (ITask task : taskset) {
			C = task.getWcet();
			T = task.getPeriod();
			u += C/T; 
		}
		return u;
	}



	/**
	 * 
	 * @param taskset
	 * @return
	 */

	public static boolean isFPTimeFaisible(ITask [] taskSet){
		ISortedQueue ts= new SortedQueuePeriod();
		for (ITask task : taskSet) {
			ts.addTask(task);
		}
		return isFPTimeFaisible(ts);
	}

	public static boolean isFPTimeFaisible(ISortedQueue ts){
		ISortedQueue taskSet =new SortedQueuePeriod();
		for (ITask task : ts) {
			taskSet.addTask(task);
		}
		double u = utilisation(taskSet);
		if(u > 1)return false;
		switch (detectDeadLineModel(taskSet)) {
		case CONSTRAINED_DEADLINE:
		case IMPLICITE_DEADLINE  :	
			SortedSet<ITask> tasks = new TreeSet<ITask>(comparator);
			for (ITask task : taskSet) 
				tasks.add(task);

			long Ci, Tj,Di,Wt,t,nbInterfer;
			for (ITask task_i : tasks) {
				Ci  = task_i.getWCET_orginal();
				Di  = task_i.getDeadline();
				Wt = 0;
				do{
					t = Wt;
					Wt =  Ci;
					for (ITask task_j : tasks.headSet(task_i)) {
						Tj  = task_j.getPeriod();
						nbInterfer  = t / Tj;
						nbInterfer += (t % Tj ==0 ? 0 : 1);
						Wt += nbInterfer *task_j.getWcet();
					}
				}while(Wt != t && Wt <= Di);
				if(Wt > Di)return false;
			}
			return true;
		}
		// TODO arbitrary deadline
		return false;
	}	


	/**
	 * 
	 * @param tasks
	 * @return
	 */
	public static DeadlineModel detectDeadLineModel(ISortedQueue tasks){
		boolean implicite = true;
		for (ITask task : tasks) {
			if(task.getDeadline() > task.getPeriod()) return  DeadlineModel.ARBITRARY_DEADLINE;
			if(task.getDeadline() < task.getPeriod())implicite = false;
		}
		return implicite ? DeadlineModel.IMPLICITE_DEADLINE : DeadlineModel.CONSTRAINED_DEADLINE;
	}

	/**
	 * 
	 * @param taskset
	 * @return
	 */

	public static long FPBusyPeriod(Iterable<ITask> taskset){
		ISortedQueue ts = new SortedQueuePeriod();
		for (ITask task : taskset) {
			ts.addTask(task);
		}
		return FPBusyPeriod(ts);
	}

	public static long FPBusyPeriod(ISortedQueue taskset){
		ITask lowerPriorityTask = null;
		for (ITask task : taskset) {
			if(lowerPriorityTask==null)lowerPriorityTask = task;
			if(task.getPriority() > lowerPriorityTask.getPriority())
				lowerPriorityTask = task;
		}
		if(lowerPriorityTask != null) return FPBusyPeriodForLevelK(taskset, lowerPriorityTask);
		return -1;
	}

	public static long FPBusyPeriodForLevelK(ISortedQueue taskset, ITask task_k){
		long t, w = task_k.getWCET_orginal();
		long hp = hyperPeriod(taskset);
		do {
			t = w;
			w = 0;
			for (ITask task_i : taskset.getHeadSet(task_k, true)) {
				w += (t / task_i.getPeriod()) * task_i.getWcet();
				w += (t % task_i.getPeriod() == 0 ? 0 : task_i.getWcet());  
			}
		}while(t!=w && t<=hp);
		return t;
	}

	

	public static long  lcm (long nb1, long nb2) {
		long prod, rest, lcm;

		prod = nb1*nb2;
		rest   = nb1%nb2;
		while(rest != 0){
			nb1 = nb2;
			nb2 = rest;
			rest = nb1%nb2;
		}
		lcm = prod/nb2;
		return lcm;		
	} 

	private static long lcm(long nb1, long ... nbs){
		long lcm = nb1;
		for (long nbi : nbs) {
			lcm = lcm(lcm, nbi);
		}
		return lcm;
	}

	public static long hyperPeriod(Iterable<ITask> taskset){
		int count = 0;
		for (@SuppressWarnings("unused") ITask task : taskset) count++;
		long [] tasksPeriods = new long [count];
		int i = 0;
		long p1 = 0;
		for (ITask task : taskset) {
			tasksPeriods[i] = task.getPeriod();
			p1 = task.getPeriod();
			i++;
		}
		return lcm(p1,tasksPeriods);
	}

	public static long effectiveDeadline(ITask task, ISortedQueue taskset, long di) {
		long edi = di, lastedi;
		long t = di - task.getDeadline();
		//System.out.println("#### tk="+task.getPriority()+"### t="+t+" #### di="+di+" ############");
		do{
			lastedi = edi;
			for (ITask task_j : taskset.getHeadSet(task, false)) {
				long xj = (long) task_j.getNextActivation(t);
				long fj = Math.max(0, (edi-xj)/task_j.getPeriod());
				if(xj<edi && xj + fj * task_j.getPeriod() + task_j.getWcet() >= edi ){
					edi = xj + fj*task_j.getPeriod();
					//System.out.println("t"+task_j.getPriority()+" edi="+edi+" fj="+fj+" xj="+xj+" ci="+task_j.getWcet());
				}
			}
			//System.out.println("^^^^^^^^^^^^^^^");
		}while(edi != lastedi);
		return edi;

	}

    
}
