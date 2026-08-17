import { apiRequest } from '../../../services/api/client.js';

const ADMIN_AUTH_KEY = 'barbershopAdminAuth';

function adminHeaders() {
  const credentials = sessionStorage.getItem(ADMIN_AUTH_KEY);
  return credentials ? { Authorization: `Basic ${credentials}` } : {};
}

export const appointmentApi = {
  listServices() {
    return apiRequest('/api/public/services');
  },

  listBarbers() {
    return apiRequest('/api/public/barbers');
  },

  listAvailableSlots(barberId, date, durationMinutes) {
    const query = new URLSearchParams({ date, durationMinutes });
    return apiRequest(`/api/public/barbers/${barberId}/available-slots?${query}`);
  },

  createCustomer(customer) {
    return apiRequest('/api/public/customers', {
      method: 'POST',
      body: JSON.stringify(customer),
    });
  },

  createAppointment(appointment) {
    return apiRequest('/api/public/appointments', {
      method: 'POST',
      body: JSON.stringify(appointment),
    });
  },

  getUpcomingAppointment() {
    return apiRequest('/api/public/appointments/upcoming');
  },

  listCustomerAppointments(phone) {
    const query = new URLSearchParams({ phone });
    return apiRequest(`/api/public/appointments?${query}`);
  },

  cancelCustomerAppointment(appointmentId) {
    return apiRequest(`/api/public/appointments/${appointmentId}/cancel`, {
      method: 'PATCH',
    });
  },

  listAdminAppointments() {
    return apiRequest('/api/admin/appointments', { headers: adminHeaders() });
  },

  updateAppointmentStatus(appointmentId, status) {
    return apiRequest(`/api/admin/appointments/${appointmentId}/status`, {
      method: 'PATCH',
      headers: adminHeaders(),
      body: JSON.stringify({ status }),
    });
  },

  hasAdminCredentials() {
    return Boolean(sessionStorage.getItem(ADMIN_AUTH_KEY));
  },

  setAdminCredentials(username, password) {
    sessionStorage.setItem(ADMIN_AUTH_KEY, btoa(`${username}:${password}`));
  },

  clearAdminCredentials() {
    sessionStorage.removeItem(ADMIN_AUTH_KEY);
  },
};
