import { TestBed } from '@angular/core/testing';

import { Boleto } from './boleto';

describe('Boleto', () => {
  let service: Boleto;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Boleto);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
