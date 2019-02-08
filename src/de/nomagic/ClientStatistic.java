package de.nomagic;

import java.util.HashMap;
import java.util.Iterator;

public class ClientStatistic
{
    private HashMap<String, Integer> stat = new  HashMap<String, Integer>();
    private HashMap<String, Integer> activity = new  HashMap<String, Integer>();

    public ClientStatistic()
    {
    }

    public void printStatistsics()
    {
        Iterator<String> it = stat.keySet().iterator();
        System.out.println("Client Name : Number of Jobs");
        int sum = 0;
        while(it.hasNext())
        {
            String entryKey = it.next();
            Integer cnt = stat.get(entryKey);
            Integer act = activity.get(entryKey);
            // NULL => 0
            if(null == cnt)
            {
            	cnt = 0;
            }
            if(null == act)
            {
            	act = 0;
            }
            String msg = String.format("%30s : %10d (%3d)", entryKey, cnt, act);
            sum = sum + cnt;
            System.out.println(msg);
        }
        System.out.println("total : " + sum);
        System.out.println("End of List");
        activity.clear();
    }

    public void addJob(String clientId)
    {
        Integer cnt = stat.get(clientId);
        if(null == cnt)
        {
            // first Job for this client
            stat.put(clientId, 1);
        }
        else
        {
            cnt++;
            stat.put(clientId, cnt);
        }

        Integer act = activity.get(clientId);
        if(null == act)
        {
            // first Job for this client
            activity.put(clientId, 1);
        }
        else
        {
            act++;
            activity.put(clientId, act);
        }
    }

}
