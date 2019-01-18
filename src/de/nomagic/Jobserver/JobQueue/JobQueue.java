package de.nomagic.Jobserver.JobQueue;

public interface JobQueue
{
    String getNextJob();
    boolean hasMoreJobs();
    void skip(long num);
}
