package de.nomagic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.job.Job;

public class Worker
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private String workerHost = "";
    private int workePort = 0;
    private String workerName = "";
    private Socket clientSocket;
    private BufferedReader inFromWorker;
    private DataOutputStream outToWorker;
    private boolean isConnected = false;
    private boolean valid = true;

    public Worker(String url)
    {
        String[] parts = url.split(":");
        if(2 == parts.length)
        {
            workerHost = parts[0];
            try
            {
                workePort = Integer.parseInt(parts[1]);
                valid = true;
            }
            catch(java.lang.NumberFormatException e)
            {
                e.printStackTrace();
                valid = false;
            }
        }
        else
        {
            log.trace("parts.length = " + parts.length);
            valid = false;
        }
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getName()
    {
        return workerName;
    }

    public boolean connect()
    {
        if(false == valid)
        {
            return false;
        }
        try
        {
            clientSocket = new Socket(workerHost, workePort);
            outToWorker = new DataOutputStream(clientSocket.getOutputStream());
            inFromWorker = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            isConnected = true;
            String hello = inFromWorker.readLine();
            log.info(hello);
            if(false == getNameFromHello(hello))
            {
                return false;
            }
            log.info("connected to worker {}", workerName);
            return true;
        }
        catch(java.lang.IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void disconnect()
    {
        if(false == valid)
        {
            return;
        }
        if(null == clientSocket)
        {
            return;
        }
        try
        {
            clientSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        isConnected = false;
    }

    public String[] getSupportedJobTypes()
    {
        // TODO
        return null;
    }

    public void startWorkingOn(Job j)
    {
        // TODO
    }

    private boolean getNameFromHello(String hello)
    {
        // get name from hello
        // example: "Hi, I'm Peter. How can I help you?" -> "Peter"
        if(null == hello)
        {
            return false;
        }
        if(1 > hello.length())
        {
            return false;
        }
        if(false == hello.startsWith("Hi, I'm "))
        {
            return false;
        }
        String name = hello.substring(8, hello.indexOf('.'));
        if(1 > name.length())
        {
            return false;
        }
        workerName = name;
        return true;
    }

}
