import {Paginate} from '../models/paginate';
import {Observable} from 'rxjs';
import {BaseFilter} from '../models/filters/baseFilter';

export interface IPaginate<T> {
  filter: BaseFilter;

  destroy(id: number): Observable<void>;

  paginate(index: number, size: number): Observable<Paginate<T>>;
}
