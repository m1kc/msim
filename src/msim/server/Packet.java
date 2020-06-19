/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author m1kc
 */
public class Packet {

    String source, dest, type, content;

    public Packet(String source, DataInputStream dis) throws IOException {
        this.source = source;
        this.dest = dis.readUTF();
        this.type = dis.readUTF();
        this.content = dis.readUTF();
    }

    public Packet(String source, String dest, String type, String content) {
        this.source = source;
        this.dest = dest;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return source + " → " + dest + ", "
                + "\"" + type + "\": «" + content + "»";
    }

}
