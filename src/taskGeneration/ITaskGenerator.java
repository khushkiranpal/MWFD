/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package taskGeneration;

/**
 *
 * @author KIRAN
 */
public interface ITaskGenerator {
    public ITask generate(double utilization, int deadlineModel, long period);
    public ITask generate(double utilization, int deadlineModel);
     public ITask newInstance(Object... params);
     public ITask generateSporadic ( int deadlineModel, double utilization, double parameter, int periodDistribution );
    
}
