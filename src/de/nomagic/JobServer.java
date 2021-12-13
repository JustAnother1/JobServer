package de.nomagic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.nomagic.Jobserver.ResultReporter;
import de.nomagic.Jobserver.JobQueue.BruteForceJobQueue;
import de.nomagic.Jobserver.JobQueue.InMemoryJobQueue;
import de.nomagic.Jobserver.JobQueue.JobQueue;
import de.nomagic.Jobserver.JobQueue.TextFileFolderJobQueue;
import de.nomagic.Jobserver.JobQueue.TextFileJobQueue;

public class JobServer extends Thread
{
    private final static long REPORT_INTERVALL = 30000;  // ms


    private String JobFileName = "neu.txt";
    private String JobFolderName = ".";
    private int ServerPort = 4321;
    private JobQueue jobs = null;
    private boolean shouldRun = true;
    private int numJobsSendOut = 0;
    private DataOutputStream toClient = null;
    private ClientStatistic cs = new ClientStatistic();
    private ResultReporter rr = new ResultReporter();
    private ServerSocket welcomeSocket;


    public JobServer()
    {
    }

    public static void main(String[] args)
    {
        JobServer m = new JobServer();
        m.getConfigFromCommandLine(args);

        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                public void run()
                {
                    System.out.println("Shutting down ...");
                    m.close();
                    m.shouldRun = false;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        m.welcomeSocket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println("closed.");
                }
            }
        );

        m.start();
    }

    private void printHelpText()
    {
        System.err.println("Parameters:");
        System.err.println("===========");
        System.err.println("-h");
        System.err.println("     : This text");
        System.err.println("-jobfile <file name>");
        System.err.println("     : read jobs from text file 'file name'");
        System.err.println("-jobdir <folder name>");
        System.err.println("     : read jobs from text files with .txt extension in the folder 'folder name'");
        System.err.println("-port <port number>");
        System.err.println("     : use the given port instead of the default port " + ServerPort);
        System.err.println("-skip <number of jobs>");
        System.err.println("     : start executing jobs atthe defined position in the job list");
        System.err.println("-bf <type> <start value> <increment>");
        System.err.println("     : scan a large key space increment by increment");
    }

    public void getConfigFromCommandLine(String[] args)
    {
        String JobType = "";
        String startValue = "";
        String increment = "";
        boolean isBruteForce = false;
        boolean hasJobDir = false;
        boolean hasJobFile = false;
        long skip = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(true == args[i].startsWith("-"))
            {
                if(true == "-jobfile".equals(args[i]))
                {
                    hasJobFile = true;
                    i++;
                    JobFileName = args[i];
                }
                else if(true == "-jobdir".equals(args[i]))
                {
                    hasJobDir = true;
                    i++;
                    JobFolderName = args[i];
                }
                else if(true == "-port".equals(args[i]))
                {
                    i++;
                    ServerPort = Integer.parseInt(args[i]);
                }
                else if(true == "-skip".equals(args[i]))
                {
                    i++;
                    skip = Long.parseLong(args[i]);
                }
                else if(true == "-bf".equals(args[i]))
                {
                    i++;
                    JobType = args[i];
                    i++;
                    startValue = args[i];
                    i++;
                    increment = args[i];
                    isBruteForce = true;
                }
                else if(true == "-h".equals(args[i]))
                {
                    printHelpText();
                    System.exit(0);
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[i]);
                    printHelpText();
                    System.exit(2);
                }
            }
            else
            {
                System.err.println("Invalid Parameter : " + args[i]);
                printHelpText();
                System.exit(1);
            }
        }

        if(true == hasJobDir)
        {
            jobs = new TextFileFolderJobQueue(JobFolderName);
        }
        else if(true == hasJobFile)
        {
            jobs = new TextFileJobQueue(JobFileName);
        }
        else if(true == isBruteForce)
        {
            jobs = new BruteForceJobQueue(JobType, startValue, increment);
        }
        else
        {
            jobs = new InMemoryJobQueue();
        }
        if(0 < skip)
        {
            for(int i = 0; i < skip; i++)
            {
                jobs.getNextJob();
            }
        }
    }

    void parse_v1_request(String cmd) throws IOException
    {
        // Version 1:
        if(true == "getNextJob".equals(cmd))
        {
            String job = jobs.getNextJob();
            if(null == job)
            {
                System.out.println("No more Jobs available!");
                shouldRun = false;
            }
            else
            {
                toClient.writeBytes(job);
                numJobsSendOut++;
                cs.addJob("Anonymous");
            }
        }
        else if(true == cmd.startsWith("login:"))
        {
            String clientId = cmd.substring(6);
            String job = jobs.getNextJob();
            if(null == job)
            {
                System.out.println("No more Jobs available!");
                shouldRun = false;
            }
            else
            {
                System.out.print(new SimpleDateFormat("HH.mm.ss").format(new Date())
                        + " : " + numJobsSendOut
                        + " : Giving Job to " + clientId + "\n");
                toClient.writeBytes(job);
                numJobsSendOut++;
                cs.addJob(clientId);
            }
        }
        else
        {
            // invalid command
            toClient.writeBytes("ERROR: invalid Command !!!");
        }
    }

    void parse_v2_request(String cmd) throws IOException
    {
        RequestVersion2 req = new RequestVersion2(cmd);
        if(true == req.isAddJob())
        {
            // add a new Job
            if(true == jobs.addJob(req.getType(), req.getJobSpec()))
            {
                System.out.println(new SimpleDateFormat("HH.mm.ss").format(new Date())
                        + " : Received a new Job from " + req.getClientId());
                System.out.println("Job Type = " + req.getType()  + " Spec : " + req.getJobSpec());
                toClient.writeBytes("2:0:\n");
            }
            else
            {
                toClient.writeBytes("2:3:\n");
            }
        }
        else if(true == req.isJobResult())
        {
            rr.report(req);
            toClient.writeBytes("2:0:\n");
        }
        else
        {
            // finished last job?
            rr.report(req);
            // get next Job
            String job = jobs.getNextJob();
            if((null == job) || (1 > job.length()))
            {
                toClient.writeBytes("2:1:\n");
            }
            else
            {
                rr.nextJob(req, job);
                toClient.writeBytes("2:0:" + job + "\n");
                System.out.println(new SimpleDateFormat("HH.mm.ss").format(new Date())
                        + " : " + numJobsSendOut +  " : Giving a Job to " + req.getClientId());
                numJobsSendOut++;
                cs.addJob(req.getClientId());
            }
        }
    }

    @Override
    public void run()
    {
        // Startup

        if(false == jobs.hasMoreJobs())
        {
            System.out.println("No Jobs available!");
        }

        try
        {
            welcomeSocket = new ServerSocket(ServerPort);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }
        long now = System.currentTimeMillis();
        long nextReport = now + REPORT_INTERVALL;
        while((false == isInterrupted()) && (true == shouldRun))
        {
            try
            {
                final Socket connectionSocket = welcomeSocket.accept();
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                toClient = new DataOutputStream(connectionSocket.getOutputStream());
                String cmd = fromClient.readLine();
                if(null != cmd)
                {
                    if(true == cmd.startsWith("2:"))
                    {
                        parse_v2_request(cmd);
                    }
                    else
                    {
                        // invalid command or V1
                        parse_v1_request(cmd);
                    }
                    now = System.currentTimeMillis();
                    if(now > nextReport)
                    {
                        nextReport = now + REPORT_INTERVALL;
                        cs.printStatistsics();
                        jobs.printStatistsics();
                    }
                }
                toClient.flush();
                connectionSocket.close();
            }
            catch(IOException e)
            {
                if(true == shouldRun)
                {
                    e.printStackTrace();
                }
            }
        }
        try
        {
            welcomeSocket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Shutdown
        close();
        System.out.println("Done!");
        System.exit(0);
    }

    protected void close()
    {
        jobs.close();
        System.out.println("Send out " + numJobsSendOut + " Jobs.");
        cs.printStatistsics();
    }

}
