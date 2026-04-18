export interface NavLink {
  label: string;
  href: string;
}

export interface ProductCard {
  id: string;
  index: string;
  badge?: string;
  name: string;
  price: number;
  material: string;
  color: string;
  slug: string;
  image: string;
}

export interface MarqueeItem {
  text: string;
}

export interface LookbookFrame {
  id: string;
  label: string;
  size: 'large' | 'small';
  image: string;
}
