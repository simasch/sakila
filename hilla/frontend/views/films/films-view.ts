import '@vaadin/button';
import '@vaadin/notification';
import '@vaadin/text-field';
import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

@customElement('films-view')
export class FilmsView extends View {
    name = '';

    connectedCallback() {
        super.connectedCallback();
        this.classList.add('flex', 'p-m', 'gap-m', 'items-end');
    }

    render() {
        return html`
            <h1>Films</h1>
        `;
    }
}
