import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidacionClinica } from './validacion-clinica';

describe('ValidacionClinica', () => {
  let component: ValidacionClinica;
  let fixture: ComponentFixture<ValidacionClinica>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValidacionClinica],
    }).compileComponents();

    fixture = TestBed.createComponent(ValidacionClinica);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
