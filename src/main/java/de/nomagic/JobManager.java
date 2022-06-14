package de.nomagic;

import java.util.HashMap;

import de.nomagic.job.Job;

public class JobManager
{
    private HashMap<String, Job> activeJobs = new HashMap<String, Job>();

    public JobManager()
    {
    }

    public String listActiveJobs()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all active jobs:");
        sb.append(ControlConnectionTask.LINE_END);
        for (String name : activeJobs.keySet())
        {
            sb.append(name);
            sb.append(ControlConnectionTask.LINE_END);
        }
        return sb.toString();
    }

    public void updateStatus()
    {
        // ask all active workers for their status
        // TODO
    }

    public void add(Job j, Worker w)
    {
        // TODO
    }

    public Job[] getFinishedJobs()
    {
        // TODO
        return null;
    }

    public String getStatus()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Status of active Jobs:" + ControlConnectionTask.LINE_END);
        sb.append(activeJobs.size() + " active Jobs:" + ControlConnectionTask.LINE_END);
        // TODO
        return sb.toString();
    }

}
