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
                + "exit - close this connection" + LINE_END
                + "help - show this list of available commands" + LINE_END
                + "kill - kills this server" + LINE_END
                + "status - show current state of server" + LINE_END;
    }

    private String cmdExit()
    {
        shouldRun = false;
        return "Bye.";
    }

    private String cmdKill()
    {
        shouldRun = false;
        jobServer.close();
        return "I'm dying!!!!";
    }

    private String cmdStatus()
    {
        return jobServer.getStatus();
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
        case "exit": return cmdExit();
        case "kill": return cmdKill();
        case "status": return cmdStatus();

        default: return "invalid command : " + cmd + " ! try help for list of available commands";
        }
    }
}
