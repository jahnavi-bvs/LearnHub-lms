// Light/dark theme. The whole palette is CSS variables; dark mode redefines them under
// <html data-theme="dark"> (see index.css), so components never need to know the theme.

const STORAGE_KEY = 'learnhub.theme';

/** The saved choice, or the operating system's preference on a first visit. */
export function getInitialTheme() {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved === 'light' || saved === 'dark') return saved;
  } catch {
    // storage blocked (e.g. private mode) - fall through
  }
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

/** Applies a theme to the page; save=true also remembers it for next time. */
export function applyTheme(theme, save = false) {
  document.documentElement.dataset.theme = theme;
  if (save) {
    try {
      localStorage.setItem(STORAGE_KEY, theme);
    } catch {
      // not saved - the theme still applies for this visit
    }
  }
}
