/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

import com.tomclaw.bingear.BinGear;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author m1kc
 */
public class Server extends Thread
{
    /*SSL*/ServerSocket server;
    int port;
    
    List<Connection> connections = Collections.synchronizedList(new LinkedList<Connection>());

    static final String EMPTY = "";
    static final String FAIL = "fail";
    static final String SUCCESS = "success";
    static final String WTF = "wtf";
    
    static final String SERVER = "SERVER";
    
    public boolean noRegistration = false;
    
    public Server(int port)
    {
        this.port = port;
    }
    
    @Override
    public void run()
    {
        try 
        {
            server = new ServerSocket(port);
            //server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(port);
            Log.print("Сервер готов.");
        }
        catch (IOException ex) 
        {
            Log.error("Не могу поднять сервер. Останавливаем поток...", ex);
            return;
        }
        
        while(true)
        {
            try
            {
                /*SSL*/Socket s = /*(SSLSocket)*/ server.accept();
                Connection c = new Connection(this, s);
                c.openStreams();
                c.start();
                connections.add(c);
            }
            catch (IOException ex)
            {
                Log.error("Ошибка при работе с соединением.", ex);
            }
        }
    }

    private boolean isOnline(String account)
    {
        for (Connection c : connections)
        {
            if (c.account.hashCode()==account.hashCode())
            {
                return true;
            }
        }
        return false;
    }
    
    private void sendPacketToAccount(Packet p, String account) throws IOException
    {
        for (Connection c : connections)
        {
            if (c.account.hashCode() == account.hashCode())
            {
                Utils.sendPacket(p, c.dos);
            }
        }
    }
    
    /**
     * Отправляет в ответ клиенту пакет с заданным типом и типом исходного
     * пакета в поле content.
     */
    private void responseFromServer(Connection c, Packet p, String responseType) throws IOException
    {
        responseFromServer(c, p, responseType, p.type);
    }
    
    /**
     * Отправляет в ответ клиенту пакет с заданными типом и 
     * значением поля content.
     */
    private void responseFromServer(Connection c, Packet p, String responseType, String responseContent) throws IOException
    {
        Utils.sendPacket(new Packet(SERVER, c.account, responseType, responseContent), c.dos);
    }
    
    public void accountConnected(String account) throws IOException
    {
        for (Contact c : Storage.getContactsForIteration())
        {
            if (c.ownerAccount.hashCode()==account.hashCode())
            {
                for (Connection conn : connections)
                {
                    if (conn.account.hashCode()==c.id.hashCode())
                    {
                        Utils.sendPacket(new Packet(account, conn.account, "presence", "online"), conn.dos);
                    }
                }
            }
        }
    }
    
    public void accountDisonnected(String account) throws IOException
    {
        for (Contact c : Storage.getContactsForIteration())
        {
            if (c.ownerAccount.hashCode()==account.hashCode())
            {
                for (Connection conn : connections)
                {
                    if ((conn.account).hashCode()==c.id.hashCode())
                    {
                        Utils.sendPacket(new Packet(account, conn.account, "presence", "offline"), conn.dos);
                    }
                }
            }
        }
    }
    
