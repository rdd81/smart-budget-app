import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { EditTransactionComponent } from './edit-transaction.component';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { Transaction, TransactionType } from '../../models/transaction.model';
import { HttpErrorResponse } from '@angular/common/http';

class ActivatedRouteMock {
  snapshot = {
    paramMap: {
      get: () => 'tx-1'
    }
  } as ActivatedRoute['snapshot'];
}

class TransactionServiceMock {
  getTransaction = jasmine.createSpy('getTransaction').and.returnValue(of({
    id: 'tx-1',
    amount: 150,
    transactionDate: '2025-02-01',
    description: 'Test',
    category: { id: 'cat-expense', name: 'Food', type: TransactionType.EXPENSE },
    transactionType: TransactionType.EXPENSE
  } as unknown as Transaction));

  updateTransaction = jasmine.createSpy('updateTransaction').and.returnValue(of({} as Transaction));
}

class CategoryServiceMock {
  getCategories = jasmine.createSpy('getCategories').and.returnValue(of([
    { id: 'cat-expense', name: 'Food', type: TransactionType.EXPENSE },
    { id: 'cat-income', name: 'Salary', type: TransactionType.INCOME }
  ]));
}

describe('EditTransactionComponent', () => {
  let component: EditTransactionComponent;
  let fixture: ComponentFixture<EditTransactionComponent>;
  let transactionService: TransactionServiceMock;

  beforeEach(async () => {
    transactionService = new TransactionServiceMock();

    await TestBed.configureTestingModule({
      imports: [EditTransactionComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useClass: ActivatedRouteMock },
        { provide: TransactionService, useValue: transactionService },
        { provide: CategoryService, useClass: CategoryServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EditTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component and load transaction', () => {
    expect(component).toBeTruthy();
    expect(component.form.value.description).toBe('Test');
  });

  it('should change category options when transaction type toggles', () => {
    component.form.controls.transactionType.setValue(TransactionType.INCOME);
    component.applyCategoryFilter();
    expect(component.filteredCategories.length).toBe(1);
    expect(component.filteredCategories[0].type).toBe(TransactionType.INCOME);
  });

  it('should submit valid form and call update service', () => {
    component.form.controls.description.setValue('Updated desc');
    component.onSubmit();

    expect(transactionService.updateTransaction).toHaveBeenCalledWith('tx-1', jasmine.objectContaining({
      description: 'Updated desc'
    }));
  });

  it('should handle 404 on update', () => {
    transactionService.updateTransaction.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 404 }))
    );

    component.onSubmit();
    expect(component.submitError).toContain('no longer exists');
  });
});
