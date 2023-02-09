package ch.martinelli.sakila.ui.views.customers;

import ch.martinelli.sakila.backend.entity.CustomerListEntry;
import ch.martinelli.sakila.backend.repository.CustomerRepository;
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
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

import static ch.martinelli.sakila.db.tables.CustomerList.CUSTOMER_LIST;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;

@RolesAllowed("ADMIN")
@PageTitle("Customers")
@Route(value = "customers/:id?/:action?(edit)", layout = MainLayout.class)
public class CustomersView extends Div implements BeforeEnterObserver {

    private final Grid<CustomerListEntry> grid = new Grid<>();
    private final CustomerRepository customerRepository;

    private final BeanValidationBinder<CustomerRecord> binder = new BeanValidationBinder<>(CustomerRecord.class);

    public CustomersView(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

        addClassNames("customers-view");

        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setMultiSort(true);

        grid.addColumn(CustomerListEntry::name).setHeader("Name").setSortProperty(CUSTOMER_LIST.NAME.getName()).setAutoWidth(true);
        grid.addColumn(CustomerListEntry::address).setHeader("Address").setSortProperty(CUSTOMER_LIST.ADDRESS.getName()).setAutoWidth(true);
        grid.addColumn(CustomerListEntry::city).setHeader("City").setSortProperty(CUSTOMER_LIST.CITY.getName()).setAutoWidth(true);

        grid.setItems(query -> customerRepository.findAll(orderFields(CUSTOMER_LIST, query), query.getOffset(), query.getLimit()).stream());

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format("customers/%s/edit", event.getValue().id()));
            } else {
                binder.setBean(customerRepository.newRecord());

                UI.getCurrent().navigate(CustomersView.class);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String ID = "id";
        Optional<Integer> customerId = event.getRouteParameters().get(ID).map(Integer::valueOf);
        if (customerId.isPresent()) {
            Optional<CustomerRecord> customer = customerRepository.findById(customerId.get());
            if (customer.isPresent()) {
                binder.setBean(customer.get());
            } else {
                Notification.show(String.format("The requested customer was not found, ID = %s", customerId.get()));

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
                CustomerRecord customer = binder.getBean();
                customerRepository.save(customer);

                binder.setBean(customerRepository.newRecord());
                refreshGrid();

                Notification.show("Customer details stored.");

                UI.getCurrent().navigate(CustomersView.class);
            }
        });

        Button cancel = new Button("Cancel", e -> {
            binder.setBean(customerRepository.newRecord());
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
