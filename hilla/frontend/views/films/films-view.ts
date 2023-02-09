import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from '../../views/view';

@customElement('films-view')
export class FilmsView extends View {

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
