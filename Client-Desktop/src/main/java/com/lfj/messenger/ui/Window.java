package com.lfj.messenger.ui;

import com.lfj.dev.annotations.ActiveDevelopment;
import com.lfj.dev.annotations.Priority;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.ShutdownEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

@ActiveDevelopment(priority = Priority.MEDIUM)
public class Window {
    private EventBus eventBus;
    private Window(){  }
    public Window(EventBus eventBus){
        this.eventBus = eventBus;
    }
    public void start(Stage stage) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setWidth(1000); stage.setHeight(500);
        stage.show();
        stage.setOnCloseRequest(value -> this.eventBus.publishAsync(new ShutdownEvent()));
    }
}
