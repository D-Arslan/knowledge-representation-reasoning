package rcr.modal;

import rcr.modal.temporal.TemporalLogicJustice;
import rcr.modal.temporal.TemporalLogicMusic;

public class ModalLogicExample {

    public void run() {
        // --- Logique modale classique (Kripke generique) ---
        ModalLogicMusic.run();
        System.out.println();
        ModalLogicJustice.run();

        // --- Logique modale temporelle (operateurs G, F, H, P) ---
        // Chapitre 3 du cours : la relation d'accessibilite devient un ordre
        // temporel, et la verite d'une proposition change dans le temps.
        System.out.println();
        System.out.println("===========================================================");
        System.out.println("  EXTENSION : LOGIQUE MODALE TEMPORELLE (cours chap. 3)");
        System.out.println("===========================================================");
        System.out.println();
        TemporalLogicMusic.run();
        System.out.println();
        TemporalLogicJustice.run();
    }
}
