import {Component, Injector, OnInit} from '@angular/core';
import {FilterClass} from '../../../../../../core/class/FilterClass';
import {FormBuilder} from '@angular/forms';
import {SearchBarService} from '../../../../../../core/services/search-bar.service';

@Component({
  selector: 'app-task-filter',
  template: '',
})
export class TaskFilterComponent extends FilterClass implements OnInit {
  constructor(
    public formBuilder: FormBuilder,
    public searchbarService: SearchBarService,
    protected injector: Injector
  ) {
    super(searchbarService, formBuilder, 'task', ['title', 'user.email'], [], injector);
  }

  ngOnInit(): void {
    this.filterForm = this.formBuilder.group({});

    this.emitFilter();
    this.createFormControls();
    this.searchBarServiceCatch();
    this.prepareData();
  }
}