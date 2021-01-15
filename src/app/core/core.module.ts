import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ZeroValuePipe} from './pipes/zero-value.pipe';
import {AddPercentPipe} from './pipes/add-percent.pipe';
import {GetinitialsPipe} from './pipes/getinitials.pipe';
import {FilterPersonPipe} from './pipes/filter-person.pipe';
import {RemovePercentPipe} from './pipes/remove-percent.pipe';
import {NotNullPipe} from './pipes/not-null.pipe';
import {SortByPipe} from './pipes/sort-by.pipe';
import {RemovePercentGetNumberPipe} from './pipes/remove-percent-get-number.pipe';


@NgModule({
  declarations: [
    ZeroValuePipe,
    AddPercentPipe,
    GetinitialsPipe,
    FilterPersonPipe,
    RemovePercentPipe,
    NotNullPipe,
    SortByPipe,
    RemovePercentGetNumberPipe
  ],
  exports: [
    ZeroValuePipe,
    AddPercentPipe,
    GetinitialsPipe,
    FilterPersonPipe,
    RemovePercentPipe,
    NotNullPipe,
    SortByPipe,
    RemovePercentGetNumberPipe,
  ],
  imports: [
    CommonModule,
  ]
})
export class CoreModule {
}
