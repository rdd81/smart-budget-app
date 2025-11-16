import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransactionService } from './transaction.service';
import { Transaction, TransactionType } from '../models/transaction.model';
import { Page } from '../models/pagination.model';
import { environment } from '../../environments/environment';

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/transactions`;

  const mockTransaction: Transaction = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    amount: 100.50,
    transactionDate: '2025-01-15',
    description: 'Test transaction',
    category: {
      id: 'cat-123',
      name: 'Food',
      type: TransactionType.EXPENSE
    },
    transactionType: TransactionType.EXPENSE,
    createdAt: '2025-01-15T10:00:00Z',
    updatedAt: '2025-01-15T10:00:00Z'
  };

  const mockPage: Page<Transaction> = {
    content: [mockTransaction],
    totalElements: 1,
    totalPages: 1,
    page: 0,
    size: 20
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransactionService]
    });
    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getTransactions', () => {
    it('should fetch transactions with default parameters', () => {
      service.getTransactions().subscribe(page => {
        expect(page).toEqual(mockPage);
        expect(page.content.length).toBe(1);
        expect(page.content[0].id).toBe(mockTransaction.id);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should fetch transactions with pagination parameters', () => {
      const params = {
        page: 1,
        size: 10,
        sortBy: 'transactionDate' as const,
        sortDirection: 'desc' as const
      };

      service.getTransactions(params).subscribe();

      const req = httpMock.expectOne(request => {
        return request.url === apiUrl &&
               request.params.get('page') === '1' &&
               request.params.get('size') === '10' &&
               request.params.get('sortBy') === 'transactionDate' &&
               request.params.get('sortDirection') === 'desc';
      });
      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should handle empty response', () => {
      const emptyPage: Page<Transaction> = {
        content: [],
        totalElements: 0,
        totalPages: 0,
        page: 0,
        size: 20
      };

      service.getTransactions().subscribe(page => {
        expect(page.content.length).toBe(0);
        expect(page.totalElements).toBe(0);
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(emptyPage);
    });

    it('should handle API error', () => {
      service.getTransactions().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getTransaction', () => {
    it('should fetch single transaction by ID', () => {
      const id = '123e4567-e89b-12d3-a456-426614174000';

      service.getTransaction(id).subscribe(transaction => {
        expect(transaction).toEqual(mockTransaction);
        expect(transaction.id).toBe(id);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTransaction);
    });

    it('should handle 404 not found', () => {
      const id = 'non-existent-id';

      service.getTransaction(id).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('deleteTransaction', () => {
    it('should delete transaction by ID', () => {
      const id = '123e4567-e89b-12d3-a456-426614174000';

      service.deleteTransaction(id).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle delete error', () => {
      const id = '123e4567-e89b-12d3-a456-426614174000';

      service.deleteTransaction(id).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(403);
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });
    });
  });

  describe('createTransaction', () => {
    it('should post payload to create new transaction', () => {
      const payload = {
        amount: 99.99,
        transactionDate: '2025-02-01',
        description: 'New expense',
        categoryId: 'cat-123',
        transactionType: TransactionType.EXPENSE
      };

      service.createTransaction(payload).subscribe(response => {
        expect(response).toEqual(mockTransaction);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(payload);
      req.flush(mockTransaction);
    });

    it('should propagate API errors on create', () => {
      const payload = {
        amount: 10,
        transactionDate: '2025-02-01',
        description: 'Failing tx',
        categoryId: 'cat-456',
        transactionType: TransactionType.INCOME
      };

      service.createTransaction(payload).subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Validation error', { status: 400, statusText: 'Bad Request' });
    });
  });
});
