import { Component, HostListener, signal } from '@angular/core';
import { NavLink } from '../../models/landing.models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  scrolled = signal<boolean>(false);
  menuOpen = signal<boolean>(false);

  navLinks: NavLink[] = [
    { label: 'COLECCION', href: '/coleccion' },
    { label: 'LOOKBOOK',  href: '/lookbook' },
    { label: 'ARCHIVO',   href: '/archivo' },
    { label: 'HISTORIA',  href: '/historia' },
    { label: 'CONTACTO',  href: '/contacto' },
  ];

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled.set(window.scrollY > 20);
  }

  toggleMenu(): void {
    this.menuOpen.update(open => !open);
  }
}
