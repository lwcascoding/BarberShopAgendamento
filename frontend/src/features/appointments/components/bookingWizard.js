import { icon } from '../../../components/common/icons.js';
import { escapeHtml } from '../../../utils/html.js';
import { appointmentApi } from '../services/appointmentApi.js';
import { fallbackBarbers, fallbackTimes, formatCurrency, services as fallbackServices } from '../data/services.js';

const stepLabels = ['Serviço', 'Profissional', 'Data', 'Horário'];
const serviceIcons = {
  HAIRCUT: 'scissors', BEARD: 'waves', HAIRCUT_AND_BEARD: 'scissors', EYEBROW: 'waves', NECKLINE: 'razor',
};

function initialState(defaultServiceId) {
  return {
    step: 1, serviceId: defaultServiceId, barberId: null, date: null, time: null,
    customerName: '', customerPhone: '', appointment: null, submitting: false, complete: false,
  };
}

export class BookingWizard {
  constructor(root, notify) {
    this.root = root;
    this.notify = notify;
    this.services = fallbackServices;
    this.barbers = fallbackBarbers;
    this.times = fallbackTimes;
    this.state = initialState(this.services[0].id);
  }

  async mount() {
    this.root.addEventListener('click', (event) => this.handleClick(event));
    this.root.addEventListener('input', (event) => this.handleInput(event));
    this.render();

    const [servicesResult, barbersResult] = await Promise.allSettled([
      appointmentApi.listServices(), appointmentApi.listBarbers(),
    ]);

    if (servicesResult.status === 'fulfilled' && servicesResult.value.length) {
      this.services = servicesResult.value.map((service) => ({
        id: service.code,
        name: service.name,
        duration: service.durationMinutes,
        price: Number(service.price),
        icon: serviceIcons[service.code] ?? 'scissors',
      }));
      this.state.serviceId = this.services[0].id;
    }

    if (barbersResult.status === 'fulfilled' && barbersResult.value.length) {
      this.barbers = barbersResult.value;
    } else if (barbersResult.status === 'rejected') {
      this.notify('Não foi possível carregar os profissionais. Verifique o backend.');
    }

    if (!window.location.hash || window.location.hash === '#agendamento') this.render();
  }

  show({ serviceId, barberId } = {}) {
    if (this.state.complete) this.state = initialState(this.services[0].id);
    if (serviceId && this.services.some(({ id }) => id === serviceId)) {
      this.state.serviceId = serviceId;
      this.state.step = 1;
      this.state.time = null;
    }
    if (barberId && this.barbers.some(({ id }) => String(id) === String(barberId))) {
      this.state.barberId = barberId;
      this.state.step = 1;
      this.state.date = null;
      this.state.time = null;
    }
    this.render();
  }

  get selectedService() {
    return this.services.find(({ id }) => id === this.state.serviceId);
  }

  get selectedBarber() {
    return this.barbers.find(({ id }) => String(id) === String(this.state.barberId));
  }

  get selectedDate() {
    return this.getDates().find(({ value }) => value === this.state.date);
  }

  getDates() {
    return Array.from({ length: 7 }, (_, index) => {
      const date = new Date();
      date.setDate(date.getDate() + index);
      return {
        value: date.toISOString().slice(0, 10),
        weekday: new Intl.DateTimeFormat('pt-BR', { weekday: 'short' }).format(date).replace('.', ''),
        day: new Intl.DateTimeFormat('pt-BR', { day: '2-digit' }).format(date),
        month: new Intl.DateTimeFormat('pt-BR', { month: 'short' }).format(date).replace('.', ''),
        full: new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long' }).format(date),
      };
    });
  }

  renderSteps() {
    return stepLabels.map((label, index) => {
      const number = index + 1;
      const stateClass = number === this.state.step ? ' is-current' : number < this.state.step ? ' is-done' : '';
      const marker = number < this.state.step ? icon('check', '', 15) : number;
      return `<div class="wizard-step${stateClass}" data-step="${number}"><span>${marker}</span>${label}</div>`;
    }).join('');
  }

