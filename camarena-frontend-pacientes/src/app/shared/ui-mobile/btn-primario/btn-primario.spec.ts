import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BtnPrimario } from './btn-primario';

describe('BtnPrimario', () => {
  let component: BtnPrimario;
  let fixture: ComponentFixture<BtnPrimario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BtnPrimario],
    }).compileComponents();

    fixture = TestBed.createComponent(BtnPrimario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
