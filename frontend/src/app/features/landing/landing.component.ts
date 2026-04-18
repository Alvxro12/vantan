import { Component } from '@angular/core';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HeroComponent } from './components/hero/hero.component';
import { CollectionComponent } from './components/collection/collection.component';
import { MarqueeComponent } from './components/marquee/marquee.component';
import { LookbookComponent } from './components/lookbook/lookbook.component';
import { FooterComponent } from './components/footer/footer.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [NavbarComponent, HeroComponent, CollectionComponent, MarqueeComponent, LookbookComponent, FooterComponent],
  templateUrl: './landing.component.html'
})
export class LandingComponent {}
