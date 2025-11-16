import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { TransactionListComponent } from './transaction-list.component';
import { TransactionService } from '../../services/transaction.service';
import { Transaction, TransactionType } from '../../models/transaction.model';
import { Page } from '../../models/pagination.model';

describe('TransactionListComponent', () => {
  let component: TransactionListComponent;
  let fixture: ComponentFixture<TransactionListComponent>;
  let transactionService: jasmine.SpyObj<TransactionService>;

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

  const mockIncomeTransaction: Transaction = {
    ...mockTransaction,
    id: 'income-123',
    amount: 500.00,
    description: 'Salary',
    category: {
      id: 'cat-salary',
      name: 'Salary',
      type: TransactionType.INCOME
    },
    transactionType: TransactionType.INCOME
  };

  const mockPage: Page<Transaction> = {
    content: [mockTransaction, mockIncomeTransaction],
    totalElements: 2,
    totalPages: 1,
    page: 0,
    size: 20
  };

  const emptyPage: Page<Transaction> = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    page: 0,
    size: 20
  };

  beforeEach(async () => {
    const transactionServiceSpy = jasmine.createSpyObj('TransactionService', [
      'getTransactions',
      'getTransaction',
      'deleteTransaction'
    ]);

    await TestBed.configureTestingModule({
      imports: [TransactionListComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: TransactionService, useValue: transactionServiceSpy }
      ]
    }).compileComponents();

    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
    fixture = TestBed.createComponent(TransactionListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load transactions on initialization', () => {
      transactionService.getTransactions.and.returnValue(of(mockPage));

      fixture.detectChanges(); // triggers ngOnInit

      expect(transactionService.getTransactions).toHaveBeenCalledWith({
        page: 0,
        size: 20,
        sortBy: 'transactionDate',
        sortDirection: 'desc'
      });
      expect(component.transactions).toEqual(mockPage.content);
      expect(component.totalPages).toBe(1);
      expect(component.totalElements).toBe(2);
      expect(component.loading).toBeFalse();
    });

    it('should handle API error on load', () => {
      const error = { status: 500, message: 'Server error' };
      transactionService.getTransactions.and.returnValue(throwError(() => error));

      fixture.detectChanges();

      expect(component.loading).toBeFalse();
      expect(component.error).toBe('Failed to load transactions. Please try again.');
    });
  });

  describe('empty state', () => {
    it('should show empty state when no transactions exist', () => {
      transactionService.getTransactions.and.returnValue(of(emptyPage));

      fixture.detectChanges();

      expect(component.isEmpty).toBeTrue();
      expect(component.transactions.length).toBe(0);

      const compiled = fixture.nativeElement as HTMLElement;
      const emptyStateText = compiled.querySelector('h3');
      expect(emptyStateText?.textContent).toContain('No transactions yet');
    });

    it('should not show empty state when loading', () => {
      component.loading = true;
      component.transactions = [];

      expect(component.isEmpty).toBeFalse();
    });

    it('should not show empty state when there is an error', () => {
      component.error = 'Some error';
      component.transactions = [];

      expect(component.isEmpty).toBeFalse();
    });
  });

  describe('sorting', () => {
    beforeEach(() => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();
    });

    it('should toggle sort direction when clicking same column', () => {
      transactionService.getTransactions.calls.reset();
      transactionService.getTransactions.and.returnValue(of(mockPage));

      component.onSort('transactionDate');

      expect(component.sortBy).toBe('transactionDate');
      expect(component.sortDirection).toBe('asc');
      expect(transactionService.getTransactions).toHaveBeenCalledWith({
        page: 0,
        size: 20,
        sortBy: 'transactionDate',
        sortDirection: 'asc'
      });
    });

    it('should change sort column and default to desc', () => {
      transactionService.getTransactions.calls.reset();
      transactionService.getTransactions.and.returnValue(of(mockPage));

      component.onSort('amount');

      expect(component.sortBy).toBe('amount');
      expect(component.sortDirection).toBe('desc');
      expect(component.currentPage).toBe(0); // Reset to first page
    });

    it('should display correct sort icon', () => {
      component.sortBy = 'transactionDate';
      component.sortDirection = 'desc';

      expect(component.getSortIcon('transactionDate')).toBe('↓');
      expect(component.getSortIcon('amount')).toBe('');

      component.sortDirection = 'asc';
      expect(component.getSortIcon('transactionDate')).toBe('↑');
    });
  });

  describe('pagination', () => {
    beforeEach(() => {
      const multiPageResult: Page<Transaction> = {
        ...mockPage,
        totalPages: 3,
        totalElements: 55
      };
      transactionService.getTransactions.and.returnValue(of(multiPageResult));
      fixture.detectChanges();
    });

    it('should navigate to next page', () => {
      transactionService.getTransactions.calls.reset();
      const page1Result: Page<Transaction> = { ...mockPage, page: 1 };
      transactionService.getTransactions.and.returnValue(of(page1Result));

      component.onPageChange(1);

      expect(component.currentPage).toBe(1);
      expect(transactionService.getTransactions).toHaveBeenCalledWith({
        page: 1,
        size: 20,
        sortBy: 'transactionDate',
        sortDirection: 'desc'
      });
    });

    it('should navigate to previous page', () => {
      component.currentPage = 2;
      transactionService.getTransactions.calls.reset();
      const page1Result: Page<Transaction> = { ...mockPage, page: 1 };
      transactionService.getTransactions.and.returnValue(of(page1Result));

      component.onPageChange(1);

      expect(component.currentPage).toBe(1);
    });

    it('should not navigate beyond page bounds', () => {
      const currentPage = component.currentPage;
      transactionService.getTransactions.calls.reset();

      component.onPageChange(-1);
      expect(component.currentPage).toBe(currentPage);
      expect(transactionService.getTransactions).not.toHaveBeenCalled();

      component.onPageChange(999);
      expect(component.currentPage).toBe(currentPage);
      expect(transactionService.getTransactions).not.toHaveBeenCalled();
    });

    it('should correctly identify if has previous page', () => {
      component.currentPage = 0;
      expect(component.hasPreviousPage).toBeFalse();

      component.currentPage = 1;
      expect(component.hasPreviousPage).toBeTrue();
    });

    it('should correctly identify if has next page', () => {
      component.totalPages = 3;
      component.currentPage = 2;
      expect(component.hasNextPage).toBeFalse();

      component.currentPage = 1;
      expect(component.hasNextPage).toBeTrue();
    });
  });

  describe('formatting', () => {
    it('should format date correctly', () => {
      const formatted = component.formatDate('2025-01-15');
      expect(formatted).toContain('Jan');
      expect(formatted).toContain('15');
      expect(formatted).toContain('2025');
    });

    it('should return correct amount class for income', () => {
      expect(component.getAmountClass(TransactionType.INCOME)).toBe('text-green-600');
    });

    it('should return correct amount class for expense', () => {
      expect(component.getAmountClass(TransactionType.EXPENSE)).toBe('text-red-600');
    });
  });

  describe('delete transaction', () => {
    beforeEach(() => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();
      spyOn(window, 'confirm');
    });

    it('should delete transaction when user confirms', () => {
      (window.confirm as jasmine.Spy).and.returnValue(true);
      transactionService.deleteTransaction.and.returnValue(of(void 0));
      transactionService.getTransactions.calls.reset();
      transactionService.getTransactions.and.returnValue(of(emptyPage));

      component.onDelete(mockTransaction);

      expect(window.confirm).toHaveBeenCalled();
      expect(transactionService.deleteTransaction).toHaveBeenCalledWith(mockTransaction.id);
      expect(transactionService.getTransactions).toHaveBeenCalled(); // Reload after delete
    });

    it('should not delete when user cancels', () => {
      (window.confirm as jasmine.Spy).and.returnValue(false);

      component.onDelete(mockTransaction);

      expect(window.confirm).toHaveBeenCalled();
      expect(transactionService.deleteTransaction).not.toHaveBeenCalled();
    });

    it('should handle delete error', () => {
      (window.confirm as jasmine.Spy).and.returnValue(true);
      const error = { status: 500, message: 'Server error' };
      transactionService.deleteTransaction.and.returnValue(throwError(() => error));

      component.onDelete(mockTransaction);

      expect(component.error).toBe('Failed to delete transaction. Please try again.');
      expect(component.loading).toBeFalse();
    });
  });

  describe('retry functionality', () => {
    it('should reload transactions on retry', () => {
      transactionService.getTransactions.calls.reset();
      transactionService.getTransactions.and.returnValue(of(mockPage));

      component.error = 'Some error';
      component.onRetry();

      expect(transactionService.getTransactions).toHaveBeenCalled();
      expect(component.error).toBeNull();
    });
  });

  describe('rendering', () => {
    it('should display transaction list correctly', () => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Test transaction');
      expect(compiled.textContent).toContain('Salary');
    });

    it('should show loading state', () => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();

      component.loading = true;
      component.transactions = [];
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Loading transactions');
    });

    it('should show error state with retry button', () => {
      transactionService.getTransactions.and.returnValue(throwError(() => ({ status: 500 })));
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      const retryButton = compiled.querySelector('button');
      expect(retryButton?.textContent).toContain('Retry');
    });
  });
});