  renderServiceStep() {
    const rows = this.services.map((service) => {
      const selected = service.id === this.state.serviceId;
      return `
        <button class="service-option${selected ? ' is-selected' : ''}" type="button" data-service="${service.id}" aria-pressed="${selected}">
          <span class="service-icon">${icon(service.icon, '', 27)}</span>
          <span class="service-details"><strong>${service.name}</strong><small>${service.duration} minutos</small></span>
          <span class="service-price">${formatCurrency(service.price)}</span>
          <span class="selection-dot">${selected ? icon('check', '', 18) : ''}</span>
        </button>`;
    }).join('');

    return `
      <div class="choice-layout">
        <div class="choice-column"><h3>Escolha o serviço</h3><div class="service-list">${rows}</div></div>
        <aside class="art-card"><div class="chair-mark">${icon('scissors', '', 72)}</div><div><h3>Seu estilo,<br>nossa arte.</h3><span class="gold-rule"></span><p>Agende seu horário<br>e viva a experiência<br>Barbearia Felipe Fernandes.</p></div></aside>
      </div>`;
  }

  renderBarberStep() {
    return `
      <div class="choice-column"><h3>Escolha o profissional</h3><div class="professional-grid">
        ${this.barbers.map((barber) => {
          const selected = String(barber.id) === String(this.state.barberId);
          return `<button class="professional-card${selected ? ' is-selected' : ''}" type="button" data-barber="${barber.id}" aria-pressed="${selected}"><span class="professional-avatar">FF</span><span><strong>${escapeHtml(barber.name)}</strong><small>Barbeiro profissional</small></span><span class="selection-dot">${selected ? icon('check', '', 18) : ''}</span></button>`;
        }).join('')}
      </div></div>`;
  }

  renderDateStep() {
    return `
      <div class="choice-column"><h3>Escolha a data</h3><div class="date-grid">
        ${this.getDates().map((date) => {
          const selected = date.value === this.state.date;
          return `<button class="date-card${selected ? ' is-selected' : ''}" type="button" data-date="${date.value}" aria-pressed="${selected}"><small>${date.weekday}</small><strong>${date.day}</strong><span>${date.month}</span></button>`;
        }).join('')}
      </div></div>`;
  }

  renderTimeStep() {
    const timeOptions = this.times.length
      ? this.times.map((time) => {
          const selected = time === this.state.time;
          return `<button class="time-card${selected ? ' is-selected' : ''}" type="button" data-time="${time}" aria-pressed="${selected}">${icon('clock', '', 18)}${time}</button>`;
        }).join('')
      : '<p class="empty-slots">Não há horários disponíveis para esta data e serviço.</p>';

    return `
      <div class="choice-column">
        <h3>Escolha o horário</h3><p class="choice-support">${this.selectedDate?.full ?? ''}</p>
        <div class="time-grid">${timeOptions}</div>
        <div class="customer-form">
          <div class="form-heading"><h3>Seus dados</h3><p>Usaremos estas informações para identificar o agendamento.</p></div>
          <label><span>Nome completo</span><input type="text" name="customerName" value="${escapeHtml(this.state.customerName)}" autocomplete="name" maxlength="100" placeholder="Digite seu nome" required></label>
          <label><span>WhatsApp</span><input type="tel" name="customerPhone" value="${escapeHtml(this.state.customerPhone)}" autocomplete="tel" maxlength="20" placeholder="(24) 99999-9999" required></label>
        </div>
      </div>`;
  }

  renderConfirmation() {
    return `
      <div class="booking-success"><span class="success-icon">${icon('check', '', 40)}</span>
        <p class="eyebrow">Agendamento #${this.state.appointment.id} concluído</p><h3>Seu horário está reservado.</h3>
        <p>${escapeHtml(this.selectedService.name)} com ${escapeHtml(this.selectedBarber.name)}<br>${this.selectedDate.full}, às ${this.state.time}.</p>
        <button class="secondary-button" type="button" data-action="restart">Fazer novo agendamento</button>
      </div>`;
  }

  canAdvance() {
    if (this.state.step === 4) {
      const phoneDigits = this.state.customerPhone.replace(/\D/g, '');
      return Boolean(this.state.time && this.state.customerName.trim().length >= 2 && phoneDigits.length >= 10);
    }
    return Boolean([this.state.serviceId, this.state.barberId, this.state.date][this.state.step - 1]);
  }

