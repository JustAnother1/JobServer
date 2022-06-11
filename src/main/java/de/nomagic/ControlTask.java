package de.nomagic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlTask extends Thread
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private boolean shouldRun = true;
    private int myPort = 2323;
    private JobServer jobServer;

    public ControlTask()
    {
        super.setName("Control Interface");
    }

    public void close()
    {
        shouldRun = false;
        this.interrupt();
    }

    public void setPort(int TcpPort)
    {
        myPort = TcpPort;
    }

    public void setServer(JobServer jobServer)
    {
        this.jobServer = jobServer;
    }

    @Override
    public void run()
    {
        // Startup
        ServerSocket TcpListenSocket = null;
        try
        {
            TcpListenSocket = new ServerSocket(myPort);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }
        log.info("Started Worker on port {}", myPort);
        while((false == isInterrupted()) && (true == shouldRun))
        {
            try
            {
                final Socket connectionSocket = TcpListenSocket.accept();  // this blocks until a connection becomes available
                ControlConnectionTask cn = new ControlConnectionTask(connectionSocket, jobServer);
                cn.start();
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
            TcpListenSocket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Shutdown
        log.info("closing communication channel!");
    }




}
