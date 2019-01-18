/**
 *
 */
package de.nomagic.Jobserver.JobQueue;

/**
 * @author lars
 *
 */
public abstract class BaseJobQueue implements JobQueue
{

    /**
     *
     */
    public BaseJobQueue()
    {
        // TODO Auto-generated constructor stub
    }

    public abstract String getNextJob();

}
