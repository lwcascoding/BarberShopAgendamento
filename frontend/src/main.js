import './styles/tokens.css';
import './styles/reset.css';
import './styles/main.css';
import './styles/responsive.css';

import { BarbershopApp } from './app/BarbershopApp.js';

const appRoot = document.querySelector('#app');
const application = new BarbershopApp(appRoot);

application.mount();
