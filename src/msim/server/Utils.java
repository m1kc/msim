/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author m1kc
 */
public class Utils {

    public static synchronized void sendPacket(Packet p, DataOutputStream dos) throws IOException {
        Log.print(">> " + p.toString());
        dos.writeUTF(p.source);
        dos.writeUTF(p.type);
        dos.writeUTF(p.content);
        dos.flush();
    }
}
