package de.nomagic;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerManager
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private HashMap<String, Worker> workers = new HashMap<String, Worker>();

    public WorkerManager()
    {
    }

    public void addWorker(String name, Worker w)
    {
        workers.put(name, w);
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

    public Worker getNextWorkerThatCanWork()
    {
        // TODO
        return null;
    }

    public String getStatus()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Status of Workers:" + ControlConnectionTask.LINE_END);
        sb.append(workers.size() + " Workers:" + ControlConnectionTask.LINE_END);
        // TODO
        return sb.toString();
    }

}
