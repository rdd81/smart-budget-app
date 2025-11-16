import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { TransactionListComponent } from './transaction-list.component';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { Category, Transaction, TransactionType } from '../../models/transaction.model';
import { Page } from '../../models/pagination.model';

describe('TransactionListComponent', () => {
  let component: TransactionListComponent;
  let fixture: ComponentFixture<TransactionListComponent>;
  let transactionService: jasmine.SpyObj<TransactionService>;
  let categoryService: jasmine.SpyObj<CategoryService>;

  const mockTransaction: Transaction = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    amount: 100.5,
    transactionDate: '2025-01-15',
    description: 'Test transaction',
    category: { id: 'cat-123', name: 'Food', type: TransactionType.EXPENSE },
    transactionType: TransactionType.EXPENSE,
    createdAt: '2025-01-15T10:00:00Z',
    updatedAt: '2025-01-15T10:00:00Z'
  };

  const mockIncomeTransaction: Transaction = {
    ...mockTransaction,
    id: 'income-123',
    amount: 500,
    description: 'Salary',
    category: { id: 'cat-salary', name: 'Salary', type: TransactionType.INCOME },
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

  const mockCategories: Category[] = [
    { id: 'cat-123', name: 'Food', type: TransactionType.EXPENSE },
    { id: 'cat-salary', name: 'Salary', type: TransactionType.INCOME }
  ];

  beforeEach(async () => {
    sessionStorage.clear();
    const transactionSpy = jasmine.createSpyObj('TransactionService', ['getTransactions', 'getTransaction', 'deleteTransaction']);
    const categorySpy = jasmine.createSpyObj('CategoryService', ['getCategories']);

    await TestBed.configureTestingModule({
      imports: [TransactionListComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: TransactionService, useValue: transactionSpy },
        { provide: CategoryService, useValue: categorySpy }
      ]
    }).compileComponents();

    transactionService = TestBed.inject(TransactionService) as jasmine.SpyObj<TransactionService>;
    categoryService = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
    categoryService.getCategories.and.returnValue(of(mockCategories));
    fixture = TestBed.createComponent(TransactionListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    transactionService.getTransactions.and.returnValue(of(mockPage));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load categories and transactions', () => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();

      expect(categoryService.getCategories).toHaveBeenCalled();
      expect(transactionService.getTransactions).toHaveBeenCalledWith(jasmine.objectContaining({
        page: 0,
        size: 20,
        sortBy: 'transactionDate',
        sortDirection: 'desc'
      }));
      expect(component.transactions.length).toBe(2);
    });

    it('should handle load error', () => {
      transactionService.getTransactions.and.returnValue(throwError(() => ({ status: 500 })));
      fixture.detectChanges();
      expect(component.error).toBe('Failed to load transactions. Please try again.');
    });
  });

  describe('empty state', () => {
    it('should show empty state without filters', () => {
      transactionService.getTransactions.and.returnValue(of(emptyPage));
      fixture.detectChanges();
      expect(component.isEmpty).toBeTrue();
      expect(component.hasActiveFilters).toBeFalse();
    });

    it('should recognize filtered empty state', () => {
      transactionService.getTransactions.and.returnValue(of(emptyPage));
      fixture.detectChanges();
      component.filters.categoryIds = ['cat-123'];
      expect(component.hasActiveFilters).toBeTrue();
    });
  });

  describe('pagination and sorting', () => {
    beforeEach(() => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();
    });

    it('should change sort order', () => {
      component.onSort('amount');
      expect(component.sortBy).toBe('amount');
      expect(transactionService.getTransactions).toHaveBeenCalledTimes(2);
    });

    it('should change page', () => {
      component.totalPages = 2;
      component.onPageChange(1);
      expect(component.currentPage).toBe(1);
    });
  });

  describe('filters', () => {
    beforeEach(() => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();
    });

    it('should apply filters and reload data', () => {
      component.filters.dateFrom = '2025-01-01';
      component.filters.dateTo = '2025-01-31';
      component.filters.transactionType = TransactionType.INCOME;
      component.filters.categoryIds = ['cat-salary'];

      component.applyFilters();

      expect(transactionService.getTransactions).toHaveBeenCalledWith(jasmine.objectContaining({
        dateFrom: '2025-01-01',
        dateTo: '2025-01-31',
        transactionType: TransactionType.INCOME,
        categoryIds: ['cat-salary']
      }));
    });

    it('should clear filters', () => {
      component.filters.dateFrom = '2025-01-01';
      component.filters.categoryIds = ['cat-123'];
      component.clearFilters();

      expect(component.filters.transactionType).toBe('ALL');
      expect(component.filters.categoryIds.length).toBe(0);
    });
  });

  describe('delete transaction', () => {
    beforeEach(() => {
      transactionService.getTransactions.and.returnValue(of(mockPage));
      fixture.detectChanges();
    });

    it('should open and close dialog', () => {
      component.openDeleteDialog(mockTransaction);
      expect(component.showDeleteDialog).toBeTrue();
      component.closeDeleteDialog();
      expect(component.showDeleteDialog).toBeFalse();
    });

    it('should delete transaction', () => {
      transactionService.deleteTransaction.and.returnValue(of(void 0));
      component.openDeleteDialog(mockTransaction);
      component.confirmDelete();
      expect(transactionService.deleteTransaction).toHaveBeenCalledWith(mockTransaction.id);
    });

    it('should handle delete error', () => {
      transactionService.deleteTransaction.and.returnValue(throwError(() => ({ status: 500 })));
      component.openDeleteDialog(mockTransaction);
      component.confirmDelete();
      expect(component.deleteError).toBe('Failed to delete transaction. Please try again.');
    });
  });
});
