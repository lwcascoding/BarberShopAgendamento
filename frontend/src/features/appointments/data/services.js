export const services = [
  { id: 'HAIRCUT', name: 'Corte de Cabelo', duration: 30, price: 50, icon: 'scissors' },
  { id: 'BEARD', name: 'Barba', duration: 30, price: 40, icon: 'waves' },
  { id: 'HAIRCUT_AND_BEARD', name: 'Corte + Barba', duration: 60, price: 80, icon: 'scissors' },
  { id: 'EYEBROW', name: 'Sobrancelha', duration: 15, price: 20, icon: 'waves' },
  { id: 'NECKLINE', name: 'Pezinho', duration: 15, price: 20, icon: 'razor' },
];

export const fallbackBarbers = [
  { id: 'felipe-fernandes', name: 'Felipe Fernandes', phone: '24 98140-9877', active: true },
];

export const fallbackTimes = [];

export function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
}
