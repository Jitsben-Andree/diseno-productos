import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneradorCodigos } from './generador-codigos';

describe('GeneradorCodigos', () => {
  let component: GeneradorCodigos;
  let fixture: ComponentFixture<GeneradorCodigos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneradorCodigos],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneradorCodigos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
