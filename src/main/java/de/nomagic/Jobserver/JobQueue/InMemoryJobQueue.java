package de.nomagic.Jobserver.JobQueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class InMemoryJobQueue extends BaseJobQueue
{
    public static final String unfinishedJobsFileName = "inmemoryJobs.txt";
    private HashMap<String,ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
    private int numJobs = 0;

    public InMemoryJobQueue()
    {
        // check if jobs file exists
        // load all jobs from that file

        File f = new File(unfinishedJobsFileName);

        if(f.exists())
        {
            try
            {
                BufferedReader list = new BufferedReader(new FileReader(f));
                String job = list.readLine();
                while(null != job)
                {
                    String type = job.substring(0, job.indexOf(':'));
                    String desc = job.substring(job.indexOf(':') + 1);
                    // System.out.println("type: " + type);
                    // System.out.println("desc: " + desc);
                    addJob(type, desc);
                    job = list.readLine();
                }
                list.close();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        // else : no unfinished Jobs
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
        if(null == res)
        {
            res = "";
        }
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
        if( 1 < keys.size())
        {
            Object[] types = keys.toArray();
            for(int i = 0; i < types.length; i++)
            {
                String typ = (String) types[new Random().nextInt(types.length)];
                res = getNextJob(typ);
                if(0 < res.length())
                {
                    return res;
                }
            }
        }
        // we had really bad luck or only one entry
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

    @Override
    public void close()
    {
        if(hasMoreJobs())
        {
            FileWriter out = null;
            try
            {
                out = new FileWriter(unfinishedJobsFileName, false);
                Set<String> keys = data.keySet();
                Iterator<String> it = keys.iterator();
                while(true == it.hasNext())
                {
                    String type = it.next();
                    System.out.println("Found type " + type);
                    String descr = null;
                    do {
                        descr = getNextJob(type);
                        if(0 < descr.length())
                        {
                            // System.out.println("saving type: " + type + " and descr: " + descr);
                            out.write(type + ":" + descr + "\n");
                        }
                    } while(0 < descr.length());
                }
                System.out.println("Found all Jobs");
                out.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(null != out)
                {
                    try
                    {
                        System.out.println("Closing file");
                        out.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
