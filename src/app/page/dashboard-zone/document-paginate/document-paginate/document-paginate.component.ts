import {AfterViewInit, Component, ComponentFactoryResolver, ComponentRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {GodButtonService} from '../../../../core/services/god-button.service';
import {DocumentPaginateService} from '../../../../core/services/document-paginate.service';
import {MessageService} from '../../../../core/services/message.service';
import {MatDialog} from '@angular/material/dialog';
import {AdHostDirective} from '../../../../core/directives/ad-host.directive';
import {InvoiceTableComponent} from '../../../app-paginate-zone/invoice-table/invoice-table.component';
import {
  PriceOfferTableComponent
} from '../../../app-paginate-zone/price-offer-table/price-offer-table.component';
import {CostTableComponent} from '../../../app-paginate-zone/cost-table/cost-table.component';
import {DemandTableComponent} from '../../demand/demand-index/component/demand-table/demand-table.component';

@Component({
  selector: 'app-document-paginate',
  templateUrl: './document-paginate.component.html',
})
// Wrap all document table and filter, is TMP
export class DocumentPaginateComponent implements OnInit, AfterViewInit {
  public title: string;
  public modelName: string;
  public inputSearchBarValues: string[];
  public inputSearchBarSelectValues: string[];
  public componentRef: ComponentRef<any>;

  constructor(
    public messageService: MessageService,
    public dialog: MatDialog,
    public documentPaginateService: DocumentPaginateService,
    private resolver: ComponentFactoryResolver,
    private godButtonService: GodButtonService,
    private route: ActivatedRoute,
  ) {
  }

  @ViewChild(AdHostDirective, {static: true}) adHost!: AdHostDirective;

  ngOnInit(): void {
    this.prepareGoodButton();
    this.prepareDocumentTable();
  }

  ngAfterViewInit(): void {
  }

  private prepareGoodButton() {
    this.route.paramMap.subscribe(param => {
      switch (param.get('documentType')) {
        case 'invoice':
          this.title = 'Fakturácia';

          this.modelName = 'invoice';
          this.inputSearchBarValues = ['title', 'contact.name'];
          this.inputSearchBarSelectValues = ['contact.id', 'company.id', 'project.id', 'state', 'documentType', 'date'];

          this.godButtonService.title = 'Nová faktúra';
          this.godButtonService.routerLink = '/invoice/create';
          this.godButtonService.menuItem = [
            {
              title: 'Nová faktúra', link: '/invoice/create/INVOICE'
            },
            {
              title: 'Nová zálohová faktúra', link: '/invoice/create/PROFORMA'
            }
          ];
          break;

        case 'priceOffer':
          this.title = 'Cenové ponuky';

          this.modelName = 'priceOffer';
          this.inputSearchBarValues = ['title', 'contact.name'];
          this.inputSearchBarSelectValues = ['contact.id', 'company.id', 'project.id', 'date'];

          this.godButtonService.title = 'Nová cenová ponuka';
          this.godButtonService.routerLink = '/price-offer/create';
          this.godButtonService.menuItem = [];
          break;

        case 'cost':
          this.title = 'Náklady';

          this.modelName = 'cost';
          this.inputSearchBarValues = ['title', 'contractor.name'];
          this.inputSearchBarSelectValues = ['contractor.id', 'company.id', 'project.id', 'state', 'date', 'documentType'];

          this.godButtonService.title = 'Nový náklad';
          this.godButtonService.routerLink = '/cost/create';
          this.godButtonService.menuItem = [];
          break;

        case 'pack':
          this.title = 'Dopyty';

          this.modelName = 'demand';
          this.inputSearchBarValues = ['title', 'contractor.name'];
          this.inputSearchBarSelectValues = ['contractor.id', 'company.id', 'project.id', 'state', 'date', 'documentType'];

          this.godButtonService.title = 'Nový dopyt';
          this.godButtonService.routerLink = '/demand/create';
          this.godButtonService.menuItem = [];
          break;

        case 'demand':
          this.title = 'Dopyty';

          this.modelName = 'demand';
          this.inputSearchBarValues = ['title', 'contractor.name'];
          this.inputSearchBarSelectValues = ['contractor.id', 'company.id', 'project.id', 'state', 'date', 'documentType'];

          this.godButtonService.title = 'Nový dopyt';
          this.godButtonService.routerLink = '/demand/create';
          this.godButtonService.menuItem = [];
          break;
      }
    });
  }

  private prepareDocumentTable() {
    this.route.paramMap.subscribe(param => {
      let factory;

      const viewContainerRef = this.adHost.viewContainerRef;
      viewContainerRef.clear();

      switch (param.get('documentType')) {
        case 'invoice':
          factory = this.resolver.resolveComponentFactory(InvoiceTableComponent);
          this.componentRef = viewContainerRef.createComponent<InvoiceTableComponent>(factory);
          break;
        case 'priceOffer':
          factory = this.resolver.resolveComponentFactory(PriceOfferTableComponent);
          this.componentRef = viewContainerRef.createComponent<InvoiceTableComponent>(factory);
          break;
        case 'cost':
          factory = this.resolver.resolveComponentFactory(CostTableComponent);
          this.componentRef = viewContainerRef.createComponent<CostTableComponent>(factory);
          break;
        case 'demand':
          factory = this.resolver.resolveComponentFactory(DemandTableComponent);
          this.componentRef = viewContainerRef.createComponent<DemandTableComponent>(factory);
          break;
        default:
          factory = this.resolver.resolveComponentFactory(InvoiceTableComponent);
          this.componentRef = viewContainerRef.createComponent<InvoiceTableComponent>(factory);
          break;
      }
    });
  }

  filterData(data: any) {
    this.documentPaginateService.filter = data.documentFilter;
    this.documentPaginateService.rsqlFilter = data.rsqlFilter;

    setTimeout(() => {
      //console.log(data.documentFilter);
      this.componentRef.instance.service.filter = data.filter;
      this.componentRef.instance.service.rsqlFilter = data.rsqlFilter;

      this.componentRef.instance.paginate();
    });
  }
}
