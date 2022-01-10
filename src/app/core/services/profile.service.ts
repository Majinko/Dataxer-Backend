import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Profile} from '../models/profile';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  reloadProfile = new Subject<boolean>();

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Profile[]> {
    return this.http.get<Profile[]>(`${environment.baseUrl}/profile/all`);
  }

  storeOrUpdate(profile: Profile): Observable<void> {
    return this.http.post<void>(`${environment.baseUrl}/profile/storeOrUpdate`, profile);
  }

  destroy(id: number): Observable<void> {
    return this.http.get<void>(`${environment.baseUrl}/profile/destroy/${id}`);
  }
}