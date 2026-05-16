package rcr.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rcr.ui.classical.ClassicalLogicController;
import rcr.ui.defaut.DefaultLogicController;
import rcr.ui.modal.ModalLogicController;
import rcr.ui.semantic.SemanticNetworkController;

public class DashboardController {

    @FXML
    public void openModal() {
        Stage stage = new Stage();
        stage.setTitle("Logique Modale — Modèles de Kripke");
        stage.setScene(new Scene(new ModalLogicController().buildUI(), 1100, 700));
        stage.show();
    }

    @FXML
    public void openDefaut() {
        Stage stage = new Stage();
        stage.setTitle("Logique des Défauts — Extensions de Reiter");
        stage.setScene(new Scene(new DefaultLogicController().buildUI(), 920, 680));
        stage.show();
    }

    @FXML
    public void openClassical() {
        Stage stage = new Stage();
        stage.setTitle("Logique Classique — Propositionnelle & Prédicats");
        stage.setScene(new Scene(new ClassicalLogicController().buildUI(), 980, 720));
        stage.show();
    }

    @FXML
    public void openSemantic() {
        Stage stage = new Stage();
        stage.setTitle("Réseaux Sémantiques — Propagation de Marqueurs");
        stage.setScene(new Scene(new SemanticNetworkController().buildUI(), 1150, 720));
        stage.show();
    }
}
