import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KanbanMuestras } from './kanban-muestras';

describe('KanbanMuestras', () => {
  let component: KanbanMuestras;
  let fixture: ComponentFixture<KanbanMuestras>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KanbanMuestras],
    }).compileComponents();

    fixture = TestBed.createComponent(KanbanMuestras);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
