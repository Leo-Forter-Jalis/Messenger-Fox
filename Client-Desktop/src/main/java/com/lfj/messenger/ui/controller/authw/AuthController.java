package com.lfj.messenger.ui.controller.authw;

import com.lfj.dev.annotations.EventBusPublisher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

public class AuthController {
    private Logger logger;

    @FXML private Label title;
    @FXML private TextField emailField;
    @FXML private PasswordField passField;
    @FXML private Label label;
    @FXML private Button auth;
    public void initialize(){
        logger = LoggerFactory.getLogger(AuthController.class);
        label.setOnMouseClicked(this::labelEvent);
    }
    private void labelEvent(MouseEvent event){
        logger.info("label clicked");
    }
    @FXML
    @EventBusPublisher
    private void onAction(){
        logger.info("button clicked");
    }
}
