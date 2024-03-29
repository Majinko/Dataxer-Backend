import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectComponent} from './project.component';
import {ProjectCreateComponent} from './project-create/project-create.component';
import {ProjectEditComponent} from './project-edit/project-edit.component';

const routes: Routes = [{
  path: '',
  component: ProjectComponent,
  children: [
    {
      path: 'create',
      component: ProjectCreateComponent
    },
    {
      path: 'create/demand/:demandId',
      component: ProjectCreateComponent
    },
    {
      path: 'edit/:project_id',
      component: ProjectEditComponent
    },
    {
      path: 'show/:id',
      loadChildren: () => import('./project-detail/project-detail.module').then(m => m.ProjectDetailModule),
    }
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProjectRoutingModule {
}
