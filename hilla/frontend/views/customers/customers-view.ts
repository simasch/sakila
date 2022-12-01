import {Binder, field} from '@hilla/form';
import {EndpointError} from '@hilla/frontend';
import '@vaadin/button';
import '@vaadin/date-picker';
import '@vaadin/date-time-picker';
import '@vaadin/form-layout';
import '@vaadin/grid';
import type {Grid, GridDataProviderCallback, GridDataProviderParams} from '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-sort-column';
import '@vaadin/horizontal-layout';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/notification';
import {Notification} from '@vaadin/notification';
import '@vaadin/polymer-legacy-adapter';
import '@vaadin/split-layout';
import '@vaadin/text-field';
import '@vaadin/upload';
import {html} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import {View} from '../view';
import Customer from 'Frontend/generated/ch/martinelli/sakila/endpoints/Customer';
import CustomerModel from 'Frontend/generated/ch/martinelli/sakila/endpoints/CustomerModel';
import CustomerListEntry from 'Frontend/generated/ch/martinelli/sakila/backend/entity/CustomerListEntry';
import {CustomerEndpoint} from 'Frontend/generated/endpoints';
import Sort from 'Frontend/generated/dev/hilla/mappedtypes/Sort';
import Direction from 'Frontend/generated/org/springframework/data/domain/Sort/Direction';

@customElement('customers-view')
export class CustomersView extends View {
  @query('#grid')
  private grid!: Grid;

  @property({ type: Number })
  private gridSize = 0;

  private gridDataProvider = this.getGridData.bind(this);

  private binder = new Binder<Customer, CustomerModel>(this, CustomerModel);

  render() {
    return html`
      <vaadin-split-layout>
        <div class="grid-wrapper">
          <vaadin-grid
            id="grid"
            theme="no-border"
            .size=${this.gridSize}
            .dataProvider=${this.gridDataProvider}
            @active-item-changed=${this.itemSelected}
          >
            <vaadin-grid-sort-column path="name" auto-width></vaadin-grid-sort-column>
            <vaadin-grid-sort-column path="address" auto-width></vaadin-grid-sort-column>
            <vaadin-grid-sort-column path="city" auto-width></vaadin-grid-sort-column>
          </vaadin-grid>
        </div>
        <div class="editor-layout">
          <div class="editor">
            <vaadin-form-layout
              ><vaadin-text-field
                label="First name"
                id="firstName"
                ${field(this.binder.model.firstName)}
              ></vaadin-text-field
              ><vaadin-text-field
                label="Last name"
                id="lastName"
                ${field(this.binder.model.lastName)}
              ></vaadin-text-field
              ></vaadin-text-field
              ><vaadin-checkbox id="important" ${field(this.binder.model.activebool)} label="Important"></vaadin-checkbox
            ></vaadin-form-layout>
          </div>
          <vaadin-horizontal-layout class="button-layout">
            <vaadin-button theme="primary" @click=${this.save}>Save</vaadin-button>
            <vaadin-button theme="tertiary" @click=${this.cancel}>Cancel</vaadin-button>
          </vaadin-horizontal-layout>
        </div>
      </vaadin-split-layout>
    `;
  }

  private async getGridData(
    params: GridDataProviderParams<CustomerListEntry>,
    callback: GridDataProviderCallback<CustomerListEntry | undefined>
  ) {
    const sort: Sort = {
      orders: params.sortOrders.map((order) => ({
        property: order.path,
        direction: order.direction == 'asc' ? Direction.ASC : Direction.DESC,
        ignoreCase: false,
      })),
    };
    const data = await CustomerEndpoint.findAll({ pageNumber: params.page, pageSize: params.pageSize, sort });
    callback(data);
  }

  async connectedCallback() {
    super.connectedCallback();

    Notification.show("Hello");

    this.gridSize = (await CustomerEndpoint.count()) ?? 0;
  }

  private async itemSelected(event: CustomEvent) {
    const item: CustomerListEntry = event.detail.value as CustomerListEntry;
    this.grid.selectedItems = item ? [item] : [];

    if (item) {
      const fromBackend = await CustomerEndpoint.findById(item.id!);
      fromBackend ? this.binder.read(fromBackend) : this.refreshGrid();
    } else {
      this.clearForm();
    }
  }

  private async save() {
    try {
      const isNew = !this.binder.value.id;
      await this.binder.submitTo(CustomerEndpoint.save);
      if (isNew) {
        // We added a new item
        this.gridSize++;
      }
      this.clearForm();
      this.refreshGrid();
      Notification.show(`SamplePerson details stored.`, { position: 'bottom-start' });
    } catch (error: any) {
      if (error instanceof EndpointError) {
        Notification.show(`Server error. ${error.message}`, { theme: 'error', position: 'bottom-start' });
      } else {
        throw error;
      }
    }
  }

  private cancel() {
    this.grid.activeItem = undefined;
  }

  private clearForm() {
    this.binder.clear();
  }

  private refreshGrid() {
    this.grid.selectedItems = [];
    this.grid.clearCache();
  }
}
