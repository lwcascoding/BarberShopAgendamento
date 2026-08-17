import { icon } from '../../../components/common/icons.js';
import { escapeHtml } from '../../../utils/html.js';
import { appointmentApi } from '../services/appointmentApi.js';
import { formatCurrency } from '../data/services.js';

const statusLabels = {
  SCHEDULED: 'Agendado',
  COMPLETED: 'Concluído',
  CANCELED: 'Cancelado',
};

export class AdminDashboard {
  constructor(root, notify) {
    this.root = root;
    this.notify = notify;
    this.appointments = [];
    this.filter = 'ALL';
    this.loading = false;
    this.authenticated = appointmentApi.hasAdminCredentials();
  }

  mount() {
    this.root.addEventListener('click', (event) => this.handleClick(event));
    this.root.addEventListener('change', (event) => {
      if (event.target.name === 'statusFilter') {
        this.filter = event.target.value;
        this.render();
      }
    });
    this.root.addEventListener('submit', (event) => this.handleLogin(event));
  }

  async show() {
    this.authenticated = appointmentApi.hasAdminCredentials();
    if (!this.authenticated) {
      this.render();
      return;
    }
    this.loading = true;
    this.render();
    await this.loadAppointments();
  }

  async loadAppointments() {
    try {
      this.appointments = await appointmentApi.listAdminAppointments();
    } catch (error) {
      if (error.message.includes('inválidos')) {
        appointmentApi.clearAdminCredentials();
        this.authenticated = false;
      }
      this.notify(error.message);
    } finally {
      this.loading = false;
      this.render();
    }
  }

  formatDate(value) {
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
    }).format(new Date(value));
  }

  renderAppointment(appointment) {
    const actions = appointment.status === 'SCHEDULED'
      ? `<div class="admin-row-actions"><button type="button" data-admin-action="complete" data-id="${appointment.id}">${icon('check', '', 16)} Concluir</button><button class="danger" type="button" data-admin-action="cancel" data-id="${appointment.id}">Cancelar</button></div>`
      : '';

    return `
      <article class="admin-appointment-row">
        <div class="admin-date"><span>${icon('calendar', '', 20)}</span><strong>${this.formatDate(appointment.startTime)}</strong><small>#${appointment.id}</small></div>
        <div><small>Cliente</small><strong>${escapeHtml(appointment.customerName)}</strong><a href="tel:${escapeHtml(appointment.customerPhone)}">${escapeHtml(appointment.customerPhone)}</a></div>
        <div><small>Serviço</small><strong>${escapeHtml(appointment.serviceName)}</strong><span>${appointment.durationMinutes} min · ${formatCurrency(Number(appointment.servicePrice))}</span></div>
        <div><small>Profissional</small><strong>${escapeHtml(appointment.barberName)}</strong><span class="status-badge status-${appointment.status.toLowerCase()}">${statusLabels[appointment.status]}</span></div>
        ${actions}
      </article>`;
  }

  render() {
    if (!this.authenticated) {
      this.root.innerHTML = `
        <div class="admin-login">
          <span class="admin-login-icon">${icon('user', '', 34)}</span>
          <p class="eyebrow">Acesso restrito</p><h2>Área Administrativa</h2>
          <p>Entre com suas credenciais para acompanhar a agenda.</p>
          <form data-admin-login><label><span>Usuário</span><input name="username" autocomplete="username" required></label><label><span>Senha</span><input name="password" type="password" autocomplete="current-password" required></label><button class="primary-button" type="submit">Entrar ${icon('arrowRight', '', 18)}</button></form>
        </div>`;
      return;
    }

    const counts = this.appointments.reduce((summary, appointment) => {
      summary[appointment.status] += 1;
      return summary;
    }, { SCHEDULED: 0, COMPLETED: 0, CANCELED: 0 });

    const visibleAppointments = this.filter === 'ALL'
      ? this.appointments
      : this.appointments.filter(({ status }) => status === this.filter);

    this.root.innerHTML = `
      <div class="admin-dashboard">
        <div class="admin-header">
          <div><p class="eyebrow">Área administrativa</p><h2>Todos os Agendamentos</h2><p>Acompanhe clientes, serviços, horários e situação de cada atendimento.</p></div>
          <div class="admin-header-actions"><button class="admin-refresh" type="button" data-admin-action="refresh">${icon('clock', '', 18)} Atualizar</button><button class="admin-logout" type="button" data-admin-action="logout">Sair</button></div>
        </div>
        <div class="admin-stats">
          <div><span>Agendados</span><strong>${counts.SCHEDULED}</strong></div>
          <div><span>Concluídos</span><strong>${counts.COMPLETED}</strong></div>
          <div><span>Cancelados</span><strong>${counts.CANCELED}</strong></div>
          <div><span>Total</span><strong>${this.appointments.length}</strong></div>
        </div>
        <div class="admin-toolbar"><strong>Agenda</strong><label>Filtrar por <select name="statusFilter"><option value="ALL" ${this.filter === 'ALL' ? 'selected' : ''}>Todos</option><option value="SCHEDULED" ${this.filter === 'SCHEDULED' ? 'selected' : ''}>Agendados</option><option value="COMPLETED" ${this.filter === 'COMPLETED' ? 'selected' : ''}>Concluídos</option><option value="CANCELED" ${this.filter === 'CANCELED' ? 'selected' : ''}>Cancelados</option></select></label></div>
        <div class="admin-list">
          ${this.loading ? '<div class="admin-empty">Carregando agenda...</div>' : visibleAppointments.length ? visibleAppointments.map((appointment) => this.renderAppointment(appointment)).join('') : '<div class="admin-empty">Nenhum agendamento encontrado.</div>'}
        </div>
      </div>`;
  }

  async handleClick(event) {
    const actionButton = event.target.closest('[data-admin-action]');
    if (!actionButton) return;

    const action = actionButton.dataset.adminAction;
    if (action === 'logout') {
      appointmentApi.clearAdminCredentials();
      this.authenticated = false;
      this.appointments = [];
      this.render();
      return;
    }
    if (action === 'refresh') {
      await this.show();
      return;
    }

    const status = action === 'complete' ? 'COMPLETED' : 'CANCELED';
    actionButton.disabled = true;
    try {
      await appointmentApi.updateAppointmentStatus(actionButton.dataset.id, status);
      this.notify(status === 'COMPLETED' ? 'Atendimento marcado como concluído.' : 'Agendamento cancelado.');
      window.dispatchEvent(new CustomEvent('appointment:updated'));
      await this.loadAppointments();
    } catch (error) {
      this.notify(error.message);
      actionButton.disabled = false;
    }
  }

  async handleLogin(event) {
    if (!event.target.matches('[data-admin-login]')) return;

    event.preventDefault();
    const formData = new FormData(event.target);
    appointmentApi.setAdminCredentials(formData.get('username'), formData.get('password'));
    this.authenticated = true;
    await this.show();
  }
}
