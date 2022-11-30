package ch.martinelli.sakila.ui.views.masterdetail;

import ch.martinelli.sakila.db.tables.records.CustomerListRecord;
import ch.martinelli.sakila.db.tables.records.CustomerRecord;
import ch.martinelli.sakila.ui.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.DSLContext;

import java.util.Optional;

import static ch.martinelli.sakila.db.tables.Customer.CUSTOMER;
import static ch.martinelli.sakila.db.tables.CustomerList.CUSTOMER_LIST;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;

@PageTitle("Master-Detail")
@Route(value = "master-detail/:id?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String ID = "id";
    private final String EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private final Grid<CustomerListRecord> grid = new Grid<>();
    private final DSLContext dsl;

    private final BeanValidationBinder<CustomerRecord> binder = new BeanValidationBinder<>(CustomerRecord.class);
    private Button save;
    private Button cancel;

    private CustomerRecord customer;

    public MasterDetailView(DSLContext dsl) {
        this.dsl = dsl;

        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(CustomerListRecord::getName).setAutoWidth(true);
        grid.addColumn(CustomerListRecord::getAddress).setAutoWidth(true);
        grid.addColumn(CustomerListRecord::getCity).setAutoWidth(true);

        grid.setItems(query -> dsl.selectFrom(CUSTOMER_LIST).orderBy(orderFields(CUSTOMER_LIST, query)).offset(query.getOffset()).stream().limit(query.getLimit()));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> customerId = event.getRouteParameters().get(ID).map(Integer::valueOf);
        if (customerId.isPresent()) {
            CustomerRecord customerFromDb = dsl.selectFrom(CUSTOMER).where(CUSTOMER.CUSTOMER_ID.eq(customerId.get())).fetchOne();
            if (customerFromDb != null) {
                populateForm(customerFromDb);
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", customerId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
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

        TextField firstName = new TextField("First Name");
        binder.forField(firstName).bind("firstName");
        TextField lastName = new TextField("Last Name");
        binder.forField(lastName).bind("lastName");
        Checkbox active = new Checkbox("Active");
        binder.forField(active).bind("activebool");

        formLayout.add(firstName, lastName, active);

        editorDiv.add(formLayout);

        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        cancel = new Button("Cancel", e -> {
            clearForm();
            refreshGrid();
        });

        save = new Button("Save", e -> {
            try {
                if (this.customer == null) {
                    this.customer = CUSTOMER.newRecord();
                }

                binder.writeBean(this.customer);
                this.customer.store();

                clearForm();
                refreshGrid();

                Notification.show("SamplePerson details stored.");

                UI.getCurrent().navigate(MasterDetailView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the samplePerson details.");
            }
        });

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

    private void populateForm(CustomerRecord value) {
        customer = value;
        binder.readBean(this.customer);
    }
}
