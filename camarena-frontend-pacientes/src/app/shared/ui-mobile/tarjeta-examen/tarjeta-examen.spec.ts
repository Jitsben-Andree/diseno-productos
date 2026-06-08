import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TarjetaExamen } from './tarjeta-examen';

describe('TarjetaExamen', () => {
  let component: TarjetaExamen;
  let fixture: ComponentFixture<TarjetaExamen>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarjetaExamen],
    }).compileComponents();

    fixture = TestBed.createComponent(TarjetaExamen);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
