package de.nomagic.Jobserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import de.nomagic.RequestVersion2;

public class JobState
{
    private String JobDefinition = "";
    private String ClientName = "";
    private Date start;
    private Date finished;
    private String result = "";
    private Vector<String> parts = new Vector<String>();

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
            result = result + req.getAttribute("result");
            String[] addedParts = req.getAllParts();
            for(int i = 0; i < addedParts.length; i++)
            {
                parts.add(addedParts[i]);
            }
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

    private String difference(Date start, Date end)
    {
        // in milliseconds
        long diff = end.getTime() - start.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        StringBuffer sb = new StringBuffer();
        if(0 < diffDays)
        {
            sb.append(diffDays + " days, ");
        }
        if(0 < diffHours)
        {
            sb.append(diffHours + " hours, ");
        }
        if(0 < diffMinutes)
        {
            sb.append(diffMinutes + " minutes, ");
        }
        if(0 < diffSeconds)
        {
            sb.append(diffSeconds + " seconds.");
        }
        return sb.toString();
    }

    public String getReport()
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < parts.size(); i++)
        {
            sb.append(parts.get(i));
            sb.append("\n");
        }

        return "Client : " + ClientName + "\n"
                + "Job: " + JobDefinition + "\n"
                + "started : " + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(start) + "\n"
                + "finished : " + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(finished) + "\n"
                + "duration : " + difference(start, finished) + "\n"
                + "Result : " + result + "\n"
//                + "Request Parts : " + sb.toString()
                + "\n";
    }
}
