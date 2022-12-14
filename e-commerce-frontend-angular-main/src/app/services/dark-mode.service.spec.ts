import { TestBed } from '@angular/core/testing';
import { DarkModeService } from './dark-mode.service';

describe('DarkModeService', () => {
  let service: DarkModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DarkModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have a method for changing the theme called toggleDarkTheme()', () => {
    //check the service for the toggle function
    expect(service.toggleDarkTheme).toMatch('toggleDarkTheme()');
  });

  it('should toggle dark mode on', () => {
    document.body.classList.remove('dark-theme') // Start in light mode
    //toggle dark theme on
    service.toggleDarkTheme(); //on
    //check for 'dark-theme' class in body tag
    expect(document.body.classList).toContain('dark-theme');
    expect(localStorage.getItem('theme')).toMatch('dark');
  });

  it('should toggle dark mode off', () => {
    document.body.classList.add('dark-theme') // start in dark mode
    service.toggleDarkTheme(); //off
    expect(localStorage.getItem('theme')).toMatch('light');
    expect(document.body.classList).toMatch('');
  });
});
