package ch.martinelli.sakila.ui.views.films;

import ch.martinelli.sakila.db.tables.records.FilmRecord;
import ch.martinelli.sakila.ui.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jooq.DSLContext;

import static ch.martinelli.sakila.db.tables.Film.FILM;

@AnonymousAllowed
@PageTitle("Films")
@Route(value = "films", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FilmsView extends Div {

    public FilmsView(DSLContext dsl) {
        addClassNames("films-view", "max-w-screen-lg", "mx-auto", "pb-l", "px-l");

        H2 header = new H2("Popular");
        header.addClassNames("mb-0", "mt-xl", "text-3xl");

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames("items-center", "justify-between");
        container.add(header, sortBy);

        VirtualList<FilmRecord> virtualList = new VirtualList<>();
        virtualList.setHeightFull();
        virtualList.addClassNames("gap-m", "grid", "list-none", "m-0", "p-0");

        virtualList.setItems(dsl.selectFrom(FILM).stream());
        virtualList.setRenderer(new ComponentRenderer<>(FilmCard::new));

        add(container, virtualList);
    }
}