    public void processPacket(Connection c, Packet p) throws IOException 
    {
        Log.print("<< "+p.toString());
        
        String[] tmp = p.content.split("[|]");
        String login, pass;
        String id;
        boolean success = false;
        
        try
        {
            switch(p.type)
            {
                case "ping":
                    responseFromServer(c, p, "ping-response", EMPTY);
                    break;
                    
                case "register":
                    login = tmp[0];
                    pass = tmp[1];
                    if (noRegistration || Storage.accountExists(login))
                    {
                        responseFromServer(c, p, FAIL);
                    }
                    else
                    {
                        Storage.createAccount(login, pass);
                        Log.print("Зарегистрирован новый аккаунт: "+login+", "+pass+".");
                        responseFromServer(c, p, SUCCESS);
                    }
                    break;
                    
                case "auth":
                    login = tmp[0];
                    pass = tmp[1];
                    if (c.authorized)
                    {
                        responseFromServer(c, p, WTF);
                    }
                    else
                    {
                        if (Storage.accountExists(login, pass))
                        {
                            Log.print("Авторизовался: "+c.account+", теперь он - "+login+".");
                            c.account = login;
                            c.authorized = true;
                            responseFromServer(c, p, SUCCESS);
                            accountConnected(login);
                        }
                        else
                        {
                            responseFromServer(c, p, FAIL);
                        }
                    }
                    break;
                    
                case "message":
                    if (isOnline(p.dest))
                    {
                        sendPacketToAccount(new Packet(c.account, p.dest, "message", p.content), p.dest);
                    }
                    else
                    {
                        Log.print("Оффлайн сообщение. Да иди ты нахуй!");
                    }
                    break;
                    
                case "contacts-list":
                    BinGear b = new BinGear();
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if (q.ownerAccount.hashCode()==c.account.hashCode())
                        {
                            if (b.getGroup(q.group) == null) b.addGroup(q.group);
                            b.addItem(q.group, q.nick, q.id);
                        }
                    }
                    responseFromServer(c, p, "contacts-list", b.exportToIni());
                    break;
                    
                case "contacts-add":
                    Storage.addContact(c.account, tmp[0], tmp[1], tmp[2]);
                    responseFromServer(c, p, SUCCESS);
                    break;
                    
                case "contacts-rename":
                    id = tmp[0];
                    String newNick = tmp[1];
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if ((q.ownerAccount.hashCode()==c.account.hashCode())&&(q.id.hashCode()==id.hashCode()))
                        {
                            q.nick = newNick;
                            success = true;
                        }
                    }
                    responseFromServer(c, p, (success ? SUCCESS : FAIL));
                    break;
                    
                case "contacts-remove":
                    up:
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if ((q.ownerAccount.hashCode()==c.account.hashCode())&&(q.id.hashCode()==p.content.hashCode()))
                        {
                            Storage.removeContact(q);
                            success = true;
                            break up;
                        }
                    }
                    responseFromServer(c, p, (success ? SUCCESS : FAIL));
                    break;
                    
                case "contacts-groups-list":
                    HashSet<String> s = new HashSet<>();
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if (q.ownerAccount.hashCode()==c.account.hashCode())
                        {
                            s.add(q.group);
                        }
                    }
                    StringBuilder result = new StringBuilder();
                    for (String string : s)
                    {
                        result.append("|").append(string);
                    }
                    responseFromServer(c, p, "contacts-groups-list", result.substring(1));
                    break;
                    
                case "contacts-groups-rename":
                    String oldName = tmp[0];
                    String newName = tmp[1];
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if ((q.ownerAccount.hashCode()==c.account.hashCode())&&(q.group.hashCode()==oldName.hashCode()))
                        {
                            q.group = newName;
                            success = true;
                        }
                    }
                    responseFromServer(c, p, (success ? SUCCESS : FAIL));
                    break;
                    
                case "contacts-groups-remove":
                    LinkedList<Contact> rm = new LinkedList<>();
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        if ((q.ownerAccount.hashCode()==c.account.hashCode())&&(q.group.hashCode()==p.content.hashCode()))
                        {
                            rm.add(q);
                        }
                    }
                    for (Contact q : rm) 
                    {
                        Storage.removeContact(q);
                        success = true;
                    }
                    responseFromServer(c, p, (success ? SUCCESS : FAIL));
                    break;
                    
                case "presence-poll":
                    for (Contact q : Storage.getContactsForIteration())
                    {
                        String acc = q.id.split("[@]")[0];
                        String srv = q.id.split("[@]")[1];
                        if (q.ownerAccount.hashCode()==p.source.hashCode())
                        {
                            if (isOnline(acc))
                            {
                                Utils.sendPacket(new Packet(acc, p.source, "presence", "online"), c.dos);
                            }
                            else
                            {
                                Utils.sendPacket(new Packet(acc, p.source, "presence", "offline"), c.dos);
                            }
                        }
                    }
                    break;
                    
                default:
                    Log.print("Неверный пакет: "+p.type);
                    responseFromServer(c, p, "error-unknown-type");
                    break;
            }
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (Throwable ex)
        {
            Log.error("Исключение при обработке пакета.", ex);
            responseFromServer(c, p, "error-internal", EMPTY);
        }
    }
}
