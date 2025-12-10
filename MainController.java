package controllers;

import javafx.application.Application;
import views.PRApplication;

public class MainController {
    public static void main(String[] args){
        System.out.print("Project is starting...");
        Application.launch(PRApplication.class, args);
        System.out.print("Project has started...");
    }
}