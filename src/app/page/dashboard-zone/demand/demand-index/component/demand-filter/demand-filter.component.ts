import {Component, Injector, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {SearchBarService} from '../../../../../../core/services/search-bar.service';
import {FilterClass} from '../../../../../../core/class/FilterClass';

@Component({
  selector: 'app-demand-filter',
  template: '',
})
export class DemandFilterComponent extends FilterClass implements OnInit {
  constructor(
    public formBuilder: FormBuilder,
    public searchbarService: SearchBarService,
    protected injector: Injector
  ) {
    super(searchbarService, formBuilder, 'demand', ['title', 'contact.name'], [], injector);
  }

  ngOnInit(): void {
    this.filterForm = this.formBuilder.group({});

    this.emitFilter();
    this.createFormControls();
    this.searchBarServiceCatch();
    this.prepareData();
  }
}