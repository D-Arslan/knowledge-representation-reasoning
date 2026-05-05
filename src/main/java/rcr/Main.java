package rcr;

import rcr.modal.ModalLogicExample;
import rcr.defaut.DefaultLogicExample;
import rcr.semantic.SemanticNetworkExample;
import rcr.description.DescriptionLogicExample;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Projet RCR - Logiques Formelles ===\n");

        System.out.println("--- 1. Logique Modale ---");
        ModalLogicExample modal = new ModalLogicExample();
        modal.run();

        System.out.println("\n--- 2. Logique des Défauts ---");
        DefaultLogicExample defaut = new DefaultLogicExample();
        defaut.run();

        System.out.println("\n--- 3. Réseaux Sémantiques ---");
        SemanticNetworkExample semantic = new SemanticNetworkExample();
        semantic.run();

        System.out.println("\n--- 4. Logique de Description ---");
        DescriptionLogicExample description = new DescriptionLogicExample();
        description.run();

        System.out.println("\n=== Fin ===");
    }
}
