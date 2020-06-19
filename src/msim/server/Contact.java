/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

/**
 * @author m1kc
 */
public class Contact {

    String ownerAccount, id, nick, group;

    public Contact(String ownerAccount, String id, String nick, String group) {
        this.ownerAccount = ownerAccount;
        this.id = id;
        this.nick = nick;
        this.group = group;
    }

}
