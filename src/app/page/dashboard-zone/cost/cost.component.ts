import { Inject } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GodButtonService } from 'src/app/core/services/god-button.service';

@Component({
  selector: 'app-cost',
  template: `
      <div class="container-fluid mt-2">
          <div class="row">
              <div class="col">
                  <router-outlet></router-outlet>
              </div>
          </div>
      </div>
  `,
})
export class CostComponent implements OnInit {

  constructor(
    @Inject(GodButtonService) private readonly godButtonService: GodButtonService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.godButtonService.menuItem = [];
    this.godButtonService.title = this.route.snapshot.data.godButtonTitle;
    this.godButtonService.routerLink = this.route.snapshot.data.gotButtonRouteLink;
  }
}
