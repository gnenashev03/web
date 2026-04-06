import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Taskeditcomponent } from './taskeditcomponent';

describe('Taskeditcomponent', () => {
  let component: Taskeditcomponent;
  let fixture: ComponentFixture<Taskeditcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Taskeditcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Taskeditcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
