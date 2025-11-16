import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DateRangeSelectorComponent } from './date-range-selector.component';

describe('DateRangeSelectorComponent', () => {
  let component: DateRangeSelectorComponent;
  let fixture: ComponentFixture<DateRangeSelectorComponent>;

  beforeEach(async () => {
    sessionStorage.clear();
    await TestBed.configureTestingModule({
      imports: [DateRangeSelectorComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DateRangeSelectorComponent);
    component = fixture.componentInstance;
  });

  it('should emit default range on init', () => {
    const emitSpy = spyOn(component.rangeChange, 'emit');
    fixture.detectChanges();
    expect(emitSpy).toHaveBeenCalled();
  });

  it('should emit when preset selected', () => {
    fixture.detectChanges();
    const emitSpy = spyOn(component.rangeChange, 'emit');
    component.selectPreset('LAST_MONTH');
    expect(emitSpy).toHaveBeenCalled();
  });

  it('should validate custom range', () => {
    fixture.detectChanges();
    component.selectPreset('CUSTOM');
    component.customRange = { start: '2025-02-10', end: '2025-02-05' };
    component.applyCustomRange();
    expect(component.error).toContain('Start date');
  });
});
