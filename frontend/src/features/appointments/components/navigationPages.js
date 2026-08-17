import { icon } from '../../../components/common/icons.js';
import { escapeHtml } from '../../../utils/html.js';
import { formatCurrency } from '../data/services.js';
import { appointmentApi } from '../services/appointmentApi.js';

const statusLabels = {
  SCHEDULED: 'Agendado',
  COMPLETED: 'Concluído',
  CANCELED: 'Cancelado',
};

export class NavigationPages {
  constructor(root, notify, navigate) {
    this.root = root;
    this.notify = notify;
    this.navigate = navigate;
    this.route = null;
    this.services = [];
    this.barbers = [];
    this.appointments = [];
    this.phone = '';
    this.searchedAppointments = false;
    this.loading = false;
  }

  mount() {
    this.root.addEventListener('click', (event) => this.handleClick(event));
    this.root.addEventListener('submit', (event) => this.handleSubmit(event));
  }

  async show(route) {
    this.route = route;
    this.loading = route === 'services' || route === 'professionals';
    this.render();

    try {
      if (route === 'services') this.services = await appointmentApi.listServices();
      if (route === 'professionals') this.barbers = await appointmentApi.listBarbers();
    } catch (error) {
      this.notify(error.message);
    } finally {
      this.loading = false;
      if (this.route === route) this.render();
    }
  }

  pageHeader(iconName, eyebrow, title, description) {
    return `
      <div class="page-view-header">
        <span>${icon(iconName, '', 27)}</span>
        <div><p class="eyebrow">${eyebrow}</p><h2>${title}</h2><p>${description}</p></div>
      </div>`;
  }

  renderServices() {
    const content = this.loading
      ? '<div class="page-empty">Carregando serviços...</div>'
      : this.services.map((service) => `
          <article class="catalog-card">
            <span class="catalog-icon">${icon(service.code === 'NECKLINE' ? 'razor' : 'scissors', '', 28)}</span>
            <div><h3>${escapeHtml(service.name)}</h3><p>${service.durationMinutes} minutos</p></div>
            <strong>${formatCurrency(Number(service.price))}</strong>
            <button type="button" data-page-action="book-service" data-service-id="${service.code}">Agendar</button>
          </article>`).join('');

    return `${this.pageHeader('scissors', 'Catálogo', 'Nossos Serviços', 'Escolha o cuidado ideal e siga diretamente para o agendamento.')}
      <div class="catalog-grid">${content || '<div class="page-empty">Nenhum serviço disponível.</div>'}</div>`;
  }

  renderProfessionals() {
    const content = this.loading
      ? '<div class="page-empty">Carregando profissionais...</div>'
      : this.barbers.map((barber) => {
          const initials = barber.name.split(/\s+/).slice(0, 2).map((part) => part[0]).join('').toUpperCase();
          return `
            <article class="professional-profile-card">
              <span class="profile-avatar">${escapeHtml(initials)}</span>
              <div><p>Barbeiro profissional</p><h3>${escapeHtml(barber.name)}</h3><a href="tel:${escapeHtml(barber.phone)}">${icon('phone', '', 16)} ${escapeHtml(barber.phone)}</a></div>
              <span class="availability-status">Disponível para agendamento</span>
              <button type="button" data-page-action="book-professional" data-barber-id="${barber.id}">Ver horários</button>
            </article>`;
        }).join('');

    return `${this.pageHeader('user', 'Equipe', 'Profissionais', 'Conheça quem cuida do seu estilo na Barbearia Felipe Fernandes.')}
      <div class="professional-profile-grid">${content || '<div class="page-empty">Nenhum profissional disponível.</div>'}</div>`;
  }

  renderContact() {
    return `${this.pageHeader('phone', 'Atendimento', 'Entre em Contato', 'Fale conosco ou encontre a barbearia pelos canais abaixo.')}
      <div class="contact-view-grid">
        <article><span>${icon('whatsapp', '', 30)}</span><h3>WhatsApp</h3><p>Agendamentos, dúvidas e cancelamentos.</p><a href="https://wa.me/5524981409877" target="_blank" rel="noreferrer">Conversar agora</a></article>
        <article><span>${icon('phone', '', 30)}</span><h3>Telefone</h3><p>Atendimento pelo número da barbearia.</p><a href="tel:+5524981409877">24 98140-9877</a></article>
        <article><span>${icon('instagram', '', 30)}</span><h3>Instagram</h3><p>Acompanhe novidades e trabalhos.</p><a href="https://www.instagram.com/barbeariaffernandes/#" target="_blank" rel="noreferrer">@barbeariaffernandes</a></article>
        <article class="address-card"><span>${icon('pin', '', 30)}</span><h3>Localização</h3><p>(Dourados Shopping)<br>Av. Sávio Cota de Almeida<br>Gama-2214 / Retiro, Volta Redonda 27281422</p><a href="https://www.google.com/maps/search/?api=1&query=Dourados+Shopping+Av.+Savio+Cota+de+Almeida+Volta+Redonda" target="_blank" rel="noreferrer">Abrir no mapa</a></article>
      </div>`;
  }

