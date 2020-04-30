package de.nomagic.Jobserver;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.nomagic.RequestVersion2;

public class JobState
{
    private String JobDefinition = "";
    private String ClientName = "";
    private Date start;
    private Date finished;
    private String result;

    public JobState()
    {
    }

    public void setJobDefinition(String job)
    {
        JobDefinition = job;
    }

    public void setFromRequest(RequestVersion2 req)
    {
        if(null != req)
        {
            ClientName = req.getClientId();
            result = req.getAttribute("result");
        }
    }

    public void setStartTime(Date date)
    {
        start = date;
    }

    public void setFinishTime(Date date)
    {
        finished = date;
    }

    public String getReport()
    {
        return "Client : " + ClientName + "\n"
                + "Job: " + JobDefinition + "\n"
                + "started : " + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(start) + "\n"
                + "finished : " + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(finished) + "\n"
                + "Result : " + result + "\n\n";
    }
}
