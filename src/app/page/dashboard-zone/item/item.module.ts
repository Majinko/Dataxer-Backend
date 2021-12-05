import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ItemComponent} from './item.component';
import {IndexComponent} from './item-index/index.component';
import {ItemRoutingModule} from './item-routing.module';
import {ItemCreateComponent} from './item-create/item-create.component';
import {MaterialModule} from '../../../theme/modules/material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgSelectModule} from '@ng-select/ng-select';
import {ThemeModule} from '../../../theme/theme.module';
import {ContactModule} from '../contact/contact.module';
import {ItemPriceComponent} from './component/item-price/item-price.component';
import {CoreModule} from '../../../core/core.module';
import {ItemEditComponent} from './item-edit/item-edit.component';
import {ItemTableComponent} from './item-index/components/item-table/item-table.component';
import {ItemShowComponent} from './item-show/item-show.component';
import { ItemFilterComponent } from './item-index/components/item-filter/item-filter.component';

@NgModule({
  declarations: [ItemComponent, IndexComponent, ItemCreateComponent, ItemPriceComponent, ItemEditComponent, ItemTableComponent, ItemShowComponent, ItemFilterComponent],
  imports: [
    CommonModule,
    ItemRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    NgSelectModule,
    ThemeModule,
    ContactModule,
    FormsModule,
    CoreModule,
  ]
})
export class ItemModule {
}