  render() {
    if (this.state.complete) {
      this.root.innerHTML = this.renderConfirmation();
      return;
    }

    const stepContent = [this.renderServiceStep(), this.renderBarberStep(), this.renderDateStep(), this.renderTimeStep()][this.state.step - 1];
    const buttonText = this.state.submitting ? 'Agendando...' : this.state.step === 4 ? 'Confirmar' : 'Próximo';

    this.root.innerHTML = `
      <div class="wizard-heading"><span class="heading-icon">${icon('calendar', '', 25)}</span><div><h2>Novo Agendamento</h2><p>Escolha o serviço, profissional, data e horário.</p></div></div>
      <div class="wizard-progress">${this.renderSteps()}</div><div class="wizard-content">${stepContent}</div>
      <div class="wizard-actions">${this.state.step > 1 ? '<button class="back-button" type="button" data-action="back">Voltar</button>' : '<span></span>'}<button class="primary-button" type="button" data-action="next" ${this.canAdvance() && !this.state.submitting ? '' : 'disabled'}>${buttonText} ${icon(this.state.step === 4 ? 'check' : 'arrowRight', '', 22)}</button></div>`;
  }

  async loadTimes() {
    if (!this.state.barberId || Number.isNaN(Number(this.state.barberId))) {
      this.times = [];
      return;
    }
    try {
      const slots = await appointmentApi.listAvailableSlots(this.state.barberId, this.state.date, this.selectedService.duration);
      this.times = slots.map(({ startTime }) => startTime.slice(11, 16));
    } catch (error) {
      this.times = [];
      this.notify(error.message);
    }
  }

  handleInput(event) {
    if (event.target.name === 'customerName') this.state.customerName = event.target.value;
    if (event.target.name === 'customerPhone') this.state.customerPhone = event.target.value;
    const nextButton = this.root.querySelector('[data-action="next"]');
    if (nextButton) nextButton.disabled = !this.canAdvance();
  }

  async submitAppointment() {
    this.state.submitting = true;
    this.render();
    try {
      const customer = await appointmentApi.createCustomer({ name: this.state.customerName.trim(), phone: this.state.customerPhone.trim() });
      this.state.appointment = await appointmentApi.createAppointment({
        barberId: Number(this.state.barberId), customerId: customer.id, serviceCode: this.state.serviceId,
        startTime: `${this.state.date}T${this.state.time}:00`,
      });
      this.state.complete = true;
      this.notify('Agendamento confirmado e salvo com sucesso!');
      window.dispatchEvent(new CustomEvent('appointment:created', { detail: this.state.appointment }));
    } catch (error) {
      this.notify(error.message);
    } finally {
      this.state.submitting = false;
    }
  }

  async handleClick(event) {
    const serviceButton = event.target.closest('[data-service]');
    const barberButton = event.target.closest('[data-barber]');
    const dateButton = event.target.closest('[data-date]');
    const timeButton = event.target.closest('[data-time]');
    const actionButton = event.target.closest('[data-action]');

    const isBookingInteraction = serviceButton
      || barberButton
      || dateButton
      || timeButton
      || actionButton;

    if (!isBookingInteraction) return;

    if (serviceButton) { this.state.serviceId = serviceButton.dataset.service; this.state.time = null; }
    if (barberButton) { this.state.barberId = barberButton.dataset.barber; this.state.date = null; this.state.time = null; }
    if (dateButton) { this.state.date = dateButton.dataset.date; this.state.time = null; }
    if (timeButton) this.state.time = timeButton.dataset.time;
    if (actionButton?.dataset.action === 'back') this.state.step -= 1;

    if (actionButton?.dataset.action === 'next' && this.canAdvance()) {
      if (this.state.step === 3) await this.loadTimes();
      if (this.state.step === 4) await this.submitAppointment();
      else this.state.step += 1;
    }

    if (actionButton?.dataset.action === 'restart') {
      this.state = initialState(this.services[0].id);
      this.times = [];
    }
    this.render();
  }
}
