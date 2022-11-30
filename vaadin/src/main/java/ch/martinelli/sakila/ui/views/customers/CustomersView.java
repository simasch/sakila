package ch.martinelli.sakila.ui.views.customers;

import ch.martinelli.sakila.db.tables.records.CustomerRecord;
import ch.martinelli.sakila.ui.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.DSLContext;
import org.jooq.Record4;

import java.util.Optional;

import static ch.martinelli.sakila.db.tables.Customer.CUSTOMER;
import static ch.martinelli.sakila.db.tables.CustomerList.CUSTOMER_LIST;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;

@PermitAll
@PageTitle("Customers")
@Route(value = "customers/:id?/:action?(edit)", layout = MainLayout.class)
public class CustomersView extends Div implements BeforeEnterObserver {

    private final Grid<Record4<Integer, String, String, String>> grid = new Grid<>();
    private final DSLContext dsl;

    private final BeanValidationBinder<CustomerRecord> binder = new BeanValidationBinder<>(CustomerRecord.class);

    public CustomersView(DSLContext dsl) {
        this.dsl = dsl;

        addClassNames("customers-view");

        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(r -> r.get(CUSTOMER_LIST.NAME)).setHeader("Name").setSortProperty(CUSTOMER_LIST.NAME.getName()).setAutoWidth(true);
        grid.addColumn(r -> r.get(CUSTOMER_LIST.ADDRESS)).setHeader("Address").setSortProperty(CUSTOMER_LIST.ADDRESS.getName()).setAutoWidth(true);
        grid.addColumn(r -> r.get(CUSTOMER_LIST.CITY)).setHeader("City").setSortProperty(CUSTOMER_LIST.CITY.getName()).setAutoWidth(true);

        grid.setItems(query -> dsl
                .select(CUSTOMER_LIST.ID, CUSTOMER_LIST.NAME, CUSTOMER_LIST.ADDRESS, CUSTOMER_LIST.CITY)
                .from(CUSTOMER_LIST)
                .orderBy(orderFields(CUSTOMER_LIST, query))
                .offset(query.getOffset())
                .limit(query.getLimit())
                .fetchStream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setMultiSort(true);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format("customers/%s/edit", event.getValue().get(CUSTOMER_LIST.ID)));
            } else {
                binder.setBean(dsl.newRecord(CUSTOMER));

                UI.getCurrent().navigate(CustomersView.class);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String ID = "id";
        Optional<Integer> customerId = event.getRouteParameters().get(ID).map(Integer::valueOf);
        if (customerId.isPresent()) {
            CustomerRecord customer = dsl.selectFrom(CUSTOMER).where(CUSTOMER.CUSTOMER_ID.eq(customerId.get())).fetchOne();
            if (customer != null) {
                binder.setBean(customer);
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", customerId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CustomersView.class);
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
        Button save = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                dsl.transaction(t -> {
                    CustomerRecord customer = binder.getBean();
                    t.dsl().attach(customer);
                    customer.store();
                });

                binder.setBean(dsl.newRecord(CUSTOMER));
                refreshGrid();

                Notification.show("Customer details stored.");

                UI.getCurrent().navigate(CustomersView.class);
            }
        });

        Button cancel = new Button("Cancel", e -> {
            binder.setBean(dsl.newRecord(CUSTOMER));
            refreshGrid();
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

}