import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { BoletoService } from './boleto';

describe('BoletoService', () => {
  let service: BoletoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(BoletoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
