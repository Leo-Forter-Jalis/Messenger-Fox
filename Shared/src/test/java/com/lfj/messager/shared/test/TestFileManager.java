package com.lfj.messager.shared.test;

import com.lfj.messager.logger.Logger;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lfj.messager.file.FileManager;

import java.io.IOException;
import java.nio.file.Path;

public class TestFileManager {

    @Test
    public void test() throws InterruptedException {
        /*
        FileManager fileManager = FileManager.getInstance();
        String fileTest = "Test.json";
        String jsonTest = "..\\.\\config\\Test.json";
        String text = "LOLOLOLOOLOO";
        try {
            fileManager.writeJson(fileTest, text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThrows(Exception.class, () -> {fileManager.writeJson(jsonTest, text);});
        */
        new Logger(TestFileManager.class).log(Level.INFO, "Запуск потока FileManager...");
        Thread t = new Thread(() ->{
            FileManager f = FileManager.INSTANCE;
        }, "FileManager");
        t.start();
        Thread.sleep(3000);
        t.join(5000);
    }
}
