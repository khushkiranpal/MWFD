package taskGeneration_temp;
import java.util.ArrayList;
import java.util.Comparator;

import taskGeneration.ITask;

public class TaskSet    { 
    private ArrayList<ITask> taskSet;
    int size;
    public static int SORT_D=1,SORT_T=2,SORT_ID=3;
    private int CsleepMin = 2;
    public TaskSet() {
            this.taskSet = new ArrayList<>();
            size = 0;
    }
    public TaskSet(ArrayList<ITask> t) {
        this.taskSet = t;
        size = t.size();
        /*Task tsleep = getSleepTask();
        if(tsleep.getWcet() > CsleepMin)
            addTask(tsleep);*/
        sort(SORT_ID);
    }
    public boolean worstCaseResp(ITask tSleep){
        for(ITask t:taskSet)
        {
            long w=t.getWCET_orginal();
			long w1=w-1;
            while(w != w1)
            {
                w1 = w;
                w = (int) (t.getWcet() + Math.ceil((double) w1/tSleep.getPeriod())*tSleep.getWcet());
                for(int i=0; taskSet.get(i) != t; i++)
                    w += (int) (Math.ceil((double) w1/taskSet.get(i).getPeriod())*taskSet.get(i).getWcet());
                
            }
            if( w > t.getDeadline())
                return false;
            }
        return true;
    }
    public double processUtil() {
        double s=0;
        for(ITask t:taskSet)
            s += (double)t.getWcet()/t.getPeriod();
        return s;
    }
    public long hyperPeriod(){
		if(size>0)
		{
			long h=taskSet.get(0).getPeriod();
			int i=1;
			while(i<size)
			{
				h=h*getTask(i).getPeriod()/pgcd(h,getTask(i).getPeriod());
				i++;
			}
			return h;
		}
		return 0;
	}
    public long pgcd(long h, long l) {
		if(l>h)
		{
			long c=l;
			l=h;
			h=c;
		}
		while(h%l!=0)
		{
			long c=h;
			h=l;
			l=c%l;
		}
		return l;
	}
    public void addTask(ITask t){
            taskSet.add(t);
	}
    public void printTs(){
            for(ITask t:taskSet)
            {
                    System.out.println(t);
            }
	}
    public void setTaskSet(ArrayList<ITask> ts){
            taskSet=ts;
            size = ts.size();
	}
    public ArrayList<ITask> getPeriodaskSet() {
            return taskSet;
	}
    public ITask getTask(int i){
        return taskSet.get(i);
	}
    void sort(int type){
        Comparator sorter = new Comparator<ITask>() {
            @Override
            public int compare(ITask t1, ITask t2) {
                if(type == 1 && t1.getDeadline() != t2.getDeadline())
                    return (int) (t1.getDeadline() - t2.getDeadline());
                
                if(type == 2 && t1.getPeriod() != t2.getPeriod())
                    return (int) (t1.getPeriod() - t2.getPeriod());
                
                return (int) (t1.getId() - t2.getId());
            }
        };
        taskSet.sort(sorter);
    }        
    boolean worstCaseResp() {
        for(ITask t:taskSet)
        {
            long w=t.getWCET_orginal();
			long w1=w-1;
            while(w != w1)
            {
                w1 = w;
                w = t.getWCET_orginal();
                for(int i=0; taskSet.get(i) != t; i++)
                    w += (int) (Math.ceil((double) w1/taskSet.get(i).getPeriod())*taskSet.get(i).getWcet());
                
            }
            if( w > t.getDeadline())
                return false;
            }
        return true;
    }
    
}
