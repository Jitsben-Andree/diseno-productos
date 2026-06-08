import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KaizenFeedback } from './kaizen-feedback';

describe('KaizenFeedback', () => {
  let component: KaizenFeedback;
  let fixture: ComponentFixture<KaizenFeedback>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KaizenFeedback],
    }).compileComponents();

    fixture = TestBed.createComponent(KaizenFeedback);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
