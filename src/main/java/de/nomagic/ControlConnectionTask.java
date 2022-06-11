package de.nomagic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlConnectionTask extends Thread
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    public static final int VERSION = 1;
    public static final String LINE_END = "\r\n";

    private final Socket connectionSocket;
    private final BufferedReader fromServer;
    private final DataOutputStream toServer;
    private boolean shouldRun = true;
    private JobServer jobServer;

    public ControlConnectionTask(Socket connectionSocket, JobServer jobServer) throws IOException
    {
        this.jobServer = jobServer;
        this.connectionSocket = connectionSocket;
        fromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        toServer = new DataOutputStream(connectionSocket.getOutputStream());
    }

    @Override
    public void run()
    {
        log.info("Openeing control connection!");
        try
        {
            toServer.writeBytes("Hi, How can I help you?" + LINE_END);
            String cmd = fromServer.readLine();
            while((null != cmd) && (true == shouldRun))
            {
                String response = parse(cmd);
                toServer.writeBytes(response);
                toServer.writeBytes(LINE_END);
                toServer.flush();
                if(true == shouldRun)
                {
                    cmd = fromServer.readLine();
                }
            }
            connectionSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        log.info("Closed control connection");
    }

    private String cmdHelp()
    {
        return "available commands:" + LINE_END
                + "add - add a new task to the work queue" + LINE_END
                + "exit - close this connection" + LINE_END
                + "get - get result of task" + LINE_END
                + "help - show this list of available commands" + LINE_END
                + "kill - kills this worker" + LINE_END
                + "list - list all queued or finished tasks" + LINE_END
                + "status - show current state of worker thread" + LINE_END
                + "version - show currently running client version" + LINE_END;
    }

    private String parse(String cmd) throws IOException
    {
        if(null == cmd)
        {
            return "";
        }
        if(1 > cmd.length())
        {
            return "";
        }
        String[] cmd_parts = cmd.split("\\s+");

        switch(cmd_parts[0])
        {
        case "help": return cmdHelp();

        default: return "invalid command : " + cmd + " !";
        }
        /*
        if(cmd_parts[0].startsWith("add"))
        {
            if(cmd_parts[0].length() > 1)
            {
                byte[] data = receiveFile();
                if(data.length > 0)
                {
                    return "OK: received the job " + cmd_parts[1];
                }
                else
                {
                    return "ERROR: Failed to receive the job " + cmd_parts[1];
                }
            }
            else
            {
                return "ERROR: task name missing !";
            }
        }
        if(cmd_parts[0].startsWith("exit"))
        {
            shouldRun = false;
            return "Bye.";
        }
        if(cmd_parts[0].startsWith("get"))
        {
            if(cmd_parts[0].length() > 1)
            {
                byte[] data = worker.get(cmd_parts[1]);
                sendFile(data);
                return "";
            }
            else
            {
                return "ERROR: task name missing !";
            }
        }
        if(cmd_parts[0].startsWith("help"))
        {

        }
        if(cmd_parts[0].startsWith("kill"))
        {
            shouldRun = false;
            worker.kill();
            return "I'm dying!!!!";
        }
        if(cmd_parts[0].startsWith("list"))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Queued tasks:");
            String[] queued = worker.getNamesOfQueuedJobs();
            for(int i = 0; i < queued.length; i++)
            {
                sb.append(LINE_END);
                sb.append(queued[i]);
            }
            sb.append(LINE_END);
            sb.append("finished tasks:");
            String[] finished = worker.getNamesOfFinishedJobs();
            for(int i = 0; i < finished.length; i++)
            {
                sb.append(LINE_END);
                sb.append(finished[i]);
            }
            return sb.toString();
        }
        if(cmd_parts[0].startsWith("status"))
        {
            return worker.getStatus();
        }
        if(cmd_parts[0].startsWith("version"))
        {
            return "" + VERSION;
        }

        return "invalid command : " + cmd + " !";
        */
    }
/*
    private byte[] receiveFile()
    {
        // TODO Auto-generated method stub
        return new byte[0];
    }

    private void sendFile(byte[] data) throws IOException
    {
        toServer.writeBytes("fileContentLength=" + data.length + ":" + LINE_END);
        toServer.write(data, 0, data.length);
    }
    */
}
