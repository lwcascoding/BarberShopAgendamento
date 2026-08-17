import { icon } from '../components/common/icons.js';
import { renderSidebar } from '../components/layout/sidebar.js';
import { BookingWizard } from '../features/appointments/components/bookingWizard.js';
import { AdminDashboard } from '../features/appointments/components/adminDashboard.js';
import { NavigationPages } from '../features/appointments/components/navigationPages.js';
import { appointmentApi } from '../features/appointments/services/appointmentApi.js';
import { escapeHtml } from '../utils/html.js';

export class BarbershopApp {
  constructor(root) {
    this.root = root;
    this.toastTimer = null;
  }

  mount() {
    this.root.innerHTML = this.template();

    this.wizard = new BookingWizard(
      this.root.querySelector('#booking-wizard'),
      (message) => this.showToast(message),
    );
    this.adminDashboard = new AdminDashboard(
      this.root.querySelector('#booking-wizard'),
      (message) => this.showToast(message),
    );
    this.navigationPages = new NavigationPages(
      this.root.querySelector('#booking-wizard'),
      (message) => this.showToast(message),
      (route, options) => this.navigate(route, options),
    );
    this.wizard.mount();
    this.adminDashboard.mount();
    this.navigationPages.mount();
    this.bindEvents();
    this.openInitialView();
    this.refreshUpcomingAppointment();
    window.addEventListener('appointment:created', () => this.refreshUpcomingAppointment());
    window.addEventListener('appointment:updated', () => this.refreshUpcomingAppointment());
    window.addEventListener('popstate', () => this.openInitialView());
  }

  async refreshUpcomingAppointment() {
    const card = this.root.querySelector('#upcoming-appointment');
    if (!card) return;

    try {
      const upcoming = await appointmentApi.getUpcomingAppointment();

      if (!upcoming) {
        card.innerHTML = '<div class="upcoming-empty">Nenhum agendamento futuro.</div>';
        return;
      }

      const date = new Date(upcoming.startTime);
      const day = new Intl.DateTimeFormat('pt-BR', { day: '2-digit' }).format(date);
      const month = new Intl.DateTimeFormat('pt-BR', { month: 'short' }).format(date).replace('.', '').toUpperCase();
      const weekday = new Intl.DateTimeFormat('pt-BR', { weekday: 'long' }).format(date);
      const time = new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(date);

      card.innerHTML = `<time datetime="${upcoming.startTime}"><strong>${day}</strong><span>${month}</span></time><div><strong>${escapeHtml(upcoming.serviceName)}</strong><p>com ${escapeHtml(upcoming.barberName)}</p><small>${icon('clock', '', 16)} ${escapeHtml(weekday)} - ${time}</small></div><span class="confirmed">Confirmado</span>`;
    } catch {
      card.innerHTML = '<div class="upcoming-empty">Agenda temporariamente indisponível.</div>';
    }
  }

  openInitialView() {
    const routes = {
      '#admin': 'admin',
      '#meus-agendamentos': 'my-appointments',
      '#servicos': 'services',
      '#profissionais': 'professionals',
      '#contato': 'contact',
      '#perfil': 'profile',
    };
    const route = routes[window.location.hash] ?? 'booking';
    this.navigate(route, {}, false);
  }

