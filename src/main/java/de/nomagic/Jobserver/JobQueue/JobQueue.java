package de.nomagic.Jobserver.JobQueue;

public interface JobQueue
{
    boolean addJob(String type, String Job);
    String getNextJob();
    String getNextJob(String type);
    boolean hasMoreJobs();
    void printStatistsics();
    void close();
}
