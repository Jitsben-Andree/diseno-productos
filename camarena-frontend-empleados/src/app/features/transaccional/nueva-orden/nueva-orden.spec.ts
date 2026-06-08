import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevaOrden } from './nueva-orden';

describe('NuevaOrden', () => {
  let component: NuevaOrden;
  let fixture: ComponentFixture<NuevaOrden>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevaOrden],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaOrden);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
