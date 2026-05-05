package rcr.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rcr.ui.defaut.DefaultLogicController;
import rcr.ui.modal.ModalLogicController;

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
}
