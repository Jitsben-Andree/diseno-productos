import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GrupoFamiliar } from './grupo-familiar';

describe('GrupoFamiliar', () => {
  let component: GrupoFamiliar;
  let fixture: ComponentFixture<GrupoFamiliar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GrupoFamiliar],
    }).compileComponents();

    fixture = TestBed.createComponent(GrupoFamiliar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
