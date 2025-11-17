import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subject, catchError, combineLatest, debounceTime, distinctUntilChanged, of, startWith, switchMap, takeUntil, tap } from 'rxjs';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { Category, Transaction, TransactionRequest, TransactionType } from '../../models/transaction.model';
import { CategorizationService } from '../../services/categorization.service';
import { CategorySuggestion } from '../../models/categorization.model';

@Component({
  selector: 'app-edit-transaction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './edit-transaction.component.html',
  styleUrl: './edit-transaction.component.css'
})
export class EditTransactionComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);
  private readonly categorizationService = inject(CategorizationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroy$ = new Subject<void>();

  readonly TransactionType = TransactionType;
  readonly transactionId = this.route.snapshot.paramMap.get('id') ?? '';
  readonly SUGGESTION_CONFIDENCE_THRESHOLD = 0.6;

  loadingTransaction = true;
  loadingCategories = true;
  notFound = false;
  loadError: string | null = null;
  submitError: string | null = null;
  successMessage: string | null = null;
  submitInProgress = false;

  categories: Category[] = [];
  filteredCategories: Category[] = [];
  suggestion: CategorySuggestion | null = null;
  suggestionLoading = false;
  suggestionError: string | null = null;

  form = this.fb.group({
    amount: ['', [Validators.required, Validators.min(0.01)]],
    transactionDate: ['', [Validators.required, EditTransactionComponent.notInFutureValidator]],
    description: ['', [Validators.required, Validators.maxLength(255)]],
    categoryId: ['', Validators.required],
    transactionType: [TransactionType.EXPENSE, Validators.required]
  });

  ngOnInit(): void {
    if (!this.transactionId) {
      this.notFound = true;
      this.loadingTransaction = false;
      return;
    }

    this.form.controls.transactionType.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.applyCategoryFilter());

    this.fetchCategories();
    this.fetchTransaction();
    this.setupSuggestionListener();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  static notInFutureValidator(control: { value: string | null }) {
    if (!control.value) {
      return null;
    }
    const selected = new Date(control.value);
    const today = new Date();
    selected.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    return selected > today ? { futureDate: true } : null;
  }

  fetchCategories(): void {
    this.loadingCategories = true;
    this.categoryService.getCategories().pipe(takeUntil(this.destroy$)).subscribe({
      next: categories => {
        this.categories = categories;
        this.loadingCategories = false;
        this.applyCategoryFilter();
      },
      error: () => {
        this.loadingCategories = false;
        this.loadError = 'Unable to load categories. Please try again.';
      }
    });
  }

  fetchTransaction(): void {
    this.loadingTransaction = true;
    this.transactionService.getTransaction(this.transactionId).pipe(takeUntil(this.destroy$)).subscribe({
      next: transaction => {
        this.loadingTransaction = false;
        this.patchForm(transaction);
      },
      error: error => {
        this.loadingTransaction = false;
        if (error.status === 404) {
          this.notFound = true;
        } else {
          this.loadError = 'Unable to load transaction. Please try again.';
        }
      }
    });
  }

  patchForm(transaction: Transaction): void {
    this.form.patchValue({
      amount: transaction.amount,
      transactionDate: transaction.transactionDate,
      description: transaction.description,
      categoryId: transaction.category?.id ?? '',
      transactionType: transaction.transactionType
    });
    this.applyCategoryFilter();
  }

  applyCategoryFilter(): void {
    const selectedType = this.form.controls.transactionType.value ?? TransactionType.EXPENSE;
    this.filteredCategories = this.categories.filter(category => category.type === selectedType);
    const currentCategory = this.form.controls.categoryId.value;
    if (currentCategory && !this.filteredCategories.some(category => category.id === currentCategory)) {
      this.form.controls.categoryId.setValue('');
    }
  }

  hasError(controlName: keyof typeof this.form.controls, errorCode: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.touched && control.hasError(errorCode);
  }

  onSubmit(): void {
    this.submitError = null;
    this.successMessage = null;

    if (this.form.invalid || this.loadingTransaction || this.loadingCategories) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.value;
    const payload: TransactionRequest = {
      amount: Number(formValue.amount),
      transactionDate: formValue.transactionDate as string,
      description: formValue.description as string,
      categoryId: formValue.categoryId as string,
      transactionType: formValue.transactionType as TransactionType
    };

    this.submitInProgress = true;
    this.transactionService.updateTransaction(this.transactionId, payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.submitInProgress = false;
          this.successMessage = 'Transaction updated successfully.';
        },
        error: error => {
          this.submitInProgress = false;
          this.submitError = error.status === 404
            ? 'This transaction no longer exists.'
            : 'Failed to update transaction. Please try again.';
        }
      });
  }

  onCancel(): void {
    this.router.navigate(['/transactions']);
  }

  acceptSuggestion(): void {
    if (this.suggestion?.categoryId) {
      this.form.controls.categoryId.setValue(this.suggestion.categoryId);
    }
  }

  private setupSuggestionListener(): void {
    const description$ = this.form.controls.description.valueChanges.pipe(
      startWith(this.form.controls.description.value ?? '')
    );
    const amount$ = this.form.controls.amount.valueChanges.pipe(
      startWith(this.form.controls.amount.value ?? '')
    );
    const type$ = this.form.controls.transactionType.valueChanges.pipe(
      startWith(this.form.controls.transactionType.value ?? TransactionType.EXPENSE)
    );

    combineLatest([description$, amount$, type$])
      .pipe(
        debounceTime(500),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        tap(() => this.suggestionError = null),
        switchMap(([description, amount, transactionType]) => {
          const trimmedDescription = (description ?? '').trim();
          if (!trimmedDescription || !transactionType) {
            this.suggestion = null;
            this.suggestionLoading = false;
            return of(null);
          }
          this.suggestionLoading = true;
          return this.categorizationService.suggestCategory({
            description: trimmedDescription,
            amount: amount ? Number(amount) : undefined,
            transactionType
          }).pipe(
            catchError(() => {
              this.suggestionError = 'Unable to fetch suggestion right now.';
              return of(null);
            })
          );
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(suggestion => {
        this.suggestionLoading = false;
        if (suggestion && suggestion.confidence >= this.SUGGESTION_CONFIDENCE_THRESHOLD) {
          this.suggestion = suggestion;
        } else {
          this.suggestion = null;
        }
      });
  }
}
