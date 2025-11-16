import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CategoryService } from './category.service';
import { Category, TransactionType } from '../models/transaction.model';
import { environment } from '../../environments/environment';

describe('CategoryService', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/categories`;

  const mockCategories: Category[] = [
    { id: '1', name: 'Salary', type: TransactionType.INCOME },
    { id: '2', name: 'Rent', type: TransactionType.EXPENSE }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch categories', () => {
    service.getCategories().subscribe(categories => {
      expect(categories.length).toBe(2);
      expect(categories[0].name).toBe('Salary');
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  it('should cache categories and avoid duplicate requests', () => {
    service.getCategories().subscribe();
    service.getCategories().subscribe();

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  it('should refresh cache when forceRefresh is true', () => {
    service.getCategories().subscribe();
    const firstReq = httpMock.expectOne(apiUrl);
    firstReq.flush(mockCategories);

    service.getCategories(true).subscribe(categories => {
      expect(categories[1].name).toBe('Rent');
    });
    const secondReq = httpMock.expectOne(apiUrl);
    secondReq.flush(mockCategories);
  });
});
