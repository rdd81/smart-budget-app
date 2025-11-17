import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../environments/environment';
import { TransactionType } from '../models/transaction.model';
import { CategorizationService } from './categorization.service';

describe('CategorizationService', () => {
  let service: CategorizationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CategorizationService]
    });

    service = TestBed.inject(CategorizationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call suggest endpoint with payload', () => {
    const payload = {
      description: 'Starbucks latte',
      amount: 5.25,
      transactionType: TransactionType.EXPENSE
    };

    service.suggestCategory(payload).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/categorization/suggest`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ categoryId: 'cat-1', categoryName: 'Food', confidence: 0.9 });
  });
});