  navigate(route, options = {}, updateHash = true) {
    const hashes = {
      booking: 'agendamento',
      'my-appointments': 'meus-agendamentos',
      admin: 'admin',
      services: 'servicos',
      professionals: 'profissionais',
      contact: 'contato',
      profile: 'perfil',
    };

    if (updateHash) window.history.pushState(null, '', `#${hashes[route]}`);

    this.root.querySelectorAll('[data-nav]').forEach((item) => {
      item.classList.toggle('is-active', item.dataset.nav === route);
    });

    if (route === 'booking') this.wizard.show(options);
    else if (route === 'admin') this.adminDashboard.show();
    else this.navigationPages.show(route);

    this.root.querySelector('#booking-wizard').scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  bindEvents() {
    const sidebar = this.root.querySelector('#sidebar');
    const backdrop = this.root.querySelector('#sidebar-backdrop');

    const closeMenu = () => {
      sidebar.classList.remove('is-open');
      backdrop.classList.remove('is-visible');
    };

    this.root.querySelector('[data-menu-open]').addEventListener('click', () => {
      sidebar.classList.add('is-open');
      backdrop.classList.add('is-visible');
    });
    this.root.querySelector('[data-menu-close]').addEventListener('click', closeMenu);
    backdrop.addEventListener('click', closeMenu);

    this.root.querySelectorAll('[data-route]').forEach((button) => {
      button.addEventListener('click', () => this.navigate(button.dataset.route));
    });

    this.root.querySelectorAll('[data-nav]').forEach((item) => {
      item.addEventListener('click', () => {
        closeMenu();
        const route = item.dataset.nav;

        if (route === 'logout') {
          appointmentApi.clearAdminCredentials();
          this.adminDashboard.authenticated = false;
          this.showToast('Sessão encerrada com sucesso.');
          this.navigate('booking');
          return;
        }

        this.navigate(route);
      });
    });

    this.root.querySelectorAll('[data-feedback]').forEach((button) => {
      button.addEventListener('click', () => this.showToast(button.dataset.feedback));
    });
  }

  showToast(message) {
    const toast = this.root.querySelector('#toast');
    toast.textContent = message;
    toast.classList.add('is-visible');
    window.clearTimeout(this.toastTimer);
    this.toastTimer = window.setTimeout(() => toast.classList.remove('is-visible'), 3400);
  }

  template() {
    return `
      <div class="app-shell">
        <button class="mobile-menu-button" type="button" data-menu-open aria-label="Abrir menu">${icon('menu')}</button>
        <div class="sidebar-backdrop" id="sidebar-backdrop"></div>
        <aside class="sidebar" id="sidebar">
          <button class="sidebar-close" type="button" data-menu-close aria-label="Fechar menu">${icon('close')}</button>
          ${renderSidebar()}
        </aside>

        <main class="main-content">
          <header class="hero">
            <div class="hero-actions">
              <button class="new-appointment-button" type="button" data-route="booking">${icon('calendar', '', 21)} Novo Agendamento</button>
              <button class="round-button" type="button" data-feedback="Você não possui novas notificações." aria-label="Notificações">${icon('bell', '', 21)}</button>
              <button class="profile-button" type="button" data-route="profile" aria-label="Abrir perfil">${icon('user', '', 23)}${icon('chevronDown', '', 16)}</button>
            </div>
            <div class="hero-copy">
              <p>Bem-vindo à</p>
              <h1>Barbearia<br>Felipe Fernandes</h1>
              <span class="title-ornament"><i></i>✦<i></i></span>
              <h2>Estilo é mais que aparência, é atitude.</h2>
            </div>
          </header>

          <div class="content-wrap">
            <div class="dashboard-grid">
              <section class="booking-panel" id="booking-wizard" aria-live="polite"></section>

              <aside class="right-rail">
                <section class="side-panel appointments-card">
                  <div class="side-panel-title"><h2>${icon('calendar', '', 23)} Próximos Agendamentos</h2><button type="button" data-route="my-appointments">Ver todos</button></div>
                  <article class="next-appointment" id="upcoming-appointment"><div class="upcoming-empty">Carregando agenda...</div></article>
                  <article class="promo-card">
                    <div class="promo-image" role="img" aria-label="Instrumentos profissionais de barbearia"></div>
                    <div class="promo-copy"><h3>Cuidado que transforma.</h3><p>Confiança que fica.</p><button type="button" data-route="services">Conheça nossos serviços</button></div>
                  </article>
                  <article class="hours-card">
                    <h3>${icon('clock', '', 19)} Horário de Funcionamento</h3>
                    <dl><div><dt>Segunda a Sexta</dt><dd>09:00 - 20:00</dd></div><div><dt>Sábado</dt><dd>08:00 - 18:00</dd></div><div><dt>Domingo</dt><dd>Fechado</dd></div></dl>
                  </article>
                  <article class="whatsapp-card">
                    <div><strong>Dúvidas ou precisar cancelar?</strong><p>Fale conosco pelo WhatsApp.</p></div>
                    <a href="https://wa.me/5524981409877" target="_blank" rel="noreferrer" aria-label="Conversar pelo WhatsApp">${icon('whatsapp', '', 27)}</a>
                  </article>
                </section>
              </aside>
            </div>

            <footer class="page-quote">
              <blockquote>“Aqui não é só sobre cabelo e barba, é sobre autoestima, personalidade e respeito.”</blockquote>
              <div><span></span><strong>Barbearia Felipe Fernandes</strong><span></span></div>
              ${icon('scissors', '', 20)}
            </footer>
          </div>
        </main>
        <div class="toast" id="toast" role="status"></div>
      </div>
    `;
  }
}
