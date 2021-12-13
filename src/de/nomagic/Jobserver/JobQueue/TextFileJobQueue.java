package de.nomagic.Jobserver.JobQueue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TextFileJobQueue extends BaseJobQueue
{
    private BufferedReader list = null;

    public TextFileJobQueue(String FileName)
    {
        try
        {
            list = new BufferedReader(new FileReader(FileName));
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            list = null;
        }
    }

    @Override
    public String getNextJob(String type)
    {
        // TODO type = filename?, ignore for now
        return getNextJob();
    }

    public String getNextJob()
    {
        if(null == list)
        {
            return null;
        }
        else
        {
            try
            {
                return list.readLine();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                list = null;
            }
            return null;
        }
    }

    public boolean hasMoreJobs()
    {
        if(null == list)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean addJob(String type, String Job)
    {
        // not possible
        return false;
    }

    @Override
    public void printStatistsics()
    {
        System.out.println("Queue Type = single text file");
    }

    @Override
    public void close()
    {
        // not possible
    }

}
