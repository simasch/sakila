package ch.martinelli.sakila.ui.views.films;

import ch.martinelli.sakila.backend.entity.FilmListEntry;
import ch.martinelli.sakila.backend.repository.FilmRepository;
import ch.martinelli.sakila.ui.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import org.vaadin.firitin.components.orderedlayout.VScroller;

import java.util.List;

@PageTitle("Film List")
@Route(value = "films", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class FilmsView extends Div {

    private static final int PAGE_SIZE = 24;
    private final FilmRepository filmRepository;
    private final OrderedList imageContainer = new OrderedList();
    private int offset = 0;

    public FilmsView(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;

        addClassNames("films-view");

        Div content = new Div();
        content.addClassNames(MaxWidth.SCREEN_XLARGE, Margin.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();

        H2 header = new H2("Popular");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, FontSize.XXXLARGE);

        Paragraph description = new Paragraph("Most popular movies");
        description.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, TextColor.SECONDARY);

        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        container.add(headerContainer, sortBy);

        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        loadFilms(offset);

        content.add(container, imageContainer);

        VScroller vScroller = new VScroller(content, Scroller.ScrollDirection.VERTICAL);
        vScroller.setHeightFull();

        vScroller.addScrollToEndListener(e -> {
            offset += PAGE_SIZE;
            loadFilms(offset);
        });

        add(vScroller);
    }

    private void loadFilms(int offset) {
        List<FilmListEntry> films = filmRepository.findAll(offset, PAGE_SIZE);

        for (FilmListEntry film : films) {
            imageContainer.add(new FilmCard(film, "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80"));
        }
    }
}
