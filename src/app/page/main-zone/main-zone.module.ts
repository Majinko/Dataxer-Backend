import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './main.component';
import {RouterModule, Routes} from '@angular/router';
import {ThemeModule} from '../../theme/theme.module';
import {MaterialModule} from '../../theme/modules/material.module';
import {UserResolver} from '../../core/resolver/user.resolver';
import {AppProfileResolver} from '../../core/resolver/appProfile.resolver';
import {NgxPermissionsModule} from 'ngx-permissions';
import {AuthGuardService} from '../../core/guards/auth-guard.service';

const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuardService],
    resolve: {user: UserResolver, appProfile: AppProfileResolver},
    children: [
      {
        path: '',
        loadChildren: () => import('./dashboard-zone/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'paginate',
        loadChildren: () => import('./paginate-zone/paginate-zone.module').then(m => m.PaginateZoneModule)
      }
    ]
  }
];

@NgModule({
  declarations: [
    MainComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule.forChild(routes),
    ThemeModule,

    // Specify your library as an import
    NgxPermissionsModule.forRoot()
  ],
  providers: [AppProfileResolver, UserResolver]
})
export class MainZoneModule {
}
