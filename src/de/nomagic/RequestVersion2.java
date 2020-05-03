package de.nomagic;

public class RequestVersion2
{
    private boolean isJobREsult = false;
    private boolean isAddJob = false;
    private String JobSpec = "";
    private String name = "";
    private String type = "";
    private String[] parts;


    public RequestVersion2(String request)
    {
                                     //123456
        if(true == request.startsWith("2:add:"))
        {
            isAddJob = true;
            JobSpec = request.substring(6);
            request = JobSpec;
        }
                                     //123456789
        if(true == request.startsWith("2:result:"))
        {
            isJobREsult = true;
            JobSpec = request.substring(9);
            request = JobSpec;
        }
        parts = request.split(":");
        for(int i = 0; i < parts.length; i++)
        {
            String line = parts[i];
            line = line.trim();
            if(line.startsWith("name"))
            {
                name = line.substring(line.indexOf('=') + 1);
                name = name.trim();
            }
            if(line.startsWith("type"))
            {
                type = line.substring(line.indexOf('=') + 1);
                type = type.trim();
            }
        }
    }

    public String[] getAllParts()
    {
        return parts;
    }

    public String getAttribute(String which)
    {
        for(int i = 0; i < parts.length; i++)
        {
            String line = parts[i];
            line = line.trim();
            if(line.startsWith(which))
            {
                String res = line.substring(line.indexOf('=') + 1);
                res = res.trim();
                if(0 < res.length())
                {
                    return res;
                }
                // else continue search
            }
        }
        return "";
    }

    public boolean isAddJob()
    {
        return isAddJob;
    }

    public boolean isJobResult()
    {
        return isJobREsult;
    }

    public String getType()
    {
        return type;
    }

    public String getJobSpec()
    {
        // Request is add a Job!
        return JobSpec;
    }

    public String getClientId()
    {
        return name;
    }

}
