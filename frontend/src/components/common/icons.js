const icons = {
  calendar: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M8 14h.01M12 14h.01M16 14h.01M8 18h.01M12 18h.01"/>',
  scissors: '<circle cx="6" cy="7" r="3"/><circle cx="6" cy="17" r="3"/><path d="m8.7 8.3 12.1 6.1M8.7 15.7 20.8 9.6"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4.5 21a7.5 7.5 0 0 1 15 0"/>',
  image: '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="8.5" cy="9" r="1.5"/><path d="m21 15-5-5L5 20"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7h.01"/>',
  phone: '<path d="M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 4.2 2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c.1 1 .4 2 .7 2.9a2 2 0 0 1-.4 2.1L8.1 10a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.4 12.8 12.8 0 0 0 2.9.7 2 2 0 0 1 1.6 1.9Z"/>',
  logout: '<path d="M10 17l5-5-5-5M15 12H3M15 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4"/>',
  pin: '<path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/>',
  instagram: '<rect x="3" y="3" width="18" height="18" rx="5"/><circle cx="12" cy="12" r="4"/><circle cx="17.5" cy="6.5" r=".5" fill="currentColor"/>',
  bell: '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/>',
  chevronDown: '<path d="m8 10 4 4 4-4"/>',
  arrowRight: '<path d="M5 12h14M14 7l5 5-5 5"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  check: '<path d="m5 12 4 4L19 6"/>',
  menu: '<path d="M4 7h16M4 12h16M4 17h16"/>',
  close: '<path d="m6 6 12 12M18 6 6 18"/>',
  whatsapp: '<path d="M20.5 11.8a8.4 8.4 0 0 1-12.4 7.4L3 20.5l1.4-4.9A8.4 8.4 0 1 1 20.5 11.8Z"/><path d="M8.2 7.5c.2-.4.4-.4.7-.4h.5c.2 0 .4.1.5.5l.7 1.7c.1.3 0 .5-.2.7l-.5.6c-.2.2-.2.4 0 .7.7 1.2 1.7 2.1 2.9 2.7.3.2.5.1.7-.1l.8-1c.2-.2.4-.3.7-.2l1.8.8c.3.1.5.3.5.5 0 .3-.2 1.5-1.1 2.1-.6.5-1.5.7-2.5.4-1-.3-2.4-.8-4.1-2.3-1.4-1.2-2.4-2.8-2.7-3.8-.4-1.1 0-2.2.4-2.9Z"/>',
  razor: '<path d="M5 4h12l2 3-9 4-5-2Z"/><path d="m10 11 5 9h-3l-5-8"/>',
  waves: '<path d="M4 9c5-4 11-4 16 0M6 14c4-3 8-3 12 0M8 18c3-2 5-2 8 0"/>',
};

export function icon(name, className = '', size = 24) {
  const content = icons[name] ?? icons.info;
  return `<svg class="icon ${className}" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${content}</svg>`;
}
