import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Subject, combineLatest, debounceTime, distinctUntilChanged, of, switchMap, takeUntil, tap, catchError, startWith } from 'rxjs';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { Category, Transaction, TransactionRequest, TransactionType } from '../../models/transaction.model';
import { CategorizationService } from '../../services/categorization.service';
import { CategorySuggestion } from '../../models/categorization.model';

@Component({
  selector: 'app-add-transaction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './add-transaction.component.html',
  styleUrl: './add-transaction.component.css'
})
export class AddTransactionComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);
  private readonly categorizationService = inject(CategorizationService);
  private readonly router = inject(Router);
  private readonly destroy$ = new Subject<void>();

  readonly TransactionType = TransactionType;
  readonly SUGGESTION_CONFIDENCE_THRESHOLD = 0.6;

  form = this.fb.group({
    amount: ['', [Validators.required, Validators.min(0.01)]],
    transactionDate: [this.todayIso, [Validators.required, AddTransactionComponent.notInFutureValidator]],
    description: ['', [Validators.required, Validators.maxLength(255)]],
    categoryId: ['', Validators.required],
    transactionType: [TransactionType.EXPENSE, Validators.required]
  });

  categories: Category[] = [];
  filteredCategories: Category[] = [];
  loadingCategories = false;
  loadError: string | null = null;
  submitError: string | null = null;
  successMessage: string | null = null;
  submitInProgress = false;
  suggestion: CategorySuggestion | null = null;
  suggestionLoading = false;
  suggestionError: string | null = null;

  ngOnInit(): void {
    this.fetchCategories();
    this.form.controls.transactionType.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.applyCategoryFilter());
    this.setupSuggestionListener();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get todayIso(): string {
    return new Date().toISOString().split('T')[0] ?? '';
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
    this.loadError = null;
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

    if (this.form.invalid || this.loadingCategories) {
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
    this.transactionService.createTransaction(payload).pipe(takeUntil(this.destroy$)).subscribe({
      next: (transaction: Transaction) => {
        this.submitInProgress = false;
        this.successMessage = 'Transaction added successfully.';
        this.resetForm(transaction.transactionType);
      },
      error: () => {
        this.submitInProgress = false;
        this.submitError = 'Failed to save transaction. Please try again.';
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/transactions']);
  }

  reloadCategories(): void {
    this.fetchCategories();
  }

  acceptSuggestion(): void {
    if (this.suggestion?.categoryId) {
      this.form.controls.categoryId.setValue(this.suggestion.categoryId);
    }
  }

  private resetForm(transactionType: TransactionType | null): void {
    const nextType = transactionType ?? this.form.controls.transactionType.value ?? TransactionType.EXPENSE;
    this.form.reset({
      amount: '',
      transactionDate: this.todayIso,
      description: '',
      categoryId: '',
      transactionType: nextType
    });
    this.applyCategoryFilter();
    this.form.markAsPristine();
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
        tap(() => {
          this.suggestionError = null;
        }),
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
