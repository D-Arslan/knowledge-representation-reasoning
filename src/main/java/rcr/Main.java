package rcr;

import rcr.classical.ClassicalLogicExample;
import rcr.modal.ModalLogicExample;
import rcr.defaut.DefaultLogicExample;
import rcr.description.DescriptionLogicExample;
import rcr.semantic.SemanticNetworkExample;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Projet RCR - Logiques Formelles ===\n");

        System.out.println("--- 1. Logique Classique (Propositionnelle + Predicats) ---");
        new ClassicalLogicExample().run();

        System.out.println("\n--- 2. Logique Modale ---");
        new ModalLogicExample().run();

        System.out.println("\n--- 3. Logique des Defauts ---");
        new DefaultLogicExample().run();

        System.out.println("\n--- 4. Logique de Description ---");
        new DescriptionLogicExample().run();

        System.out.println("\n--- 5. Reseaux Semantiques ---");
        new SemanticNetworkExample().run();

        System.out.println("\n=== Fin ===");
    }
}
