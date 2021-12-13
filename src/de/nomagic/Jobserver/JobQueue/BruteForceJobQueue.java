package de.nomagic.Jobserver.JobQueue;

public class BruteForceJobQueue extends BaseJobQueue
{
    private String jobType;
    private int JobsSendOut = 0;
    private int[] curKey;
    private int[] inc;
    private boolean reachedEnd = false;

    /**
     *
     * @param jobType The string send to the client as type
     * @param startValue Hex presentation of the first key to check. Example : "11 33 55 77 99 AA CC"
     * @param increment how many keys will be in one work package. Example "01 00 00" => 2^16 keys = 65536
     */
    public BruteForceJobQueue(String jobType, String startValue, String increment)
    {
        this.jobType = jobType;
        String[] parts = startValue.split(" ");
        int keyLength = parts.length;
        System.out.println("Key Length = " + keyLength);
        curKey = new int[keyLength];
        for(int i = 0; i < keyLength; i++)
        {
            curKey[i] = Integer.parseInt(parts[keyLength - (i + 1)], 16);
        }
        System.out.println("start key : "  + ArrayToHexString(curKey));

        String[] incParts = increment.split(" ");
        inc = new int[incParts.length];
        for(int i = 0; i < incParts.length; i++)
        {
            inc[i] = Integer.parseInt(incParts[incParts.length -(i + 1)], 16);
        }
        System.out.println("increment : "  + ArrayToHexString(inc));
    }

    @Override
    public boolean addJob(String type, String Job)
    {
        // not possible with this queue
        return false;
    }

    @Override
    public String getNextJob(String type)
    {
        return getNextJob();
    }

    @Override
    public boolean hasMoreJobs()
    {
        return ! reachedEnd;
    }

    @Override
    public void printStatistsics()
    {
        System.out.println("Queue Type = Brute Force");
        System.out.println("Jobs send out : " + JobsSendOut);
    }

    @Override
    public String getNextJob()
    {
        String Job = "type=" + jobType + ":key=" + ArrayToHexString(curKey) + ":";

        // calculate next Job
        for(int i = 0; i < inc.length; i++)
        {
            curKey[i] =  curKey[i] + inc[i];
        }
        // handle overflow
        for(int i = 0; i < curKey.length; i++)
        {
            if(curKey[i] > 255)
            {
                curKey[i] = curKey[i] - 256;
                if(i + 1 < curKey.length)
                {
                    curKey[i+1] = curKey[i+1] + 1;
                }
                else
                {
                    reachedEnd = true;
                }
            }
        }

        JobsSendOut ++;
        System.out.println("giving Job : " + Job);
        return Job;
    }

    private String ArrayToHexString(int[] arr)
    {
        StringBuffer hexString = new StringBuffer();
        for(int i = arr.length -1; i > -1; i--)
        {
            hexString.append(String.format("%02X ", arr[i]));
        }
        String res = hexString.toString();
        return res.trim();
    }

    @Override
    public void close()
    {
        System.out.println("Queue Type = Brute Force");
        System.out.println("Type : " + jobType);
        System.out.println("Jobs send out : " + JobsSendOut);
        System.out.println("reached end : " + reachedEnd);
        System.out.println("next key : "  + ArrayToHexString(curKey));
        System.out.println("increment : "  + ArrayToHexString(inc));
    }

}
