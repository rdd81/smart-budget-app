import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AddTransactionComponent } from './add-transaction.component';
import { CategoryService } from '../../services/category.service';
import { TransactionService } from '../../services/transaction.service';
import { Category, Transaction, TransactionType } from '../../models/transaction.model';
import { HttpErrorResponse } from '@angular/common/http';

class CategoryServiceMock {
  categories: Category[] = [
    { id: 'cat-expense', name: 'Food', type: TransactionType.EXPENSE },
    { id: 'cat-income', name: 'Salary', type: TransactionType.INCOME }
  ];

  getCategories() {
    return of(this.categories);
  }
}

class TransactionServiceMock {
  createTransaction = jasmine.createSpy('createTransaction').and.returnValue(of({
    id: 'tx-1'
  } as unknown as Transaction));
}

describe('AddTransactionComponent', () => {
  let component: AddTransactionComponent;
  let fixture: ComponentFixture<AddTransactionComponent>;
  let transactionService: TransactionServiceMock;
  let categoryService: CategoryServiceMock;

  beforeEach(async () => {
    transactionService = new TransactionServiceMock();
    categoryService = new CategoryServiceMock();

    await TestBed.configureTestingModule({
      imports: [AddTransactionComponent],
      providers: [
        provideRouter([]),
        { provide: TransactionService, useValue: transactionService },
        { provide: CategoryService, useValue: categoryService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component and load expense categories by default', () => {
    expect(component).toBeTruthy();
    expect(component.filteredCategories.length).toBe(1);
    expect(component.filteredCategories[0].type).toBe(TransactionType.EXPENSE);
  });

  it('should filter categories when transaction type changes', () => {
    component.form.controls.transactionType.setValue(TransactionType.INCOME);
    component.applyCategoryFilter();
    expect(component.filteredCategories.length).toBe(1);
    expect(component.filteredCategories[0].type).toBe(TransactionType.INCOME);
  });

  it('should not submit when form is invalid', () => {
    component.onSubmit();
    expect(transactionService.createTransaction).not.toHaveBeenCalled();
  });

  it('should submit form and display success message on success', () => {
    component.form.setValue({
      amount: '120.50',
      transactionDate: component.todayIso,
      description: 'Groceries',
      categoryId: 'cat-expense',
      transactionType: TransactionType.EXPENSE
    });

    component.onSubmit();

    expect(transactionService.createTransaction).toHaveBeenCalledWith({
      amount: 120.5,
      transactionDate: component.todayIso,
      description: 'Groceries',
      categoryId: 'cat-expense',
      transactionType: TransactionType.EXPENSE
    });
    expect(component.successMessage).toBeTruthy();
  });

  it('should handle API failure gracefully', () => {
    transactionService.createTransaction.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 400 }))
    );

    component.form.setValue({
      amount: '50',
      transactionDate: component.todayIso,
      description: 'Error case',
      categoryId: 'cat-expense',
      transactionType: TransactionType.EXPENSE
    });

    component.onSubmit();

    expect(component.submitError).toBeTruthy();
  });
});
