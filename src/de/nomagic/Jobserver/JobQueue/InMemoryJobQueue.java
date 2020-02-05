package de.nomagic.Jobserver.JobQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class InMemoryJobQueue extends BaseJobQueue
{
    private HashMap<String,ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
    private int numJobs = 0;

    public InMemoryJobQueue()
    {
        // Nothing to do here
    }

    @Override
    public boolean hasMoreJobs()
    {
        if(0 < numJobs)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getNextJob(String type)
    {
        ArrayList<String> jobs = data.get(type);
        if(null == jobs)
        {
            return "";
        }
        if(true == jobs.isEmpty())
        {
            return "";
        }
        String res = jobs.get(0);
        jobs.remove(0);
        numJobs--;
        return res;
    }

    @Override
    public String getNextJob()
    {
        String res = "";
        if(true == data.isEmpty())
        {
            return "";
        }
        Set<String> keys = data.keySet();
        Iterator<String> it = keys.iterator();
        while(true == it.hasNext())
        {
            res = getNextJob(it.next());
            if(0 < res.length())
            {
                return res;
            }
        }
        return res;
    }

    @Override
    public boolean addJob(String type, String Job)
    {
        if((null == type) || (null == Job))
        {
            return false;
        }
        if((1 > type.length()) || (1 > Job.length()))
        {
            return false;
        }
        ArrayList<String> jobs = data.get(type);
        if(null == jobs)
        {
            // first entry for this type
            jobs = new ArrayList<String>();
        }
        jobs.add(Job);
        numJobs++;
        data.put(type, jobs);
        return true;
    }

    @Override
    public void printStatistsics()
    {
        System.out.println("Queue Type = in memory");
        Set<String> keys = data.keySet();
        Iterator<String> it = keys.iterator();
        while(true == it.hasNext())
        {
            String type = it.next();
            ArrayList<String> jobs = data.get(type);
            System.out.println("Jobs remaining : " + jobs.size() + " of type " + type);
        }
    }

}
