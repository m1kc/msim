/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

/**
 *
 * @author m1kc
 */
public class Connection extends Thread
{
    Server parent;
    /*SSL*/Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    long uid = Math.abs(new Random().nextLong());
    String account = "$temp$"+String.valueOf(uid);
    boolean authorized = false;
    private boolean streamsReady = false;

    public Connection(Server parent, /*SSL*/Socket s) 
    {
        this.parent = parent;
        this.s = s;
        Log.print("Подключение с IP "+s.getInetAddress().getHostAddress()+", дан временный номер "+account);
    }
    
    public void openStreams() throws IOException
    {
        if (streamsReady) 
        {
            Log.print("Предупреждение: вызов openStreams() после открытия потоков, соединение "+uid+" ("+account+").");
            return;
        }
        dis = new DataInputStream(s.getInputStream());
        dos = new DataOutputStream(s.getOutputStream());
        s.setSoTimeout(120_000);
        streamsReady = true;
    }
    
    @Override
    public void run()
    {
        try 
        {
            if (!streamsReady) openStreams();
            while(true)
            {
                Packet p = new Packet(account, dis);
                parent.processPacket(this, p);
            }
        }
        catch (SocketTimeoutException ex)
        {
            Log.print(uid+" ("+account+") отключился по таймауту.");
        }
        catch (SocketException ex)
        {
            if (ex.getMessage().hashCode()=="Connection reset".hashCode())
            {
                Log.print(uid+" ("+account+") отключился.");
            }
            else
            {
                Log.error("Страшная хуйня на соединении "+uid+" ("+account+").", ex);
            }
        }
        catch (EOFException ex)
        {
            Log.print(uid+" ("+account+") отключился.");
        }
        catch (IOException ex) 
        {
            Log.error("Страшная хуйня на соединении "+uid+" ("+account+").", ex);
        }
        finally
        {
            parent.connections.remove(this);
            
            try
            {
                parent.accountDisonnected(account);
            }
            catch (IOException ex)
            {
                // Да похуй уже.
            }
        }
    }
    
}
