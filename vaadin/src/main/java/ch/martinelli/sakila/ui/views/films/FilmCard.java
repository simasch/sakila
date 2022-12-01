package ch.martinelli.sakila.ui.views.films;

import ch.martinelli.sakila.backend.entity.FilmListEntry;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

public class FilmCard extends ListItem {

    public FilmCard(FilmListEntry film, String url) {
        addClassNames("bg-contrast-5", "flex", "flex-col", "items-start", "p-m", "rounded-l");

        Div div = new Div();
        div.addClassNames("bg-contrast", "flex items-center", "justify-center", "mb-m", "overflow-hidden",
                "rounded-m w-full");
        div.setHeight("160px");

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(url);
        image.setAlt(film.title());

        div.add(image);

        Span header = new Span();
        header.addClassNames("text-xl", "font-semibold");
        header.setText(film.title());

        Span subtitle = new Span();
        subtitle.addClassNames("text-s", "text-secondary");
        subtitle.setText(film.actors());

        Paragraph description = new Paragraph(film.description());
        description.addClassName("my-m");

        Span badge = new Span();
        badge.getElement().setAttribute("theme", "badge");
        badge.setText(film.category());

        add(div, header, subtitle, description, badge);
    }
}
