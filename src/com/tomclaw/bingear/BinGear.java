package com.tomclaw.bingear;

import com.tomclaw.utils.ArrayUtil;
import com.tomclaw.utils.StringUtil;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012 http://www.tomclaw.com/
 *
 * @author Игорь
 */
public class BinGear
{
    public HashMap<String, HashMap<String, String>> hashtable;

    public BinGear()
    {
        hashtable = new HashMap<>();
    }

    public void addGroup(String groupName) throws IncorrectValueException
    {
        if (groupName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        hashtable.put(groupName, new HashMap<String, String>());
    }

    public void addItem(String groupName, String itemName, String value) throws GroupNotFoundException, IncorrectValueException
    {
        if (groupName == null || itemName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            ((HashMap<String, String>) hashtable.get(groupName)).put(itemName, value);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public HashMap getGroup(String groupName) throws IncorrectValueException, GroupNotFoundException
    {
        if (groupName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            return (HashMap) hashtable.get(groupName);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public String getValue(String groupName, String itemName) throws GroupNotFoundException, IncorrectValueException
    {
        if (groupName == null || itemName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            return (String) ((HashMap) hashtable.get(groupName)).get(itemName);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public String getValue(String groupName, String itemName, boolean isFullCompare) throws GroupNotFoundException, IncorrectValueException
    {
        if (groupName == null || itemName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        if (isFullCompare || hashtable.containsKey(groupName))
        {
            return getValue(groupName, itemName);
        }
        else
        {
            try
            {
                Set groupKeys = hashtable.keySet();
                String tempName;
                for (Object t : groupKeys)
                {
                    tempName = (String) t;
                    if (tempName.startsWith(groupName) || groupName.startsWith(tempName))
                    {
                        return (String) ((HashMap) hashtable.get(tempName)).get(itemName);
                    }
                }
            }
            catch (NullPointerException ex1)
            {
                throw new GroupNotFoundException(groupName.concat(" is not exist"));
            }
        }
        return null;
    }

    public void renameGroup(String groupOldName, String groupNewName) throws IncorrectValueException, GroupNotFoundException
    {
        if (groupOldName == null || groupNewName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            hashtable.put(groupNewName, hashtable.get(groupOldName));
            hashtable.remove(groupOldName);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupOldName.concat(" is not exist"));
        }
    }

    public String[] listGroups()
    {
        String[] groups = new String[hashtable.size()];
        Object[] groupKeys = hashtable.keySet().toArray();
        String groupName;
        for (int c = 0; c < groupKeys.length; c++)
        {
            groupName = (String) groupKeys[c];
            groups[c] = groupName;
        }
        return groups;
    }

    public String[] listItems(String groupName) throws IncorrectValueException, GroupNotFoundException
    {
        if (groupName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            String[] items = new String[((HashMap) hashtable.get(groupName)).size()];
            Object[] itemKeys = ((HashMap) hashtable.get(groupName)).keySet().toArray();
            for (int c = 0; c < itemKeys.length; c++)
            {
                groupName = (String) itemKeys[c];
                items[c] = groupName;
            }
            return items;
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public String[] listItems(String groupName, boolean isFullCompare) throws IncorrectValueException, GroupNotFoundException
    {
        if (groupName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        if (isFullCompare || hashtable.containsKey(groupName))
        {
            return listItems(groupName);
        }
        else
        {
            try
            {
                Set groupKeys = hashtable.keySet();
                String tempName;
                for (Object o : groupKeys)
                {
                    tempName = (String) o;
                    if (tempName.startsWith(groupName) || groupName.startsWith(tempName))
                    {
                        String[] items = new String[((HashMap) hashtable.get(tempName)).size()];
                        Object[] itemKeys = ((HashMap) hashtable.get(tempName)).keySet().toArray();
                        for (int c = 0; c < itemKeys.length; c++)
                        {
                            items[c] = (String) itemKeys[c];
                        }
                        return items;
                    }
                }
                return null;
            }
            catch (NullPointerException ex1)
            {
                throw new GroupNotFoundException(groupName.concat(" is not exist"));
            }
        }
    }

    public void renameItem(String groupName, String itemOldName, String itemNewName) throws IncorrectValueException, GroupNotFoundException
    {
        if (groupName == null || itemOldName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            ((HashMap<String, String>) hashtable.get(groupName)).put(itemNewName, ((HashMap<String, String>) hashtable.get(groupName)).get(itemOldName));
            ((HashMap<String, String>) hashtable.get(groupName)).remove(itemOldName);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public void setValue(String groupName, String itemName, String value) throws GroupNotFoundException, IncorrectValueException
    {
        if (groupName == null || itemName == null)
        {
            throw new IncorrectValueException("nulltype is not allowed here as patameter");
        }
        try
        {
            ((HashMap<String, String>) hashtable.get(groupName)).put(itemName, value);
        }
        catch (NullPointerException ex1)
        {
            throw new GroupNotFoundException(groupName.concat(" is not exist"));
        }
    }

    public void removeGroup(String groupName)
    {
        hashtable.remove(groupName);
    }

    public void removeItem(String groupName, String itemName)
    {
        ((HashMap) hashtable.get(groupName)).remove(itemName);
    }

    public void exportToIni(OutputStream outputStream) throws IOException
    {
        Set groupKeys = hashtable.keySet();
        Set itemKeys;
        String itemName;
        String groupName;
        for (Object a : groupKeys)
        {
            groupName = (String) a;
            outputStream.write(StringUtil.stringToByteArray("[".concat(groupName).concat("]\n"), true));
            itemKeys = ((HashMap) hashtable.get(groupName)).keySet();
            for (Object b : itemKeys)
            {
                itemName = (String) b;
                outputStream.write(StringUtil.stringToByteArray(itemName.concat("=").concat((String) ((HashMap) hashtable.get(groupName)).get(itemName)).concat("\n"), true));
            }
        }
        outputStream.flush();
    }

    public String exportToIni()
    {
        StringBuilder out = new StringBuilder();
        Set groupKeys = hashtable.keySet();
        Set itemKeys;
        String itemName;
        String groupName;
        for (Object a : groupKeys)
        {
            groupName = (String) a;
            out.append("[".concat(groupName).concat("]\n"));
            itemKeys = ((HashMap) hashtable.get(groupName)).keySet();
            for (Object b : itemKeys)
            {
                itemName = (String) b;
                out.append(itemName.concat("=").concat((String) ((HashMap) hashtable.get(groupName)).get(itemName)).concat("\n"));
            }
        }
        return out.toString();
    }

    public void saveToDat(DataOutputStream outputStream) throws IOException
    {
        /**
         * [int] groupsCount г [int] groupNameLength | [String] groupName |
         * [int] itemsCount | [int] itemNameLength | [String] itemName | [int]
         * valueLength L [String] value г ---- | ... L ---- ...
         */
        Set groupKeys = hashtable.keySet();
        Set itemKeys;
        String itemName;
        String groupName;
        String value;
        /**
         * Groups count *
         */
        outputStream.writeChar(hashtable.size());
        for (Object a : groupKeys)
        {
            groupName = (String) a;
            /**
             * Group name length *
             */
            //outputStream.writeChar(groupName.length());
            /**
             * Group name *
             */
            outputStream.writeUTF(groupName);
            itemKeys = ((HashMap) hashtable.get(groupName)).keySet();
            /**
             * Items count *
             */
            outputStream.writeChar(((HashMap) hashtable.get(groupName)).size());
            for (Object b : itemKeys)
            {
                itemName = (String) b;
                /**
                 * Item name length *
                 */
                //outputStream.writeChar(itemName.length());
                /**
                 * Item name *
                 */
                outputStream.writeUTF(itemName);
                value = (String) ((HashMap) hashtable.get(groupName)).get(itemName);
                /**
                 * Value length *
                 */
                //outputStream.writeChar(value.length());
                /**
                 * Value *
                 */
                outputStream.writeUTF(value);
            }
        }
        outputStream.flush();
    }

    public void readFromDat(DataInputStream inputStream) throws IOException, IncorrectValueException, GroupNotFoundException, EOFException
    {
        /**
         * [int] groupsCount г [int] groupNameLength | [String] groupName |
         * [int] itemsCount | [int] itemNameLength | [String] itemName | [int]
         * valueLength L [String] value г ---- | ... L ---- ...
         */
        hashtable.clear();
        int groupCount = inputStream.readChar();
        String groupName;
        String itemName;
        String value;
        for (int c = 0; c < groupCount; c++)
        {
            groupName = inputStream.readUTF();
            // System.out.println("[".concat(groupName).concat("]"));
            addGroup(groupName);
            int itemsCount = inputStream.readChar();
            for (int i = 0; i < itemsCount; i++)
            {
                itemName = inputStream.readUTF();
                value = inputStream.readUTF();
                // System.out.println(itemName.concat("=").concat(value));
                addItem(groupName, itemName, value);
            }
        }
    }

    public void importFromIni(DataInputStream inputStream) throws IOException, IncorrectValueException, GroupNotFoundException, EOFException, Throwable
    {
        hashtable.clear();
        byte ch;
        String prevHeader = null;
        boolean isFirstIndex = true;
        ArrayUtil buffer = new ArrayUtil();
        while (inputStream.available() > 0 && (ch = inputStream.readByte()) != -1)
        {
            if (ch == 13)
            {
                continue;
            }
            if (ch == 10)
            {
                if (buffer.length() <= 1)
                {
                    continue;
                }
                if (buffer.byteString[0] == '[' && buffer.byteString[buffer.length() - 1] == ']')
                {
                    prevHeader = StringUtil.byteArrayToString(buffer.subarray(1, buffer.length() - 1), true);
                    // System.out.println("[".concat(prevHeader).concat("]"));
                    addGroup(prevHeader);
                }
                else
                {
                    int equivIndex;
                    if (isFirstIndex)
                    {
                        equivIndex = buffer.indexOf('=');
                    }
                    else
                    {
                        equivIndex = buffer.lastIndexOf('=');
                    }
                    if (equivIndex > 0)
                    {
                        // System.out.println((StringUtil.byteArrayToString(buffer.substring(0, equivIndex), true)).concat("=").concat(StringUtil.byteArrayToString(buffer.substring(equivIndex + 1, buffer.length()), true)));
                        addItem(prevHeader,
                                StringUtil.byteArrayToString(buffer.subarray(0, equivIndex), true),
                                StringUtil.byteArrayToString(buffer.subarray(equivIndex + 1, buffer.length()), true));
                    }
                }
                buffer.clear();
                continue;
            }
            buffer.append(ch);
        }
    }
}
