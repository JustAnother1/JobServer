package de.nomagic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClientStatistic
{
    private HashMap<String, Integer> stat = new  HashMap<String, Integer>();
    private HashMap<String, Integer> activity = new  HashMap<String, Integer>();

    public ClientStatistic()
    {
    }

    public void printStatistsics()
    {
        int sum = 0;
        int sumAct = 0;

        SortedSet<String> keys = new TreeSet<>(stat.keySet());
        if(0 < keys.size())
        {
            System.out.println("Client Name : Number of Jobs");
            for (String entryKey : keys)
            {
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
                sumAct = sumAct + act;
                System.out.println(msg);
            }
            System.out.println(new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new Date())
                    + " : " + "total : " + sum + " (" + sumAct + ")");
            System.out.println("End of List");
        }
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
