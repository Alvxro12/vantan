import { Component, signal, viewChild, ElementRef, AfterViewInit } from '@angular/core';
import { LookbookFrame } from '../../models/landing.models';

@Component({
  selector: 'app-lookbook',
  standalone: true,
  imports: [],
  templateUrl: './lookbook.component.html'
})
export class LookbookComponent implements AfterViewInit {
  sectionRef = viewChild<ElementRef>('sectionRef');
  visible = signal<boolean>(false);

  frames: LookbookFrame[] = [
    { id: '01', label: '[ LOOKBOOK • PLATE 01 • EDITORIAL ]', size: 'large',  image: 'assets/images/modelo_zapato.jpeg' },
    { id: '02', label: '[ LOOKBOOK • PLATE 02 ]',             size: 'small',  image: 'assets/images/modelo_zapato_2.jpeg' },
    { id: '03', label: '[ LOOKBOOK • PLATE 03 ]',             size: 'small',  image: 'assets/images/modelo_zapato_3.jpeg' },
  ];

  get largeFrame(): LookbookFrame {
    return this.frames.find(f => f.size === 'large')!;
  }

  get smallFrames(): LookbookFrame[] {
    return this.frames.filter(f => f.size === 'small');
  }

  ngAfterViewInit(): void {
    const obs = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) this.visible.set(true); },
      { threshold: 0.1 }
    );
    obs.observe(this.sectionRef()!.nativeElement);
  }
}
