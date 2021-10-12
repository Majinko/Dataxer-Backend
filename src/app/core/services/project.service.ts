import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Project, ProjectEvaluation, ProjectManHours} from '../models/project';
import {map} from 'rxjs/operators';
import {CategoryItemNode} from '../models/category-item-node';
import {ResourceService} from '../class/ResourceService';
import {Serializer} from '../models/serializers/Serializer';
import {CategoryHelper} from '../class/CategoryHelper';

@Injectable({
  providedIn: 'root'
})
export class ProjectService extends ResourceService<Project> {
  projectStore = new Subject<Project>();

  constructor(
    private httpClient: HttpClient,
    private categoryHelper: CategoryHelper
  ) {
    super(
      httpClient,
      'project',
      new Serializer());
  }

  store(project: Project): Observable<Project> {
    return this.httpClient.post<Project>(`${environment.baseUrl}/project/store`, project).pipe(map((p) => {
      this.projectStore.next(p);

      return p;
    }));
  }

  all(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${environment.baseUrl}/project/all`);
  }

  allHasCost(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${environment.baseUrl}/project/allHasCost`);
  }

  allHasInvoice(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${environment.baseUrl}/project/allHasInvoice`);
  }

  allHasPriceOffer(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${environment.baseUrl}/project/allHasPriceOffer`);
  }

  allHasUserTime() {
    return this.httpClient.get<Project[]>(`${environment.baseUrl}/project/allHasUserTime`);
  }

  getCategories(id: number): Observable<CategoryItemNode[]> {
    // tslint:disable-next-line:max-line-length
    return this.httpClient.get<CategoryItemNode[]>(`${environment.baseUrl}/project/allProjectCategory?projectId=${id}`).pipe(map(categories => {
      return this.categoryHelper.prepareOptionTree(categories);
    }));
  }

  getProjectEvaluation(id: number, categoryId?: number): Observable<ProjectEvaluation[]> {
    return this.httpClient.get<ProjectEvaluation[]>(`${environment.baseUrl}/project/projectCategoryOverview/${id}${categoryId ? '?categoryParent=' + categoryId : ''}`);
  }

  getProjectManHours(id: number): Observable<ProjectManHours> {
    return this.httpClient.get<ProjectManHours>(`${environment.baseUrl}/project/projectManHours/${id}`);
  }

  getEvaluation(id: number): Observable<any> {
    return this.httpClient.get<any>(`${environment.baseUrl}/project/prepareEvaluation/${id}`);
  }
}
