import { Component } from '@angular/core';
import { MarqueeItem } from '../../models/landing.models';

@Component({
  selector: 'app-marquee',
  standalone: true,
  imports: [],
  templateUrl: './marquee.component.html',
  styleUrl: './marquee.component.scss'
})
export class MarqueeComponent {
  items: MarqueeItem[] = [
    { text: 'ALTEA LOW' }, { text: '—' },
    { text: 'MORO OXFORD' }, { text: '—' },
    { text: 'SABLE BOOT' }, { text: '—' },
    { text: 'OTOÑO / INVIERNO 25' }, { text: '—' },
    { text: 'HECHO A MANO' }, { text: '—' },
    { text: 'EDICION LIMITADA' }, { text: '—' },
  ];

  doubledItems: MarqueeItem[] = [...this.items, ...this.items];
}
