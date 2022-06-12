package de.nomagic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import de.nomagic.Jobserver.JobQueue.BruteForceJobQueue;
import de.nomagic.Jobserver.JobQueue.InMemoryJobQueue;
import de.nomagic.Jobserver.JobQueue.JobQueue;
import de.nomagic.Jobserver.JobQueue.TextFileFolderJobQueue;
import de.nomagic.Jobserver.JobQueue.TextFileJobQueue;

public class JobServer
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private String JobFileName = "neu.txt";
    private String JobFolderName = ".";
    private int controlPort = 4321;
    private JobQueue jobs = null;
    private boolean shouldRun = true;
    private int numJobsSendOut = 0;
    private ClientStatistic cs = new ClientStatistic();
    private HashMap<String, Worker> workers = new HashMap<String, Worker>();

    public JobServer()
    {
    }

    private void startLogging(final String[] args)
    {
        boolean colour = true;
        int numOfV = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(true == "-v".equals(args[i]))
            {
                numOfV ++;
            }
            // -noColour
            if(true == "-noColour".equals(args[i]))
            {
                colour = false;
            }
        }

        // configure Logging
        switch(numOfV)
        {
        case 0: setLogLevel("warn", colour); break;
        case 1: setLogLevel("debug", colour);break;
        case 2:
        default:
            setLogLevel("trace", colour);
            System.err.println("Build from " + getCommitID());
            break;
        }
    }

    public String getCommitID()
    {
        try
        {
            final InputStream s = JobServer.class.getResourceAsStream("/git.properties");
            final BufferedReader in = new BufferedReader(new InputStreamReader(s));

            String id = "";

            String line = in.readLine();
            while(null != line)
            {
                if(line.startsWith("git.commit.id.full"))
                {
                    id = line.substring(line.indexOf('=') + 1);
                }
                line = in.readLine();
            }
            in.close();
            s.close();
            return id;
        }
        catch( Exception e )
        {
            return e.toString();
        }
    }

    private void setLogLevel(String LogLevel, boolean colour)
    {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try
        {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            final String logCfg;
            if(true == colour)
            {
                logCfg =
                "<configuration>" +
                  "<appender name='STDERR' class='ch.qos.logback.core.ConsoleAppender'>" +
                  "<target>System.err</target>" +
                    "<encoder>" +
                       "<pattern>%highlight(%-5level) [%logger{36}] %msg%n</pattern>" +
                    "</encoder>" +
                  "</appender>" +
                  "<root level='" + LogLevel + "'>" +
                    "<appender-ref ref='STDERR' />" +
                  "</root>" +
                "</configuration>";
            }
            else
            {
                logCfg =
                "<configuration>" +
                  "<appender name='STDERR' class='ch.qos.logback.core.ConsoleAppender'>" +
                  "<target>System.err</target>" +
                    "<encoder>" +
                      "<pattern>%-5level [%logger{36}] %msg%n</pattern>" +
                    "</encoder>" +
                  "</appender>" +
                  "<root level='" + LogLevel + "'>" +
                    "<appender-ref ref='STDERR' />" +
                  "</root>" +
                "</configuration>";
            }
            ByteArrayInputStream bin;
            bin = new ByteArrayInputStream(logCfg.getBytes(StandardCharsets.UTF_8));
            configurator.doConfigure(bin);
        }
        catch (JoranException je)
        {
          // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
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
        System.err.println("     : use the given port instead of the default port " + controlPort);
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
                    controlPort = Integer.parseInt(args[i]);
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
                else if(true == "-v".equals(args[i]))
                {
                    // ignore as already handled.
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

    public void run()
    {
        ControlTask com = new ControlTask();
        com.setPort(controlPort);
        com.setServer(this);
        com.start();
        do {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // is OK
            }
        } while((true == shouldRun) && (com.isAlive()));
    }

    protected void close()
    {
        jobs.close();
        System.out.println("Send out " + numJobsSendOut + " Jobs.");
        cs.printStatistsics();
        shouldRun = false;
    }

    public static void main(String[] args)
    {
        JobServer m = new JobServer();
        m.startLogging(args);
        m.getConfigFromCommandLine(args);
        m.run();
        System.out.println("Done!");
        System.exit(0);
    }

    public String getStatus()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Status of Jobserver:" + ControlConnectionTask.LINE_END);
        sb.append("control interface on TCP port: " + controlPort + ControlConnectionTask.LINE_END);
        return sb.toString();
    }

    public String listWorkers()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all workers:");
        sb.append(ControlConnectionTask.LINE_END);
        for (String name : workers.keySet())
        {
            sb.append(name);
            sb.append(ControlConnectionTask.LINE_END);
        }
        return sb.toString();
    }

    public String addWorker(String url)
    {
        Worker w = new Worker(url);
        if(false == w.isValid())
        {
            return "ERROR: invalid URL " + url + " expected: somehost:54321";
        }
        if(false == w.connect())
        {
            return "ERROR: Could not connect to worker at " + url;
        }
        String name = w.getName();
        workers.put(name, w);
        return "added worker " + name;
    }
}
