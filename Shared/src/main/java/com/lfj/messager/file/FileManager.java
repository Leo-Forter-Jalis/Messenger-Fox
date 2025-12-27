package com.lfj.messager.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfj.messager.logger.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum FileManager {
     INSTANCE();
     private Logger logger;
     private Path baseDir;
     private Map<String, Path> pathDirectory = new ConcurrentHashMap<>();
     private Map<String, File> pathFiles = new ConcurrentHashMap<>();
     FileManager(){
        this.logger = new Logger(FileManager.class);
        this.baseDir = determineBaseDirectory();
        initialize();
     }
     private Path determineBaseDirectory(){
         String os = System.getProperty("os.name").toLowerCase();
         String userHome = System.getProperty("user.home");
         logger.log(Level.INFO,  String.format("Информация системы: %s - %s", os, userHome));
         if(os.startsWith("windows")) return Paths.get(userHome, "AppData", "Roaming", "MessFox");
         else if(os.startsWith("mac")) return Paths.get(userHome, "Library", "Application Support", "MessFox");
         else return Paths.get(userHome, ".config", "MessFox");
     }
     private void initialize(){
         try {
             if (!Files.exists(baseDir)) {
                 Files.createDirectories(baseDir);
             }
             logger.log(Level.INFO, String.format("Путь baseDir: '%s'", baseDir.toString()));
             logger.log(Level.INFO, "Сканирование директории...");
             scanDirectory(baseDir.toFile());
         }catch (IOException e){
             logger.log(Level.FATAL, String.format("Произошла ошибка инициализации файлового менеджера. \n%s", e.getMessage()));
             throw new RuntimeException("Failed initialization FileManager");
         }
     }
     private void scanDirectory(File directory){
         logger.log(Level.INFO, String.format("Текущая директория: '%s'", directory.getAbsolutePath()));
         File[] files = directory.listFiles();
         for(File file : files){
             logger.log(Level.INFO, file.getAbsolutePath());
             if(file.isDirectory()){
                 logger.log(Level.INFO, String.format("Файл: '%s' является директорией", file.getName()));
                 pathDirectory.put(file.getName(), file.toPath());
                 scanDirectory(file);
                 logger.log(Level.INFO, String.format("Вернулись в %s", directory.getAbsolutePath()));
             }else{
                 logger.log(Level.INFO, String.format("Файл: '%s' является файлом", file.getName()));
                 pathFiles.put(file.getName(), file);
             }
         }
     }
}
