import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgendamientoOnline } from './agendamiento-online';

describe('AgendamientoOnline', () => {
  let component: AgendamientoOnline;
  let fixture: ComponentFixture<AgendamientoOnline>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgendamientoOnline],
    }).compileComponents();

    fixture = TestBed.createComponent(AgendamientoOnline);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
