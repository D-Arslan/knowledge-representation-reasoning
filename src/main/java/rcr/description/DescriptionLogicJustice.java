package rcr.description;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Logique de Description — domaine : système judiciaire.
 * Ontologie OWL construite via OWL API 4.5, raisonnée par HermiT.
 *
 * TBox : Personne, ActeurJudiciaire, Magistrat, Juge, Procureur, Avocat,
 *        Accusé, Infraction, Crime, Délit, Tribunal, CourAssises,
 *        AccuséMineur, AccuséRécidiviste, AccuséPrésuméInnocent,
 *        CasSimple, CasComplexe…
 * ABox : ali, omar, karim, juge_salem, avocat_riad, procureur_malik,
 *        temoignage_ali, antecedent_omar, inculpation_karim, crime_karim…
 */
public class DescriptionLogicJustice {

    private static final String NS = "http://rcr1/justice#";

    private static IRI iri(String local) {
        return IRI.create(NS + local);
    }

    private static void ax(OWLOntologyManager m, OWLOntology o, OWLAxiom a) {
        m.addAxiom(o, a);
    }

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------
    public static void run() {
        System.out.println("=== Logique de Description : Systeme Judiciaire ===\n");
        try {
            // ===== Setup =====
            OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
            OWLOntology onto = mgr.createOntology(IRI.create("http://rcr1/justice"));
            OWLDataFactory f = mgr.getOWLDataFactory();

            // ===== Classes =====
            OWLClass Personne             = f.getOWLClass(iri("Personne"));
            OWLClass ActeurJudiciaire     = f.getOWLClass(iri("ActeurJudiciaire"));
            OWLClass Magistrat            = f.getOWLClass(iri("Magistrat"));
            OWLClass Juge                 = f.getOWLClass(iri("Juge"));
            OWLClass Procureur            = f.getOWLClass(iri("Procureur"));
            OWLClass Avocat               = f.getOWLClass(iri("Avocat"));
            OWLClass Accuse               = f.getOWLClass(iri("Accuse"));
            OWLClass Temoin               = f.getOWLClass(iri("Temoin"));
            OWLClass Infraction           = f.getOWLClass(iri("Infraction"));
            OWLClass Crime                = f.getOWLClass(iri("Crime"));
            OWLClass Delit                = f.getOWLClass(iri("Delit"));
            OWLClass Contravention        = f.getOWLClass(iri("Contravention"));
            OWLClass CrimeOrganise        = f.getOWLClass(iri("CrimeOrganise"));
            OWLClass CrimeGrave           = f.getOWLClass(iri("CrimeGrave"));
            OWLClass Tribunal             = f.getOWLClass(iri("Tribunal"));
            OWLClass CourAssises          = f.getOWLClass(iri("CourAssises"));
            OWLClass TribunalCorrectionnel = f.getOWLClass(iri("TribunalCorrectionnel"));
            OWLClass TribunalEnfants      = f.getOWLClass(iri("TribunalEnfants"));
            OWLClass AccuseMineur         = f.getOWLClass(iri("AccuseMineur"));
            OWLClass AccuseRecidiviste    = f.getOWLClass(iri("AccuseRecidiviste"));
            OWLClass AccusePresumInnocent = f.getOWLClass(iri("AccusePresumInnocent"));
            OWLClass Condamne             = f.getOWLClass(iri("Condamne"));
            OWLClass AccuseAvecTemoignage = f.getOWLClass(iri("AccuseAvecTemoignage"));
            OWLClass CasSimple            = f.getOWLClass(iri("CasSimple"));
            OWLClass CasComplexe          = f.getOWLClass(iri("CasComplexe"));
            OWLClass Inculpation          = f.getOWLClass(iri("Inculpation"));
            OWLClass Condamnation         = f.getOWLClass(iri("Condamnation"));
            OWLClass TemoignageFavorable  = f.getOWLClass(iri("TemoignageFavorable"));
            OWLClass Peine                = f.getOWLClass(iri("Peine"));
            OWLClass PeineReclusion       = f.getOWLClass(iri("PeineReclusion"));
            OWLClass PeineEmprisonnement  = f.getOWLClass(iri("PeineEmprisonnement"));
            OWLClass PeineAmende          = f.getOWLClass(iri("PeineAmende"));
            OWLClass Audience             = f.getOWLClass(iri("Audience"));
            OWLClass Proces               = f.getOWLClass(iri("Proces"));
            OWLClass Organisation         = f.getOWLClass(iri("Organisation"));
            OWLClass InfractionMineur     = f.getOWLClass(iri("InfractionMineur"));
            OWLClass MoinsDe18Ans         = f.getOWLClass(iri("MoinsDe18Ans"));
            OWLClass PrejudiceGrave       = f.getOWLClass(iri("PrejudiceGrave"));

            // ===== Object Properties =====
            OWLObjectProperty participe_a  = f.getOWLObjectProperty(iri("participe_a"));
            OWLObjectProperty siege_a      = f.getOWLObjectProperty(iri("siege_a"));
            OWLObjectProperty preside      = f.getOWLObjectProperty(iri("preside"));
            OWLObjectProperty requiert     = f.getOWLObjectProperty(iri("requiert"));
            OWLObjectProperty defend       = f.getOWLObjectProperty(iri("defend"));
            OWLObjectProperty temoigne_dans = f.getOWLObjectProperty(iri("temoigne_dans"));
            OWLObjectProperty fait_objet   = f.getOWLObjectProperty(iri("fait_objet"));
            OWLObjectProperty beneficie    = f.getOWLObjectProperty(iri("beneficie"));
            OWLObjectProperty a_antecedent = f.getOWLObjectProperty(iri("a_antecedent"));
            OWLObjectProperty a_age        = f.getOWLObjectProperty(iri("a_age"));
            OWLObjectProperty puni_par     = f.getOWLObjectProperty(iri("puni_par"));
            OWLObjectProperty cause        = f.getOWLObjectProperty(iri("cause"));
            OWLObjectProperty implique     = f.getOWLObjectProperty(iri("implique"));
            OWLObjectProperty juge_par     = f.getOWLObjectProperty(iri("juge_par"));
            OWLObjectProperty defendu_par  = f.getOWLObjectProperty(iri("defendu_par"));
            OWLObjectProperty juge_prop    = f.getOWLObjectProperty(iri("juge"));

            // ===== TBox =====

            // Hiérarchie de base
            ax(mgr, onto, f.getOWLSubClassOfAxiom(ActeurJudiciaire, Personne));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Accuse, Personne));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Magistrat, ActeurJudiciaire));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(PeineReclusion, Peine));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(PeineEmprisonnement, Peine));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(PeineAmende, Peine));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(CourAssises, Tribunal));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(TribunalCorrectionnel, Tribunal));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(TribunalEnfants, Tribunal));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(CrimeGrave, Crime));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(CrimeOrganise, Crime));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Crime, Infraction));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Delit, Infraction));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Contravention, Infraction));

            // ActeurJudiciaire ≡ Personne ⊓ ∃participe_a.Proces
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ActeurJudiciaire,
                f.getOWLObjectIntersectionOf(Personne,
                    f.getOWLObjectSomeValuesFrom(participe_a, Proces))));

            // Magistrat ≡ ActeurJudiciaire ⊓ ∃siege_a.Tribunal
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Magistrat,
                f.getOWLObjectIntersectionOf(ActeurJudiciaire,
                    f.getOWLObjectSomeValuesFrom(siege_a, Tribunal))));

            // Juge ≡ Magistrat ⊓ ∃preside.Audience
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Juge,
                f.getOWLObjectIntersectionOf(Magistrat,
                    f.getOWLObjectSomeValuesFrom(preside, Audience))));

            // Procureur ≡ Magistrat ⊓ ∃requiert.Peine
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Procureur,
                f.getOWLObjectIntersectionOf(Magistrat,
                    f.getOWLObjectSomeValuesFrom(requiert, Peine))));

            // Avocat ≡ ActeurJudiciaire ⊓ ∃defend.Accuse
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Avocat,
                f.getOWLObjectIntersectionOf(ActeurJudiciaire,
                    f.getOWLObjectSomeValuesFrom(defend, Accuse))));

            // Accuse ≡ Personne ⊓ ∃fait_objet.Inculpation
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Accuse,
                f.getOWLObjectIntersectionOf(Personne,
                    f.getOWLObjectSomeValuesFrom(fait_objet, Inculpation))));

            // Temoin ≡ ActeurJudiciaire ⊓ ∃temoigne_dans.Proces
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Temoin,
                f.getOWLObjectIntersectionOf(ActeurJudiciaire,
                    f.getOWLObjectSomeValuesFrom(temoigne_dans, Proces))));

            // Crime ≡ Infraction ⊓ ∃puni_par.PeineReclusion
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Crime,
                f.getOWLObjectIntersectionOf(Infraction,
                    f.getOWLObjectSomeValuesFrom(puni_par, PeineReclusion))));

            // Delit ≡ Infraction ⊓ ∃puni_par.PeineEmprisonnement
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Delit,
                f.getOWLObjectIntersectionOf(Infraction,
                    f.getOWLObjectSomeValuesFrom(puni_par, PeineEmprisonnement))));

            // Contravention ≡ Infraction ⊓ ∃puni_par.PeineAmende
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Contravention,
                f.getOWLObjectIntersectionOf(Infraction,
                    f.getOWLObjectSomeValuesFrom(puni_par, PeineAmende))));

            // CrimeOrganise ≡ Crime ⊓ ∃implique.Organisation
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CrimeOrganise,
                f.getOWLObjectIntersectionOf(Crime,
                    f.getOWLObjectSomeValuesFrom(implique, Organisation))));

            // CrimeGrave ≡ Crime ⊓ ∃cause.PrejudiceGrave
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CrimeGrave,
                f.getOWLObjectIntersectionOf(Crime,
                    f.getOWLObjectSomeValuesFrom(cause, PrejudiceGrave))));

            // CourAssises ≡ Tribunal ⊓ ∀juge.Crime
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CourAssises,
                f.getOWLObjectIntersectionOf(Tribunal,
                    f.getOWLObjectAllValuesFrom(juge_prop, Crime))));

            // TribunalCorrectionnel ≡ Tribunal ⊓ ∀juge.Delit
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(TribunalCorrectionnel,
                f.getOWLObjectIntersectionOf(Tribunal,
                    f.getOWLObjectAllValuesFrom(juge_prop, Delit))));

            // TribunalEnfants ≡ Tribunal ⊓ ∀juge.InfractionMineur
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(TribunalEnfants,
                f.getOWLObjectIntersectionOf(Tribunal,
                    f.getOWLObjectAllValuesFrom(juge_prop, InfractionMineur))));

            // AccuseMineur ≡ Accuse ⊓ ∃a_age.MoinsDe18Ans
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AccuseMineur,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectSomeValuesFrom(a_age, MoinsDe18Ans))));

            // AccuseRecidiviste ≡ Accuse ⊓ ∃a_antecedent.Condamnation
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AccuseRecidiviste,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectSomeValuesFrom(a_antecedent, Condamnation))));

            // AccusePresumInnocent ≡ Accuse ⊓ ¬Condamne
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AccusePresumInnocent,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectComplementOf(Condamne))));

            // AccuseAvecTemoignage ≡ Accuse ⊓ ∃beneficie.TemoignageFavorable
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AccuseAvecTemoignage,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectSomeValuesFrom(beneficie, TemoignageFavorable))));

            // CasSimple ≡ Accuse ⊓ ¬AccuseRecidiviste ⊓ ¬AccuseMineur
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CasSimple,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectComplementOf(AccuseRecidiviste),
                    f.getOWLObjectComplementOf(AccuseMineur))));

            // CasComplexe ≡ Accuse ⊓ (AccuseRecidiviste ⊔ AccuseMineur)
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CasComplexe,
                f.getOWLObjectIntersectionOf(Accuse,
                    f.getOWLObjectUnionOf(AccuseRecidiviste, AccuseMineur))));

            // ===== Individuals =====
            OWLNamedIndividual ali               = f.getOWLNamedIndividual(iri("ali"));
            OWLNamedIndividual omar              = f.getOWLNamedIndividual(iri("omar"));
            OWLNamedIndividual karim             = f.getOWLNamedIndividual(iri("karim"));
            OWLNamedIndividual jugeSalem         = f.getOWLNamedIndividual(iri("juge_salem"));
            OWLNamedIndividual avocatRiad        = f.getOWLNamedIndividual(iri("avocat_riad"));
            OWLNamedIndividual procureurMalik    = f.getOWLNamedIndividual(iri("procureur_malik"));
            OWLNamedIndividual temoignageAli     = f.getOWLNamedIndividual(iri("temoignage_ali"));
            OWLNamedIndividual antecedentOmar    = f.getOWLNamedIndividual(iri("antecedent_omar"));
            OWLNamedIndividual inculpationKarim  = f.getOWLNamedIndividual(iri("inculpation_karim"));
            OWLNamedIndividual crimeKarim        = f.getOWLNamedIndividual(iri("crime_karim"));
            // Tribunaux — individus (punning OWL 2 : même IRI que les classes)
            OWLNamedIndividual courAssisesInd    = f.getOWLNamedIndividual(iri("cour_assises"));
            OWLNamedIndividual tcInd             = f.getOWLNamedIndividual(iri("trib_correctionnel"));
            OWLNamedIndividual teInd             = f.getOWLNamedIndividual(iri("trib_enfants"));

            // UNA — tous différents
            ax(mgr, onto, f.getOWLDifferentIndividualsAxiom(
                ali, omar, karim,
                jugeSalem, avocatRiad, procureurMalik,
                temoignageAli, antecedentOmar, inculpationKarim, crimeKarim,
                courAssisesInd, tcInd, teInd));

            // ===== ABox — assertions de classes =====

            // Accusés — assertion directe (Accuse) + faits nécessaires pour les requêtes
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Accuse, ali));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Accuse, omar));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Accuse, karim));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(Juge, jugeSalem));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Avocat, avocatRiad));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Procureur, procureurMalik));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(TemoignageFavorable, temoignageAli));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Condamnation, antecedentOmar));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Inculpation, inculpationKarim));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(CrimeGrave, crimeKarim));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(CourAssises, courAssisesInd));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(TribunalCorrectionnel, tcInd));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(TribunalEnfants, teInd));

            // ali : AccusePresumInnocent — pas de condamnation (assertion explicite, OWA local)
            ax(mgr, onto, f.getOWLClassAssertionAxiom(AccusePresumInnocent, ali));

            // karim : AccuseMineur — jugé par tribunal enfants (assertion explicite)
            ax(mgr, onto, f.getOWLClassAssertionAxiom(AccuseMineur, karim));

            // ali : CasSimple — pas récidiviste, pas mineur (assertion explicite, OWA local)
            ax(mgr, onto, f.getOWLClassAssertionAxiom(CasSimple, ali));

            // ===== ABox — assertions de propriétés =====
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(beneficie, ali, temoignageAli));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(a_antecedent, omar, antecedentOmar));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(fait_objet, karim, crimeKarim));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(defendu_par, ali, avocatRiad));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(juge_par, omar, tcInd));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(juge_par, karim, teInd));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(siege_a, jugeSalem, courAssisesInd));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(siege_a, procureurMalik, courAssisesInd));

            // ===== Raisonneur HermiT =====
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner reasoner = rf.createReasoner(onto);

            System.out.println("Ontologie : http://rcr1/justice");
            System.out.println("ABox      : ali, omar, karim | juge_salem, avocat_riad, procureur_malik" +
                               " | temoignages, antecedents, crimes");
            System.out.println();

            // ===== R1 =====
            System.out.println("[DL] Requete R1: ali est-il un AccusePresumInnocent ?");
            System.out.println("[DL] Individu: ali");
            boolean r1 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(AccusePresumInnocent, ali));
            System.out.println("[DL] Resultat: " + (r1 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: ali:AccusePresumInnocent asserte;" +
                               " AccusePresumInnocent ≡ Accuse ⊓ ¬Condamne => ali NOT Condamne");
            System.out.println("[DL] Coherence avec DefaultLogic: d1 (Accuse => Innocent) s'applique pour ali");
            System.out.println();

            // ===== R2 =====
            System.out.println("[DL] Requete R2: ali est-il un AccuseAvecTemoignage ?");
            System.out.println("[DL] Individu: ali");
            boolean r2 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(AccuseAvecTemoignage, ali));
            System.out.println("[DL] Resultat: " + (r2 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: ali:Accuse + beneficie(ali,temoignage_ali:TemoignageFavorable)" +
                               " => ali in ExisteBeneficie.TemoignageFavorable => AccuseAvecTemoignage");
            System.out.println("[DL] Coherence avec DefaultLogic: d5 (Temoignage => Acquittement) pour ali");
            System.out.println();

            // ===== R3 =====
            System.out.println("[DL] Requete R3: omar est-il un AccuseRecidiviste ?");
            System.out.println("[DL] Individu: omar");
            boolean r3 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(AccuseRecidiviste, omar));
            System.out.println("[DL] Resultat: " + (r3 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: omar:Accuse + a_antecedent(omar,antecedent_omar:Condamnation)" +
                               " => omar in ExisteAntecedent.Condamnation => AccuseRecidiviste");
            System.out.println("[DL] Coherence avec DefaultLogic: d2+d6 pour omar (PeineLourde, !Innocent)");
            System.out.println();

            // ===== R4 =====
            System.out.println("[DL] Requete R4: karim est-il un CasComplexe ?");
            System.out.println("[DL] Individu: karim");
            // karim:AccuseMineur (asserte) => karim in AccuseRecidiviste ⊔ AccuseMineur => CasComplexe
            boolean r4 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(CasComplexe, karim));
            System.out.println("[DL] Resultat: " + (r4 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: karim:AccuseMineur (asserte) => karim in" +
                               " AccuseRecidiviste ⊔ AccuseMineur => CasComplexe");
            System.out.println("[DL] Coherence avec DefaultLogic: d3 vs d4 conflit (TribunalEnfants vs CrimeGrave)");
            System.out.println();

            // ===== R5 =====
            System.out.println("[DL] Requete R5: ali est-il un CasSimple ?");
            System.out.println("[DL] Individu: ali");
            boolean r5 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(CasSimple, ali));
            System.out.println("[DL] Resultat: " + (r5 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: ali:CasSimple asserte;" +
                               " CasSimple ≡ Accuse ⊓ ¬AccuseRecidiviste ⊓ ¬AccuseMineur");
            System.out.println("[DL] Coherence avec DefaultLogic: d1+d5 sans blocage pour ali");
            System.out.println();

            // ===== R6 =====
            System.out.println("[DL] Requete R6: Quelles sont toutes les instances de CasComplexe ?");
            Set<OWLNamedIndividual> complexes = reasoner.getInstances(CasComplexe, false).getFlattened();
            String complexesStr = complexes.stream()
                .map(i -> i.getIRI().getFragment())
                .sorted()
                .collect(Collectors.joining(", "));
            System.out.println("[DL] Resultat: {" + complexesStr + "}");
            System.out.println("[DL] Justification: omar:AccuseRecidiviste (derive) + karim:AccuseMineur (asserte)" +
                               " => tous deux in CasComplexe");
            System.out.println("[DL] Coherence avec DefaultLogic: extensions avec conflits pour omar et karim");
            System.out.println();

            // ===== R7 =====
            System.out.println("[DL] Requete R7: Quelles sont toutes les instances de CasSimple ?");
            Set<OWLNamedIndividual> simples = reasoner.getInstances(CasSimple, false).getFlattened();
            String simplesStr = simples.stream()
                .map(i -> i.getIRI().getFragment())
                .sorted()
                .collect(Collectors.joining(", "));
            System.out.println("[DL] Resultat: {" + simplesStr + "}");
            System.out.println("[DL] Justification: ali:CasSimple asserte; omar/karim exclus par CasComplexe");
            System.out.println("[DL] Coherence avec DefaultLogic: d1+d5 => extension unique pour ali");
            System.out.println();

            // ===== R8 =====
            System.out.println("[DL] Requete R8: AccuseRecidiviste est-il subsume par Accuse ?");
            boolean r8 = reasoner.isEntailed(f.getOWLSubClassOfAxiom(AccuseRecidiviste, Accuse));
            System.out.println("[DL] Resultat: " + (r8 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: AccuseRecidiviste ≡ Accuse ⊓ ... => AccuseRecidiviste ⊑ Accuse");
            System.out.println("[DL] Coherence avec DefaultLogic: specialisation du concept Accuse");
            System.out.println();

            // ===== R9 =====
            System.out.println("[DL] Requete R9: CasSimple et CasComplexe sont-ils disjoints ?");
            boolean r9 = reasoner.isEntailed(f.getOWLDisjointClassesAxiom(CasSimple, CasComplexe));
            System.out.println("[DL] Resultat: " + (r9 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: CasSimple ≡ ¬Recidiviste ⊓ ¬Mineur ⊓ Accuse;" +
                               " CasComplexe ≡ (Recidiviste ⊔ Mineur) ⊓ Accuse => intersection = ⊥");
            System.out.println("[DL] Coherence avec DefaultLogic: extensions sans conflits vs avec conflits");
            System.out.println();

            // ===== R10 =====
            System.out.println("[DL] Requete R10: Juge et Procureur sont-ils disjoints ?");
            boolean r10 = reasoner.isEntailed(f.getOWLDisjointClassesAxiom(Juge, Procureur));
            System.out.println("[DL] Resultat: " + (r10 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: Juge ≡ Magistrat ⊓ ExistePreside.Audience;" +
                               " Procureur ≡ Magistrat ⊓ ExisteRequiert.Peine;" +
                               " non declares disjoints => un meme individu pourrait etre les deux");
            System.out.println("[DL] Coherence avec DefaultLogic: aucune regle de defaut ne les disjoint");
            System.out.println();

            // ===== R11 — Cohérence avec DefaultLogicJustice =====
            System.out.println("[DL] Requete R11: Demonstration coherence avec DefaultLogicJustice");
            System.out.println();

            // ali
            boolean aliPresumInnocent = reasoner.isEntailed(
                f.getOWLClassAssertionAxiom(AccusePresumInnocent, ali));
            boolean aliAvecTemoignage = reasoner.isEntailed(
                f.getOWLClassAssertionAxiom(AccuseAvecTemoignage, ali));
            System.out.println("[DL] ali : AccusePresumInnocent=" + (aliPresumInnocent ? "VRAI" : "FAUX")
                + " + AccuseAvecTemoignage=" + (aliAvecTemoignage ? "VRAI" : "FAUX"));
            System.out.println("[DL]   => coherent avec extension DefaultLogic {Innocent(ali), Acquittement(ali)}");
            System.out.println("[DL]      d1 (Accuse=>Innocent) + d5 (Temoignage=>Acquittement), aucun conflit");
            System.out.println();

            // omar
            boolean omarRecidiviste = reasoner.isEntailed(
                f.getOWLClassAssertionAxiom(AccuseRecidiviste, omar));
            boolean omarComplexe = reasoner.isEntailed(
                f.getOWLClassAssertionAxiom(CasComplexe, omar));
            System.out.println("[DL] omar : AccuseRecidiviste=" + (omarRecidiviste ? "VRAI" : "FAUX")
                + " + CasComplexe=" + (omarComplexe ? "VRAI" : "FAUX"));
            System.out.println("[DL]   => coherent avec extensions DefaultLogic {PeineLourde(omar)...}");
            System.out.println("[DL]      d2 (Recidiviste=>PeineLourde) sans conflit;" +
                               " d1 vs d6 => conflit sur Innocent");
            System.out.println();

            // karim
            boolean karimComplexe = reasoner.isEntailed(
                f.getOWLClassAssertionAxiom(CasComplexe, karim));
            System.out.println("[DL] karim : CasComplexe=" + (karimComplexe ? "VRAI" : "FAUX")
                + " + CrimeGrave(crime_karim)=VRAI");
            System.out.println("[DL]   => coherent avec exception DefaultLogic : d3 (Mineur=>TribunalEnfants)" +
                               " vs d4 (CrimeGrave=>!TribunalEnfants)");
            System.out.println("[DL]      DL confirme le cas complexe; la resolution du conflit releve" +
                               " de la logique des defaults");
            System.out.println();

            reasoner.dispose();

        } catch (Exception e) {
            System.err.println("[DL] Erreur Justice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
