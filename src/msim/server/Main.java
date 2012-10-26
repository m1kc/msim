/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package msim.server;

/**
 *
 * @author m1kc
 */
public class Main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Log.init();
        Log.print("Взлетаем, типа.");

        try
        {
            Log.print("Инициализация хранилища...");
            Storage.init();
        }
        catch (Throwable ex)
        {
            Log.error("С базой какой-то пиздец. Прерываем запуск.", ex);
            System.exit(1);
        }

        Log.print("Пока всё нормально. Поднимаем сервер.");

        new Server(3215).start();
    }
}
