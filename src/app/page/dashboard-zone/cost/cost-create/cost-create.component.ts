import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {COSTSTATES, COSTTYPES} from '../../../../core/data/costs';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from '@angular/material-moment-adapter';
import {addDays, APP_DATE_FORMATS} from '../../../../../helper';
import {CRURRENCIES} from '../../../../core/data/currencies';
import {AddPercentPipe} from '../../../../core/pipes/add-percent.pipe';
import {UploadHelper} from '../../../../core/class/UploadHelper';
import {CostService} from '../../../../core/services/cost.service';
import {MessageService} from '../../../../core/services/message.service';
import {Router} from '@angular/router';
import {CategoryItemNode} from '../../../../core/models/category-item-node';
import {CategoryService} from '../../../../core/services/category.service';
import {Project} from '../../../../core/models/project';
import {ProjectService} from '../../../../core/services/project.service';

@Component({
  selector: 'app-cost-create',
  templateUrl: './cost-create.component.html',
  styleUrls: ['./cost-create.component.scss'],
  providers: [
    // `MomentDateAdapter` can be automatically provided by importing `MomentDateModule` in your
    // application's root module. We provide it at the component level here, due to limitations of
    // our example generation script.
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: {useUtc: true}},
    {provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS},
    AddPercentPipe
  ],
})
export class CostCreateComponent implements OnInit {
  formGroup: FormGroup;
  submitted: boolean = false;
  isLoading: boolean = false;

  categories: CategoryItemNode[] = [];

  costTypes = COSTTYPES;
  costStates = COSTSTATES;
  currencies = CRURRENCIES;

  constructor(
    public uploadHelper: UploadHelper,
    private formBuilder: FormBuilder,
    private addPercent: AddPercentPipe,
    private costService: CostService,
    private messageService: MessageService,
    private categoryService: CategoryService,
    private projectService: ProjectService,
    private router: Router,
  ) {
  }

  ngOnInit() {
    this.prepareForm();
    this.changeValue();
    this.handleChangeProject();
  }

  private prepareForm() {
    this.formGroup = this.formBuilder.group({
      title: [null, Validators.required],
      type: null,
      state: null,
      isInternal: false,
      isRepeated: false,
      period: null,
      repeatedFrom: null,
      repeatedTo: null,
      contact: [null, Validators.required],
      project: [null, Validators.required],
      categories: [null, Validators.required],
      note: null,
      number: null,
      variableSymbol: null,
      constantSymbol: null,
      createdDate: [new Date(), Validators.required],
      deliveredDate: [new Date(), Validators.required],
      dueDate: [addDays(new Date(), 14)],
      taxableSupply: null,
      currency: this.currencies[0].value,
      price: null,
      tax: 20,
      totalPrice: null,
      paymentMethod: 'BANK_PAYMENT',
      paymentDate: [new Date(), Validators.required],
    });
  }

  private handleChangeProject() {
    this.formGroup.get('project').valueChanges.subscribe((project: Project) => {
      this.projectService.getCategories(project.id).subscribe((categories) => {
        this.categories = categories;
      });
    });
  }

  private changeValue() {
    this.formGroup.valueChanges.subscribe((value) => {
      this.formGroup.patchValue({
        totalPrice: +this.addPercent.transform(value.price, value.tax)
      }, {emitEvent: false});
    });
  }

  private getAllCategories() {
    this.categoryService.all().subscribe((nestedCategories) => {
      this.categories = nestedCategories;
    });
  }

  // convenience getter for easy access to form fields
  get f() {
    return this.formGroup.controls;
  }

  submit() {
    this.submitted = true;
    this.isLoading = true;

    if (this.formGroup.invalid) {
      this.isLoading = false;
      return;
    }

    this.costService.storeWithFiles(this.formGroup.value, this.uploadHelper.files).subscribe(() => {
      this.router.navigate(['/cost']).then(() => this.messageService.add('Náklad bol pridaný'));
    });
  }
}
