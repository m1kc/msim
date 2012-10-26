/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

/**
 *
 * @author m1kc
 */
public class Log 
{
    private static long start = -1;
    
    public static void init()
    {
        if (start==-1) 
        {
            start = System.currentTimeMillis();
        }
        else 
        {
            print("Попытка повторной инициализации лога.");
        }
    }
    
    private static String time()
    {
        long time = System.currentTimeMillis()-start;
        String seconds = String.valueOf(time/1000);
        while(seconds.length()<10) seconds = ' '+seconds;
        String milliseconds = String.valueOf(time%1000);
        while(milliseconds.length()<3) milliseconds = '0'+milliseconds;
        return seconds+"s "+milliseconds+"ms";
    }
    
    public static void print(String description)
    {
        System.out.print("["+time()+"] ");
        if (description != null) System.out.print(description);
        System.out.println();
    }
    
    public static void error(String description, Throwable ex)
    {
        System.out.print("["+time()+"] ");
        if (description != null) System.err.print(description);
        System.out.println();
        if (ex != null) ex.printStackTrace(System.err);
        System.out.println();
    }
}
