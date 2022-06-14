package de.nomagic.Jobserver.JobQueue;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.ControlConnectionTask;
import de.nomagic.job.Job;

public class JobQueueManager
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, JobQueue> queues = new HashMap<String, JobQueue>();

    public JobQueueManager()
    {
    }

    public String listQueues()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all job queues:");
        sb.append(ControlConnectionTask.LINE_END);
        for (String name : queues.keySet())
        {
            sb.append(name);
            sb.append(ControlConnectionTask.LINE_END);
        }
        return sb.toString();
    }

    public boolean hasMoreJobs()
    {
        // TODO
        return false;
    }

    public Job getJobOfType(String[] types)
    {
        if(null == types)
        {
            return null;
        }
        if(1 > types.length)
        {
            return null;
        }
        // TODO
        return null;
    }

    public String getStatus()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Status of Job Queues:" + ControlConnectionTask.LINE_END);
        sb.append(queues.size() + " Job Queues" + ControlConnectionTask.LINE_END);
        // TODO
        return sb.toString();
    }

    public void updateFinishedJobs(Job[] finished)
    {
        // TODO
        // if a Queue is now finished create a report?
    }

}
