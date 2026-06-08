import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisorPdf } from './visor-pdf';

describe('VisorPdf', () => {
  let component: VisorPdf;
  let fixture: ComponentFixture<VisorPdf>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisorPdf],
    }).compileComponents();

    fixture = TestBed.createComponent(VisorPdf);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
