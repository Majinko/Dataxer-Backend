import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {SearchBarService} from '../../../../core/services/search-bar.service';
import {ProjectService} from '../../../../core/services/project.service';
import {CompanyService} from '../../../../core/services/company.service';
import {Company} from '../../../../core/models/company';

@Component({
  selector: 'app-project-detail',
  template: `
    <div>
      <div class="d-flex justify-content-end" *ngIf="companies && companies.length > 1">

        <div class="col-md-4 col-lg-2">
          <ng-select [items]="companies"
                     [clearable]="false"
                     [multiple]
                     bindLabel="name"
                     bindValue="id"
                     placeholder="Spoločnosť"
                     [(ngModel)]="selectedProjects"
                     (ngModelChange)="getDataByFirm()"
                     class="filter-ng-select">
          </ng-select>
        </div>
        <!--<div class="d-flex align-items-center">
          <mat-form-field appearance="fill">
            <mat-label>Vyber firmu</mat-label>
            <mat-select multiple [(ngModel)]="selectedProjects" (ngModelChange)="getDataByFirm()">
              <mat-option *ngFor="let company of companies" [value]="company.id">
                {{company.name}}
              </mat-option>
            </mat-select>
          </mat-form-field>
        </div>-->
      </div>
      <app-menu-tab [navLinks]="navLinks"></app-menu-tab>
      <router-outlet></router-outlet>
    </div>`,
})
export class ProjectDetailComponent implements OnInit, OnDestroy {
  companies: Company[] = [];
  selectedProjects: number[] = [];
  navLinks: { label: string, link: string, index: number }[] = [];

  constructor(
    private readonly searchBarService: SearchBarService,
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private companyService: CompanyService
  ) {
  }

  ngOnInit(): void {
    this.searchBarService.showBar = false;

    this.navLinks = [
      {
        label: 'Prehľad',
        link: `/project/show/${+this.route.snapshot.paramMap.get('id')}`,
        index: 0
      },
      {
        label: 'Vyhodnotenie',
        link: `/project/show/${+this.route.snapshot.paramMap.get('id')}/evaluation`,
        index: 1
      },
      {
        label: 'Kategórie',
        link: `/project/show/${+this.route.snapshot.paramMap.get('id')}/category-evaluation`,
        index: 2
      }
    ];

    this.getCompanies();
  }

  ngOnDestroy(): void {
    this.searchBarService.showBar = true;
  }

  getCompanies() {
    this.companyService.all().subscribe((c) => {
      this.companies = c;
    });
  }

  getDataByFirm() {
    this.projectService.getInfoFromCompany.next(this.selectedProjects);
  }
}
