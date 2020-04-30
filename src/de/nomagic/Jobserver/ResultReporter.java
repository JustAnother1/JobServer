package de.nomagic.Jobserver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import de.nomagic.RequestVersion2;

public class ResultReporter
{
    private HashMap<String, JobState> stat = new  HashMap<String, JobState>();
    private FileWriter fw;

    public ResultReporter()
    {
        try
        {
            fw = new FileWriter("results.txt", true);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void report(RequestVersion2 req)
    {
        String client = req.getClientId();
        JobState state = stat.remove(client);
        if(null != state)
        {
            state.setFinishTime(new Date());
            state.setFromRequest(req);
            try
            {
                fw.append(state.getReport());
                fw.flush();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("First Job request of Client : " + client);
        }
    }

    public void nextJob(RequestVersion2 req, String job)
    {
        String client = req.getClientId();
        JobState state = new JobState();
        state.setJobDefinition(job);
        state.setFromRequest(req);
        state.setStartTime(new Date());
        stat.put(client, state);
    }

}
