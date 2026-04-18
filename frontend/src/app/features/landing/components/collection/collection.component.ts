import { Component, signal, viewChild, ElementRef, AfterViewInit } from '@angular/core';
import { UpperCasePipe } from '@angular/common';
import { ProductCard } from '../../models/landing.models';

@Component({
  selector: 'app-collection',
  standalone: true,
  imports: [UpperCasePipe],
  templateUrl: './collection.component.html'
})
export class CollectionComponent implements AfterViewInit {
  sectionRef = viewChild<ElementRef>('sectionRef');
  visible = signal<boolean>(false);

  products: ProductCard[] = [
    {
      id: '1', index: '001 / 003', badge: 'EDICION LIMITADA',
      name: 'Altea Low', price: 420, material: 'NAPA', color: 'CREMA', slug: 'altea-low',
      image: 'assets/images/Altea_low_crema.jpeg'
    },
    {
      id: '2', index: '002 / 003', badge: 'NUEVO',
      name: 'Moro Oxford', price: 510, material: 'BOX CALF', color: 'OBSIDIANA', slug: 'moro-oxford',
      image: 'assets/images/Moro_Oxford_Obsidiana.jpeg'
    },
    {
      id: '3', index: '003 / 003', badge: 'HECHO A MANO',
      name: 'Sable Boot', price: 680, material: 'SUEDE', color: 'TABACO', slug: 'sable-boot',
      image: 'assets/images/Sable_boot_tabaco.jpeg'
    },
  ];

  ngAfterViewInit(): void {
    const obs = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) this.visible.set(true); },
      { threshold: 0.1 }
    );
    obs.observe(this.sectionRef()!.nativeElement);
  }
}
