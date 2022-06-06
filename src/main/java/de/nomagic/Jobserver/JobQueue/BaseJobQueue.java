package de.nomagic.Jobserver.JobQueue;

public abstract class BaseJobQueue implements JobQueue
{
    public BaseJobQueue()
    {
    }

    public abstract String getNextJob();

}
