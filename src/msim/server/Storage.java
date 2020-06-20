package msim.server;

import java.io.File;
import java.util.*;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * @author m1kc
 */
public class Storage {

    private static final String filename = "msim-server.sqlite";
    private static SqlJetDb database;
    private static ISqlJetTable accountsTable, contactsTable;
    private static final Map<String, String> accounts = Collections.synchronizedMap(new HashMap<>());
    private static final List<Contact> contacts = Collections.synchronizedList(new LinkedList<>());

    public static void init() throws SqlJetException {
        Log.print("Открываем файл с базой данных...");

        File f = new File(filename);
        boolean exists = f.exists();
        database = SqlJetDb.open(f, true);

        if (!exists) {
            Log.print("Базы нету. Ща создадим.");

            Log.print("Задаём параметры...");
            database.getOptions().setAutovacuum(true);

            Log.print("Создаём таблицы...");

            String query;

            query = "CREATE TABLE `accounts` ( "
                    + " `username` TINYTEXT NOT NULL PRIMARY KEY , "
                    + " `password` TINYTEXT NOT NULL ) ";
            database.createTable(query);
            accountsTable = database.getTable("accounts");

            query = "CREATE TABLE `contacts` ( "
                    + " `ownerAccount` TINYTEXT NOT NULL , "
                    + " `id` TINYTEXT NOT NULL , "
                    + " `nick` TINYTEXT NOT NULL , "
                    + " `group` TINYTEXT NOT NULL ) ";
            database.createTable(query);
            contactsTable = database.getTable("contacts");

            ////////////////////////////////////////////////////////////////////
            Log.print("Вписываем всякую хрень...");
            createAccount("m1kc", "112");
            addContact("m1kc", "m1kc", "m1kc", "General");
            addContact("m1kc", "Solkin", "Solkin", "General");
            addContact("m1kc", "ssadsad", "someone", "kkk");
            createAccount("Solkin", "112");
            addContact("Solkin", "m1kc", "m1kc", "General");
            addContact("Solkin", "Solkin", "Solkin", "General");
            addContact("Solkin", "ssadsad", "someone", "kkk");
        }

        Log.print("База есть. Читаем данные...");
        accountsTable = database.getTable("accounts");
        contactsTable = database.getTable("contacts");
        database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        ISqlJetCursor cursor;
        cursor = accountsTable.open();
        while (!cursor.eof()) {
            accounts.put(cursor.getString("username"), cursor.getString("password"));
            cursor.next();
        }
        cursor = contactsTable.open();
        while (!cursor.eof()) {
            contacts.add(new Contact(cursor.getString("ownerAccount"),
                    cursor.getString("id"),
                    cursor.getString("nick"),
                    cursor.getString("group")));
            cursor.next();
        }
        database.commit();
    }

    /**
     * Небезопасный метод! Контакты нельзя менять - изменения не отразятся в
     * базе данных.
     */
    public static Iterable<Contact> getContactsForIteration() {
        return contacts;
    }

    public static boolean accountExists(String login) {
        return accounts.containsKey(login);
    }

    public static void createAccount(String login, String pass) throws SqlJetException {
        accounts.put(login, pass);
        accountsTable.insert(login, pass);
    }

    public static String getAccount(String login) {
        return accounts.get(login);
    }

    public static boolean accountExists(String login, String pass) {
        String tmp = getAccount(login);
        if (tmp == null) {
            return false;
        }
        return (tmp.hashCode() == pass.hashCode());
    }

    public static void addContact(String ownerAccount, String id, String nick, String group) throws SqlJetException {
        contacts.add(new Contact(ownerAccount, id, nick, group));
        contactsTable.insert(ownerAccount, id, nick, group);
    }

    public static void removeContact(Contact q) throws SqlJetException {
        contacts.remove(q);

        database.beginTransaction(SqlJetTransactionMode.WRITE);

        ISqlJetCursor deleteCursor = contactsTable.open();
        while (!deleteCursor.eof()) {
            String ownerAccount = deleteCursor.getString("ownerAccount");
            String id = deleteCursor.getString("id");
            if ((q.ownerAccount.hashCode() == ownerAccount.hashCode()) && (q.id.hashCode() == id.hashCode())) {
                deleteCursor.delete();
            }
            deleteCursor.next();
        }
        database.commit();
    }
}
