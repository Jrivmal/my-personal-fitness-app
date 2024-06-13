package com.example.application.views.my_personal_fitness_app;

import com.example.application.data.FitnessApp;
import com.example.application.services.FitnessAppService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("My_Personal_Fitness_App")
@Route(value = "/:fitnessAppID?/:action?(edit)")
@RouteAlias(value = "")
public class My_Personal_Fitness_AppView extends Div implements BeforeEnterObserver {

    private final String FITNESSAPP_ID = "fitnessAppID";
    private final String FITNESSAPP_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<FitnessApp> grid = new Grid<>(FitnessApp.class, false);

    private DatePicker date;
    private TextField moves;
    private TextField exercise_time;
    private TextField stand;
    private TextField steps;
    private TextField calories;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<FitnessApp> binder;

    private FitnessApp fitnessApp;

    private final FitnessAppService fitnessAppService;

    public My_Personal_Fitness_AppView(FitnessAppService fitnessAppService) {
        this.fitnessAppService = fitnessAppService;
        addClassNames("my-personal-fitness-app-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("moves").setAutoWidth(true);
        grid.addColumn("exercise_time").setAutoWidth(true);
        grid.addColumn("stand").setAutoWidth(true);
        grid.addColumn("steps").setAutoWidth(true);
        grid.addColumn("calories").setAutoWidth(true);
        grid.setItems(query -> fitnessAppService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FITNESSAPP_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(My_Personal_Fitness_AppView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(FitnessApp.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(moves).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("moves");
        binder.forField(exercise_time).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("exercise_time");
        binder.forField(stand).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("stand");
        binder.forField(steps).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("steps");
        binder.forField(calories).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("calories");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.fitnessApp == null) {
                    this.fitnessApp = new FitnessApp();
                }
                binder.writeBean(this.fitnessApp);
                fitnessAppService.update(this.fitnessApp);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(My_Personal_Fitness_AppView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> fitnessAppId = event.getRouteParameters().get(FITNESSAPP_ID).map(Long::parseLong);
        if (fitnessAppId.isPresent()) {
            Optional<FitnessApp> fitnessAppFromBackend = fitnessAppService.get(fitnessAppId.get());
            if (fitnessAppFromBackend.isPresent()) {
                populateForm(fitnessAppFromBackend.get());
            } else {
                Notification.show(String.format("The requested fitnessApp was not found, ID = %s", fitnessAppId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(My_Personal_Fitness_AppView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        date = new DatePicker("Date");
        moves = new TextField("Moves");
        exercise_time = new TextField("Exercise_time");
        stand = new TextField("Stand");
        steps = new TextField("Steps");
        calories = new TextField("Calories");
        formLayout.add(date, moves, exercise_time, stand, steps, calories);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(FitnessApp value) {
        this.fitnessApp = value;
        binder.readBean(this.fitnessApp);

    }
}
