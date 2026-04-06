import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Tasklistcomponent } from './tasklistcomponent';

describe('Tasklistcomponent', () => {
  let component: Tasklistcomponent;
  let fixture: ComponentFixture<Tasklistcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Tasklistcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Tasklistcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
