import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginDni } from './login-dni';

describe('LoginDni', () => {
  let component: LoginDni;
  let fixture: ComponentFixture<LoginDni>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginDni],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginDni);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
