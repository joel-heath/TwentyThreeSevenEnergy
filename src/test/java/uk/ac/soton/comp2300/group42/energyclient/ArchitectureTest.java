package uk.ac.soton.comp2300.group42.energyclient;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Validates the core architectural rules of the EnergyClient application.
 */
@AnalyzeClasses(
        packages = "uk.ac.soton.comp2300.group42.energyclient",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class ArchitectureTest {

    // =========================================================================
    // 1. HIGH-LEVEL LAYERED ARCHITECTURE RULES (Clean Architecture / MVVM)
    // =========================================================================

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Presentation").definedBy("..presentation..")
            .layer("Domain").definedBy("..domain..")
            .layer("Data").definedBy("..data..")
            .layer("DI").definedBy("..di..")

            // Presentation and Data layers should only be wired by DI. 
            // They cannot call each other.
            .whereLayer("Presentation").mayOnlyBeAccessedByLayers("DI")
            .whereLayer("Data").mayOnlyBeAccessedByLayers("DI")
            // The Domain layer is the core. It knows nothing about the outside world.
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Presentation", "Data", "DI");

    @ArchTest
    static final ArchRule no_cycles_between_packages = slices()
            .matching("uk.ac.soton.comp2300.group42.energyclient.(*)..")
            .should().beFreeOfCycles()
            .because("Cyclic dependencies make the codebase tangled and hard to maintain.");


    // =========================================================================
    // 2. DOMAIN LAYER RULES
    // =========================================================================

    @ArchTest
    static final ArchRule domain_should_not_depend_on_outside_frameworks = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..presentation..", "..data..", "..di..")
            .because("The Domain layer must be pure and independent of UI, databases, and frameworks.");

    @ArchTest
    static final ArchRule domain_repositories_must_be_interfaces = classes()
            .that().resideInAPackage("..domain.repository..")
            .should().beInterfaces()
            .because("Domain repositories define abstract contracts. Implementations belong in the Data layer.");


    // =========================================================================
    // 3. DATA LAYER & REPOSITORY IMPLEMENTATION RULES
    // =========================================================================

    @ArchTest
    static final ArchRule switchable_repositories_coordinate_local_and_remote = classes()
            .that().haveSimpleNameStartingWith("Switchable")
            .and().resideInAPackage("..data.repository..")
            .should().dependOnClassesThat().haveSimpleNameStartingWith("Local")
            .andShould().dependOnClassesThat().haveSimpleNameStartingWith("Remote")
            .because("Switchable repositories act as a facade, deciding when to hit Local Storage vs Remote APIs.");

    @ArchTest
    static final ArchRule remote_repositories_should_call_api_clients = classes()
            .that().haveSimpleNameStartingWith("Remote")
            .and().resideInAPackage("..data.repository..")
            .should().dependOnClassesThat().resideInAnyPackage("..data.backend..", "..data.external..")
            .because("Remote repositories should isolate network transport logic via specialized Clients.");

    @ArchTest
    static final ArchRule local_repositories_should_call_local_storage = classes()
            .that().haveSimpleNameStartingWith("Local")
            .and().haveSimpleNameEndingWith("Repository")
            .and().resideInAPackage("..data.repository..")
            .should().dependOnClassesThat().resideInAPackage("..data.local..")
            .because("Local repositories must persist data using the LocalStorageClient.");


    // =========================================================================
    // 4. PRESENTATION LAYER RULES (Views, Controllers, ViewModels, Stores)
    // =========================================================================

    @ArchTest
    static final ArchRule controllers_naming_convention = classes()
            .that().resideInAPackage("..presentation.controller..")
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule viewmodels_naming_convention = classes()
            .that().resideInAPackage("..presentation.viewmodel..")
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("ViewModel");

    @ArchTest
    static final ArchRule stores_naming_convention = classes()
            .that().resideInAPackage("..presentation.store..")
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Store");

    @ArchTest
    static final ArchRule viewmodels_should_be_ui_agnostic = noClasses()
            .that().resideInAPackage("..presentation.viewmodel..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "javafx.scene.control..", 
                    "javafx.scene.layout..", 
                    "javafx.scene.shape.."
            )
            .because("ViewModels should only manage state via properties. Direct references to JavaFX UI elements break the MVVM pattern.");

    @ArchTest
    static final ArchRule stores_must_map_domain_models = classes()
            .that().resideInAPackage("..presentation.store..")
            .should().dependOnClassesThat().resideInAPackage("..domain.model..")
            .because("Stores bridge the gap by fetching Domain models and parsing them into Observables.");
}