package org.ln.hellofx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginApp extends Application {
    @Override
    public void start(Stage stage) {
    	// 1. VBox Principale (Centra tutto nella finestra)
    	VBox root = new VBox(20);
    	root.setPadding(new Insets(30));
    	root.setAlignment(Pos.CENTER);

    	// 2. Creiamo un contenitore per il form per limitarne la larghezza
    	// Se non lo facciamo, i campi di testo diventeranno lunghi quanto tutta la finestra
    	VBox formContainer = new VBox(15);
    	formContainer.setMaxWidth(400); // Impedisce al form di diventare troppo largo
    	formContainer.setAlignment(Pos.CENTER);

    	// 3. Riga Utente (HBox)
    	HBox rigaUser = new HBox(10);
    	rigaUser.setAlignment(Pos.CENTER_LEFT); // Allineamento interno a sinistra
    	Label lblUser = new Label("Username:");
    	lblUser.setPrefWidth(80); // Larghezza fissa per la label così le textfield si allineano
    	TextField txtUser = new TextField();
    	HBox.setHgrow(txtUser, Priority.ALWAYS); // Elasticità al campo di testo
    	rigaUser.getChildren().addAll(lblUser, txtUser);

    	// 4. Riga Password (HBox)
    	HBox rigaPass = new HBox(10);
    	rigaPass.setAlignment(Pos.CENTER_LEFT);
    	Label lblPass = new Label("Password:");
    	lblPass.setPrefWidth(80); // Stessa larghezza della label sopra
    	PasswordField txtPass = new PasswordField();
    	HBox.setHgrow(txtPass, Priority.ALWAYS); // Elasticità
    	rigaPass.getChildren().addAll(lblPass, txtPass);

    	// Aggiungiamo i componenti al contenitore del form
    	formContainer.getChildren().addAll(rigaUser, rigaPass);

    	// Aggiungiamo tutto al root
    	root.getChildren().addAll(new Label("BENVENUTO"), formContainer, new Button("Accedi"));

        Scene scene = new Scene(root, 350, 250);
        
        stage.setTitle("Esempio Layout Annidati");
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}