  renderProfile() {
    return `${this.pageHeader('user', 'Identidade', 'Perfil da Barbearia', 'Conheça o profissional e acesse os canais públicos de atendimento.')}
      <div class="profile-view">
        <span class="profile-avatar large">FF</span>
        <div><p class="eyebrow">Barbeiro responsável</p><h3>Felipe Fernandes</h3><p>Estilo, cuidado e atendimento personalizado em Volta Redonda.</p></div>
        <div class="profile-session"><span>Atendimento</span><strong class="is-online">Agenda aberta</strong></div>
        <div class="profile-actions">
          <button class="primary-button" type="button" data-page-action="go-booking">Novo agendamento</button>
          <button class="secondary-button" type="button" data-page-action="go-contact">Entrar em contato</button>
        </div>
      </div>`;
  }

  formatDate(value) {
    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'long', timeStyle: 'short',
    }).format(new Date(value));
  }

  renderCustomerAppointment(appointment) {
    const canCancel = appointment.status === 'SCHEDULED' && new Date(appointment.startTime) > new Date();
    return `
      <article class="customer-appointment-card">
        <div class="customer-appointment-date">${icon('calendar', '', 22)}<div><small>Data e horário</small><strong>${this.formatDate(appointment.startTime)}</strong></div></div>
        <div><small>Serviço</small><strong>${escapeHtml(appointment.serviceName)}</strong><span>${appointment.durationMinutes} min · ${formatCurrency(Number(appointment.servicePrice))}</span></div>
        <div><small>Profissional</small><strong>${escapeHtml(appointment.barberName)}</strong><span class="status-badge status-${appointment.status.toLowerCase()}">${statusLabels[appointment.status]}</span></div>
        ${canCancel ? `<button class="cancel-customer-appointment" type="button" data-page-action="cancel-appointment" data-id="${appointment.id}">Cancelar agendamento</button>` : ''}
      </article>`;
  }

  renderMyAppointments() {
    let result = '<div class="page-empty compact">Digite o mesmo WhatsApp informado no agendamento.</div>';
    if (this.loading) result = '<div class="page-empty compact">Consultando sua agenda...</div>';
    if (this.searchedAppointments && !this.loading) {
      result = this.appointments.length
        ? this.appointments.map((appointment) => this.renderCustomerAppointment(appointment)).join('')
        : '<div class="page-empty compact">Nenhum agendamento encontrado para esse WhatsApp.</div>';
    }

    return `${this.pageHeader('calendar', 'Sua agenda', 'Meus Agendamentos', 'Consulte seus horários e cancele um atendimento futuro quando necessário.')}
      <form class="appointment-search" data-appointment-search>
        <label><span>WhatsApp usado no agendamento</span><input type="tel" name="phone" value="${escapeHtml(this.phone)}" maxlength="20" placeholder="(24) 99999-9999" required></label>
        <button class="primary-button" type="submit">Consultar ${icon('arrowRight', '', 18)}</button>
      </form>
      <div class="customer-appointments-list">${result}</div>`;
  }

  render() {
    const templates = {
      'my-appointments': () => this.renderMyAppointments(),
      services: () => this.renderServices(),
      professionals: () => this.renderProfessionals(),
      contact: () => this.renderContact(),
      profile: () => this.renderProfile(),
    };
    const template = templates[this.route];
    if (template) this.root.innerHTML = `<div class="page-view">${template()}</div>`;
  }

  async searchAppointments(phone) {
    this.phone = phone.trim();
    this.loading = true;
    this.searchedAppointments = true;
    this.render();
    try {
      this.appointments = await appointmentApi.listCustomerAppointments(this.phone);
    } catch (error) {
      this.appointments = [];
      this.notify(error.message);
    } finally {
      this.loading = false;
      if (this.route === 'my-appointments') this.render();
    }
  }

  async handleSubmit(event) {
    if (!event.target.matches('[data-appointment-search]')) return;
    event.preventDefault();
    const formData = new FormData(event.target);
    await this.searchAppointments(formData.get('phone'));
  }

  async handleClick(event) {
    const button = event.target.closest('[data-page-action]');
    if (!button) return;

    const action = button.dataset.pageAction;
    if (action === 'book-service') this.navigate('booking', { serviceId: button.dataset.serviceId });
    if (action === 'book-professional') this.navigate('booking', { barberId: button.dataset.barberId });
    if (action === 'go-booking') this.navigate('booking');
    if (action === 'go-contact') this.navigate('contact');
    if (action === 'cancel-appointment') {
      const confirmed = window.confirm('Deseja realmente cancelar este agendamento?');
      if (!confirmed) return;
      button.disabled = true;
      try {
        await appointmentApi.cancelCustomerAppointment(button.dataset.id);
        this.notify('Agendamento cancelado com sucesso.');
        window.dispatchEvent(new CustomEvent('appointment:updated'));
        await this.searchAppointments(this.phone);
      } catch (error) {
        this.notify(error.message);
        button.disabled = false;
      }
    }
  }
}
