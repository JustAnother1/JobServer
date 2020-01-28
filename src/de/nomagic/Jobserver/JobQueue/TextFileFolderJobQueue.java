package de.nomagic.Jobserver.JobQueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class TextFileFolderJobQueue extends BaseJobQueue
{
    private int fidx;
    private File[] jobFiles;
    private BufferedReader curFile = null;
    private String FolderName;

    public TextFileFolderJobQueue(String jobFolderName)
    {
        FolderName = jobFolderName;
        jobFiles = findFiles(jobFolderName);
        fidx = 0;
    }

    @Override
    public String getNextJob(String type)
    {
        // TODO type = filename?, ignore for now
        return getNextJob();
    }

    @Override
    public String getNextJob()
    {
        if(null == curFile)
        {
            openNextFile();
        }
        if(null == curFile)
        {
            // no more files available -> no more jobs
            return null;
        }
        // read the job
        boolean done = false;
        String res = null;
        do
        {
            try
            {
                res = curFile.readLine();
                done = true;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                curFile = null;
                openNextFile();
                if(null == curFile)
                {
                    // no next file :-(
                    res = null;
                    done = true;
                }
            }
        } while(false == done);
        return res;
    }

    @Override
    public boolean hasMoreJobs()
    {
        if(fidx < jobFiles.length)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void openNextFile()
    {
        if(fidx < jobFiles.length)
        {
            try
            {
                curFile = new BufferedReader(new FileReader(jobFiles[fidx]));
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
                curFile = null;
            }
            fidx++;
        }
        else
        {
            // no more files available -> no more jobs
            return;
        }
    }

    private File[] findFiles(String dirName)
    {
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter()
            {
                 public boolean accept(File dir, String filename)
                      {
                          return filename.endsWith(".txt");
                      }
            } );

    }

    @Override
    public boolean addJob(String type, String Job)
    {
        boolean res = false;
        FileWriter out = null;
        try
        {
            out = new FileWriter(FolderName + '/' + type + "_jobs.new", true);
            out.write(Job + "\n");
            res = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(null != out)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    res = false;
                }
            }
        }
        return res;
    }

}
