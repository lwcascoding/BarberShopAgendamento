import { icon } from '../common/icons.js';

const primaryItems = [
  ['calendar', 'Agendamentos', 'booking', true],
  ['calendar', 'Meus Agendamentos', 'my-appointments'],
  ['scissors', 'Serviços', 'services'],
  ['user', 'Profissionais', 'professionals'],
  ['phone', 'Contato', 'contact'],
];

function renderNavigationItem([iconName, label, route, active = false]) {
  return `
    <button class="nav-item${active ? ' is-active' : ''}" type="button" data-nav="${route}">
      ${icon(iconName, '', 22)}
      <span>${label}</span>
    </button>
  `;
}

export function renderSidebar() {
  return `
    <div class="sidebar-brand" aria-label="Barbearia Felipe Fernandes">
      <div class="brand-emblem">
        <span class="brand-crown">♛</span>
        ${icon('scissors', 'brand-scissors', 48)}
      </div>
      <span class="brand-kicker">Barbearia</span>
      <strong>Felipe</strong>
      <strong class="brand-surname">Fernandes</strong>
    </div>

    <nav class="sidebar-nav" aria-label="Navegação principal">
      ${primaryItems.map(renderNavigationItem).join('')}
      <div class="nav-separator"></div>
      ${renderNavigationItem(['user', 'Perfil', 'profile'])}
      ${renderNavigationItem(['logout', 'Sair', 'logout'])}
    </nav>

    <address class="contact-card">
      <div class="contact-heading">
        <div class="barber-pole" aria-hidden="true"><span></span></div>
        <div><strong>Barbearia</strong><span>Felipe Fernandes</span></div>
      </div>
      <p>${icon('pin', '', 17)}<span>(Dourados Shopping)<br>Av. Sávio Cota de Almeida<br>Gama-2214 / Retiro,<br>Volta Redonda 27281422</span></p>
      <a href="tel:+5524981409877">${icon('phone', '', 17)}<span>24 98140-9877</span></a>
      <a href="https://www.instagram.com/barbeariaffernandes/#" target="_blank" rel="noreferrer">${icon('instagram', '', 17)}<span>@barbeariaffernandes</span></a>
    </address>
  `;
}
