package rcr.description;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Logique de Description — domaine : industrie musicale algérienne.
 * Ontologie OWL construite via OWL API 4.5, raisonnée par HermiT.
 *
 * TBox : Artiste, Rappeur, RappeurAlgerien, Label, MajorLabel,
 *        IndependantLabel, Album, AlbumOr, AlbumPlatine,
 *        ArtisteSigné, ArtisteMajor, ArtisteIndependant, ArtisteLibreDroits…
 * ABox : soolking, feu, tif, flenn, universal, believe, guerilla, …
 */
public class DescriptionLogicMusic {

    private static final String NS = "http://rcr1/musique#";

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
        System.out.println("=== Logique de Description : Industrie Musicale Algerienne ===\n");
        try {
            // ===== Setup =====
            OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
            OWLOntology onto = mgr.createOntology(IRI.create("http://rcr1/musique"));
            OWLDataFactory f = mgr.getOWLDataFactory();

            // ===== Classes =====
            OWLClass Personne           = f.getOWLClass(iri("Personne"));
            OWLClass Organisation       = f.getOWLClass(iri("Organisation"));
            OWLClass Artiste            = f.getOWLClass(iri("Artiste"));
            OWLClass Rappeur            = f.getOWLClass(iri("Rappeur"));
            OWLClass RappeurAlgerien    = f.getOWLClass(iri("RappeurAlgerien"));
            OWLClass Chanteur           = f.getOWLClass(iri("Chanteur"));
            OWLClass DJ                 = f.getOWLClass(iri("DJ"));
            OWLClass Compositeur        = f.getOWLClass(iri("Compositeur"));
            OWLClass Label              = f.getOWLClass(iri("Label"));
            OWLClass MajorLabel         = f.getOWLClass(iri("MajorLabel"));
            OWLClass IndependantLabel   = f.getOWLClass(iri("IndependantLabel"));
            OWLClass Oeuvre             = f.getOWLClass(iri("Oeuvre"));
            OWLClass Album              = f.getOWLClass(iri("Album"));
            OWLClass Titre              = f.getOWLClass(iri("Titre"));
            OWLClass TitreRap           = f.getOWLClass(iri("TitreRap"));
            OWLClass Chanson            = f.getOWLClass(iri("Chanson"));
            OWLClass Rap                = f.getOWLClass(iri("Rap"));
            OWLClass AlbumCertifie      = f.getOWLClass(iri("AlbumCertifie"));
            OWLClass AlbumOr            = f.getOWLClass(iri("AlbumOr"));
            OWLClass AlbumPlatine       = f.getOWLClass(iri("AlbumPlatine"));
            OWLClass Certification      = f.getOWLClass(iri("Certification"));
            OWLClass CertificationOr    = f.getOWLClass(iri("CertificationOr"));
            OWLClass CertificationPlatine = f.getOWLClass(iri("CertificationPlatine"));
            OWLClass PlusDe100Artistes  = f.getOWLClass(iri("PlusDe100Artistes"));
            OWLClass ArtisteEtranger    = f.getOWLClass(iri("ArtisteEtranger"));
            OWLClass Darija             = f.getOWLClass(iri("Darija"));
            OWLClass PlusDe5Ans         = f.getOWLClass(iri("PlusDe5Ans"));
            OWLClass ArtisteSigné       = f.getOWLClass(iri("ArtisteSigne"));
            OWLClass ArtisteMajor       = f.getOWLClass(iri("ArtisteMajor"));
            OWLClass ArtisteIndependant = f.getOWLClass(iri("ArtisteIndependant"));
            OWLClass ArtisteLibreDroits = f.getOWLClass(iri("ArtisteLibreDroits"));
            OWLClass Featuring          = f.getOWLClass(iri("Featuring"));
            OWLClass CollabInternationale = f.getOWLClass(iri("CollabInternationale"));
            OWLClass ArtisteAvecRoyalties    = f.getOWLClass(iri("ArtisteAvecRoyalties"));
            OWLClass ArtistePartageRoyalties = f.getOWLClass(iri("ArtistePartageRoyalties"));
            OWLClass ArtisteExperimente      = f.getOWLClass(iri("ArtisteExperimente"));

            // ===== Object Properties =====
            OWLObjectProperty produit         = f.getOWLObjectProperty(iri("produit"));
            OWLObjectProperty pratique        = f.getOWLObjectProperty(iri("pratique"));
            OWLObjectProperty chante_en       = f.getOWLObjectProperty(iri("chante_en"));
            OWLObjectProperty interprete      = f.getOWLObjectProperty(iri("interprete"));
            OWLObjectProperty mixe            = f.getOWLObjectProperty(iri("mixe"));
            OWLObjectProperty compose         = f.getOWLObjectProperty(iri("compose"));
            OWLObjectProperty distribue       = f.getOWLObjectProperty(iri("distribue"));
            OWLObjectProperty signe           = f.getOWLObjectProperty(iri("signe"));
            OWLObjectProperty signe_chez      = f.getOWLObjectProperty(iri("signe_chez"));
            OWLObjectProperty contient        = f.getOWLObjectProperty(iri("contient"));
            OWLObjectProperty produit_par     = f.getOWLObjectProperty(iri("produit_par"));
            OWLObjectProperty recoit          = f.getOWLObjectProperty(iri("recoit"));
            OWLObjectProperty implique        = f.getOWLObjectProperty(iri("implique"));
            OWLObjectProperty annees_carriere = f.getOWLObjectProperty(iri("annees_carriere"));

            // ===== TBox =====

            // Artiste ≡ Personne ⊓ ∃produit.Oeuvre
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Artiste,
                f.getOWLObjectIntersectionOf(Personne,
                    f.getOWLObjectSomeValuesFrom(produit, Oeuvre))));

            // Rappeur ≡ Artiste ⊓ ∃pratique.Rap ⊓ ∀produit.TitreRap
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Rappeur,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(pratique, Rap),
                    f.getOWLObjectAllValuesFrom(produit, TitreRap))));

            // RappeurAlgerien ≡ Rappeur ⊓ ∃chante_en.Darija
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(RappeurAlgerien,
                f.getOWLObjectIntersectionOf(Rappeur,
                    f.getOWLObjectSomeValuesFrom(chante_en, Darija))));

            // Chanteur ≡ Artiste ⊓ ∃interprete.Chanson
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Chanteur,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(interprete, Chanson))));

            // DJ ≡ Artiste ⊓ ∃mixe.Titre
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(DJ,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(mixe, Titre))));

            // Compositeur ≡ Artiste ⊓ ∃compose.Oeuvre
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Compositeur,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(compose, Oeuvre))));

            // Label ≡ Organisation ⊓ ∃distribue.Album
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Label,
                f.getOWLObjectIntersectionOf(Organisation,
                    f.getOWLObjectSomeValuesFrom(distribue, Album))));

            // MajorLabel ≡ Label ⊓ ∃signe.PlusDe100Artistes
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(MajorLabel,
                f.getOWLObjectIntersectionOf(Label,
                    f.getOWLObjectSomeValuesFrom(signe, PlusDe100Artistes))));

            // IndependantLabel ≡ Label ⊓ ¬MajorLabel
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(IndependantLabel,
                f.getOWLObjectIntersectionOf(Label,
                    f.getOWLObjectComplementOf(MajorLabel))));

            // Titre ⊑ Oeuvre  ;  TitreRap ⊑ Titre  ;  MajorLabel ⊑ Label
            ax(mgr, onto, f.getOWLSubClassOfAxiom(Titre, Oeuvre));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(TitreRap, Titre));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(MajorLabel, Label));

            // Album ≡ Oeuvre ⊓ ∃contient.Titre ⊓ ∃produit_par.Artiste
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Album,
                f.getOWLObjectIntersectionOf(Oeuvre,
                    f.getOWLObjectSomeValuesFrom(contient, Titre),
                    f.getOWLObjectSomeValuesFrom(produit_par, Artiste))));

            // CertificationOr ⊑ Certification  ;  CertificationPlatine ⊑ Certification
            ax(mgr, onto, f.getOWLSubClassOfAxiom(CertificationOr, Certification));
            ax(mgr, onto, f.getOWLSubClassOfAxiom(CertificationPlatine, Certification));

            // AlbumCertifie ≡ Album ⊓ ∃recoit.Certification
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AlbumCertifie,
                f.getOWLObjectIntersectionOf(Album,
                    f.getOWLObjectSomeValuesFrom(recoit, Certification))));

            // AlbumOr ≡ AlbumCertifie ⊓ ∃recoit.CertificationOr
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AlbumOr,
                f.getOWLObjectIntersectionOf(AlbumCertifie,
                    f.getOWLObjectSomeValuesFrom(recoit, CertificationOr))));

            // AlbumPlatine ≡ AlbumCertifie ⊓ ∃recoit.CertificationPlatine
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(AlbumPlatine,
                f.getOWLObjectIntersectionOf(AlbumCertifie,
                    f.getOWLObjectSomeValuesFrom(recoit, CertificationPlatine))));

            // ArtisteSigné ≡ Artiste ⊓ ∃signe_chez.Label
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteSigné,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(signe_chez, Label))));

            // ArtisteMajor ≡ Artiste ⊓ ∃signe_chez.MajorLabel
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteMajor,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(signe_chez, MajorLabel))));

            // ArtisteIndependant ≡ Artiste ⊓ ¬∃signe_chez.MajorLabel
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteIndependant,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectComplementOf(
                        f.getOWLObjectSomeValuesFrom(signe_chez, MajorLabel)))));

            // ArtisteLibreDroits ≡ Artiste ⊓ ¬∃signe_chez.Label
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteLibreDroits,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectComplementOf(
                        f.getOWLObjectSomeValuesFrom(signe_chez, Label)))));

            // Featuring ≡ Titre ⊓ ∃implique.Artiste ⊓ ∃implique.ArtisteEtranger
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(Featuring,
                f.getOWLObjectIntersectionOf(Titre,
                    f.getOWLObjectSomeValuesFrom(implique, Artiste),
                    f.getOWLObjectSomeValuesFrom(implique, ArtisteEtranger))));

            // CollabInternationale ≡ Featuring ⊓ ∃implique.RappeurAlgerien ⊓ ∃implique.ArtisteEtranger
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(CollabInternationale,
                f.getOWLObjectIntersectionOf(Featuring,
                    f.getOWLObjectSomeValuesFrom(implique, RappeurAlgerien),
                    f.getOWLObjectSomeValuesFrom(implique, ArtisteEtranger))));

            // ArtisteAvecRoyalties ≡ Artiste ⊓ ∃produit.Album
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteAvecRoyalties,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(produit, Album))));

            // ArtistePartageRoyalties ≡ ArtisteAvecRoyalties ⊓ ∃signe_chez.Label
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtistePartageRoyalties,
                f.getOWLObjectIntersectionOf(ArtisteAvecRoyalties,
                    f.getOWLObjectSomeValuesFrom(signe_chez, Label))));

            // ArtisteExperimente ≡ Artiste ⊓ ∃annees_carriere.PlusDe5Ans
            ax(mgr, onto, f.getOWLEquivalentClassesAxiom(ArtisteExperimente,
                f.getOWLObjectIntersectionOf(Artiste,
                    f.getOWLObjectSomeValuesFrom(annees_carriere, PlusDe5Ans))));

            // ===== Individuals =====
            OWLNamedIndividual soolking      = f.getOWLNamedIndividual(iri("soolking"));
            OWLNamedIndividual feu           = f.getOWLNamedIndividual(iri("feu"));
            OWLNamedIndividual tif           = f.getOWLNamedIndividual(iri("tif"));
            OWLNamedIndividual flenn         = f.getOWLNamedIndividual(iri("flenn"));
            OWLNamedIndividual universal     = f.getOWLNamedIndividual(iri("universal"));
            OWLNamedIndividual believe       = f.getOWLNamedIndividual(iri("believe"));
            OWLNamedIndividual guerilla      = f.getOWLNamedIndividual(iri("guerilla"));
            OWLNamedIndividual derniereHeure = f.getOWLNamedIndividual(iri("derniere_heure"));
            OWLNamedIndividual certOr        = f.getOWLNamedIndividual(iri("certification_or"));
            OWLNamedIndividual certPlatine   = f.getOWLNamedIndividual(iri("certification_platine"));

            // UNA — tous différents
            ax(mgr, onto, f.getOWLDifferentIndividualsAxiom(
                soolking, feu, tif, flenn,
                universal, believe,
                guerilla, derniereHeure,
                certOr, certPlatine));

            // ===== ABox — assertions de classes =====
            ax(mgr, onto, f.getOWLClassAssertionAxiom(RappeurAlgerien, soolking));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(RappeurAlgerien, feu));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(RappeurAlgerien, tif));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(RappeurAlgerien, flenn));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(MajorLabel, universal));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(IndependantLabel, believe));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(Album, guerilla));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(Album, derniereHeure));

            ax(mgr, onto, f.getOWLClassAssertionAxiom(CertificationOr, certOr));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(CertificationPlatine, certPlatine));

            // tif et flenn : aucun label connu → ArtisteLibreDroits
            // Sous OWA, l'absence de label ne se dérive pas automatiquement ;
            // on supplée par une assertion explicite (équivalent CWA local).
            ax(mgr, onto, f.getOWLClassAssertionAxiom(ArtisteLibreDroits, tif));
            ax(mgr, onto, f.getOWLClassAssertionAxiom(ArtisteLibreDroits, flenn));

            // ===== ABox — assertions de propriétés =====
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(signe_chez, soolking, universal));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(signe_chez, feu, believe));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(produit, soolking, guerilla));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(produit, feu, derniereHeure));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(produit, tif, guerilla));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(recoit, guerilla, certOr));
            ax(mgr, onto, f.getOWLObjectPropertyAssertionAxiom(recoit, derniereHeure, certPlatine));

            // ===== Raisonneur HermiT =====
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner reasoner = rf.createReasoner(onto);

            System.out.println("Ontologie : http://rcr1/musique");
            System.out.println("ABox      : soolking, feu, tif, flenn | universal(MajorLabel)," +
                               " believe(IndependantLabel) | guerilla, derniere_heure | certifications");
            System.out.println();

            // ===== R1 =====
            System.out.println("[DL] Requete R1: soolking est-il un ArtisteIndependant ?");
            System.out.println("[DL] Individu: soolking");
            boolean r1 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(ArtisteIndependant, soolking));
            System.out.println("[DL] Resultat: " + (r1 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: soolking signe_chez universal:MajorLabel" +
                               " => soolking in ExisteSigne_chez.MajorLabel => NOT ArtisteIndependant");
            System.out.println("[DL] Coherence avec DefaultLogic: conflit d1 vs d2 pour soolking (SigneLabel)");
            System.out.println();

            // ===== R2 =====
            System.out.println("[DL] Requete R2: feu est-il un ArtisteLibreDroits ?");
            System.out.println("[DL] Individu: feu");
            boolean r2 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(ArtisteLibreDroits, feu));
            System.out.println("[DL] Resultat: " + (r2 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: feu signe_chez believe:IndependantLabel => believe:Label" +
                               " => feu in ExisteSigne_chez.Label => NOT ArtisteLibreDroits");
            System.out.println("[DL] Coherence avec DefaultLogic: d6 (NouveauContrat) => !LibreDroits(feu)");
            System.out.println();

            // ===== R3 =====
            System.out.println("[DL] Requete R3: tif est-il un ArtisteLibreDroits ?");
            System.out.println("[DL] Individu: tif");
            boolean r3 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(ArtisteLibreDroits, tif));
            System.out.println("[DL] Resultat: " + (r3 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: tif:ArtisteLibreDroits asserte en ABox (aucun label);" +
                               " OWA comble par assertion explicite => NOT ExisteSigne_chez.Label");
            System.out.println("[DL] Coherence avec DefaultLogic: d1 s'applique sans blocage => LibreDroits(tif)");
            System.out.println();

            // ===== R4 =====
            System.out.println("[DL] Requete R4: flenn est-il un ArtisteLibreDroits ?");
            System.out.println("[DL] Individu: flenn");
            boolean r4 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(ArtisteLibreDroits, flenn));
            System.out.println("[DL] Resultat: " + (r4 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: flenn:ArtisteLibreDroits asserte en ABox (pas de label)");
            System.out.println("[DL] Coherence avec DefaultLogic: d1 => LibreDroits(flenn) avant tout nouveau contrat");
            System.out.println();

            // ===== R5 =====
            System.out.println("[DL] Requete R5: guerilla est-il un AlbumOr ?");
            System.out.println("[DL] Individu: guerilla");
            // guerilla:Album + recoit(guerilla,certOr) + certOr:CertificationOr ⊑ Certification
            // => guerilla in ExisteRecoit.Certification => AlbumCertifie
            // => guerilla in ExisteRecoit.CertificationOr => AlbumOr
            boolean r5 = reasoner.isEntailed(f.getOWLClassAssertionAxiom(AlbumOr, guerilla));
            System.out.println("[DL] Resultat: " + (r5 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: guerilla:Album, recoit(guerilla,certOr)," +
                               " certOr:CertificationOr ⊑ Certification => AlbumCertifie;" +
                               " ExisteRecoit.CertificationOr => AlbumOr");
            System.out.println("[DL] Coherence avec DefaultLogic: d7 (PlusDeDeuxAns && AlbumSorti => AlbumCertifie)");
            System.out.println();

            // ===== R6 =====
            System.out.println("[DL] Requete R6: Quelles sont toutes les instances de ArtisteSigné ?");
            Set<OWLNamedIndividual> signes = reasoner.getInstances(ArtisteSigné, false).getFlattened();
            String signesStr = signes.stream()
                .map(i -> i.getIRI().getFragment())
                .sorted()
                .collect(Collectors.joining(", "));
            System.out.println("[DL] Resultat: {" + signesStr + "}");
            System.out.println("[DL] Justification: signe_chez(soolking,universal:Label)," +
                               " signe_chez(feu,believe:Label) => les deux in ArtisteSigné");
            System.out.println("[DL] Coherence avec DefaultLogic: d2 s'applique => !LibreDroits pour les signes");
            System.out.println();

            // ===== R7 =====
            System.out.println("[DL] Requete R7: Quelles sont toutes les instances de ArtisteLibreDroits ?");
            Set<OWLNamedIndividual> libres = reasoner.getInstances(ArtisteLibreDroits, false).getFlattened();
            String libresStr = libres.stream()
                .map(i -> i.getIRI().getFragment())
                .sorted()
                .collect(Collectors.joining(", "));
            System.out.println("[DL] Resultat: {" + libresStr + "}");
            System.out.println("[DL] Justification: tif et flenn asserts; soolking/feu ont signe_chez Label" +
                               " => exclus de ArtisteLibreDroits");
            System.out.println("[DL] Coherence avec DefaultLogic: d1 => LibreDroits uniquement sans label connu");
            System.out.println();

            // ===== R8 =====
            System.out.println("[DL] Requete R8: ArtisteMajor est-il subsume par ArtisteSigné ?");
            boolean r8 = reasoner.isEntailed(f.getOWLSubClassOfAxiom(ArtisteMajor, ArtisteSigné));
            System.out.println("[DL] Resultat: " + (r8 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: ArtisteMajor ≡ Artiste ⊓ ExisteSigne_chez.MajorLabel," +
                               " MajorLabel ⊑ Label => ExisteSigne_chez.Label => ArtisteMajor ⊑ ArtisteSigné");
            System.out.println("[DL] Coherence avec DefaultLogic: subsomption entre concepts derives");
            System.out.println();

            // ===== R9 =====
            System.out.println("[DL] Requete R9: ArtisteIndependant et ArtisteMajor sont-ils disjoints ?");
            boolean r9 = reasoner.isEntailed(
                f.getOWLDisjointClassesAxiom(ArtisteIndependant, ArtisteMajor));
            System.out.println("[DL] Resultat: " + (r9 ? "VRAI" : "FAUX"));
            System.out.println("[DL] Justification: ArtisteIndependant ≡ Artiste ⊓ ¬ExisteSigne_chez.MajorLabel," +
                               " ArtisteMajor ≡ Artiste ⊓ ExisteSigne_chez.MajorLabel => intersection vide");
            System.out.println("[DL] Coherence avec DefaultLogic: concepts mutuellement exclusifs");
            System.out.println();

            // ===== R10 — Démonstration non-monotonie =====
            System.out.println("[DL] Requete R10: Demonstration non-monotonie pour flenn");
            System.out.println("[DL] AVANT ajout signe_chez(flenn, universal):");
            System.out.println("[DL]   flenn : ArtisteLibreDroits -> " + (r4 ? "VRAI" : "FAUX"));

            // Ontologie APRÈS : copie + retrait de flenn:ArtisteLibreDroits + ajout signe_chez(flenn,universal)
            OWLOntologyManager mgr2 = OWLManager.createOWLOntologyManager();
            OWLOntology onto2 = mgr2.createOntology(IRI.create("http://rcr1/musique_apres"));
            for (OWLAxiom ax2 : onto.getAxioms()) {
                mgr2.addAxiom(onto2, ax2);
            }
            // Retirer l'assertion explicite flenn:ArtisteLibreDroits
            mgr2.removeAxiom(onto2, f.getOWLClassAssertionAxiom(ArtisteLibreDroits, flenn));
            // Ajouter signe_chez(flenn, universal) — flenn signe maintenant chez un MajorLabel
            mgr2.addAxiom(onto2, f.getOWLObjectPropertyAssertionAxiom(signe_chez, flenn, universal));

            OWLReasoner reasoner2 = rf.createReasoner(onto2);
            // flenn in ExisteSigne_chez.Label (car universal:MajorLabel => universal:Label)
            // => flenn in ¬ArtisteLibreDroits => NOT entailed
            boolean r10Apres = reasoner2.isEntailed(
                f.getOWLClassAssertionAxiom(ArtisteLibreDroits, flenn));
            System.out.println("[DL] APRES ajout signe_chez(flenn, universal):");
            System.out.println("[DL]   flenn : ArtisteLibreDroits -> " + (r10Apres ? "VRAI" : "FAUX"));
            System.out.println("[DL] -> Nouvelle assertion change les conclusions du raisonneur");
            System.out.println("[DL] Coherence avec DefaultLogic: non-monotonie — ajout de SigneLabel(flenn)" +
                               " invalide LibreDroits, comme d1 est bloque par d2 dans DefaultLogicMusic");
            System.out.println();

            reasoner.dispose();
            reasoner2.dispose();

        } catch (Exception e) {
            System.err.println("[DL] Erreur Music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
