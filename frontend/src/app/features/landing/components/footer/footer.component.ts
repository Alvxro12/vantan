import { Component, signal, viewChild, ElementRef, AfterViewInit } from '@angular/core';

interface FooterColumn {
  title: string;
  links: string[];
}

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [],
  templateUrl: './footer.component.html'
})
export class FooterComponent implements AfterViewInit {
  sectionRef = viewChild<ElementRef>('sectionRef');
  visible = signal<boolean>(false);

  columns: FooterColumn[] = [
    {
      title: 'COLECCION',
      links: ['Otoño / Invierno 26', 'Primavera 26', 'Archivo', 'Ediciones limitadas', 'Custom']
    },
    {
      title: 'EMPRESA',
      links: ['Historia', 'Artesanos', 'Materiales', 'Boutiques', 'Prensa']
    },
    {
      title: 'LEGAL',
      links: ['Términos', 'Privacidad', 'Cookies', 'Envíos y devoluciones', 'Cuidado']
    }
  ];

  ngAfterViewInit(): void {
    const obs = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) this.visible.set(true); },
      { threshold: 0.1 }
    );
    obs.observe(this.sectionRef()!.nativeElement);
  }
}
