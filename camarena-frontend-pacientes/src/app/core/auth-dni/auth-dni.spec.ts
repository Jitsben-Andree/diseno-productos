import { TestBed } from '@angular/core/testing';

import { AuthDni } from './auth-dni';

describe('AuthDni', () => {
  let service: AuthDni;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthDni);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
