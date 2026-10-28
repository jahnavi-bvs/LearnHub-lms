import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import App from './App.jsx';
import { AuthProvider } from './auth/AuthProvider';

// Entry point: mounts <App /> into <div id="root"> in index.html.
// BrowserRouter lets the app read and change the URL without reloading the page.
// AuthProvider makes the login state available to every component via useAuth().
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
);
