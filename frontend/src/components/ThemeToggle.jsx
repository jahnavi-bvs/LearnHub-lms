import { useState } from 'react';
import { applyTheme } from '../theme';
import Icon from './Icon';

/** Sun/moon button. The current theme is read from <html data-theme>, set at startup in main.jsx. */
export default function ThemeToggle() {
  const [theme, setTheme] = useState(() => document.documentElement.dataset.theme || 'light');
  const next = theme === 'dark' ? 'light' : 'dark';

  function toggle() {
    applyTheme(next, true);
    setTheme(next);
  }

  return (
    <button type="button" className="icon-button" onClick={toggle} aria-label={`Switch to ${next} theme`}
            title={`Switch to ${next} theme`}>
      <Icon name={theme === 'dark' ? 'sun' : 'moon'} />
    </button>
  );
}
