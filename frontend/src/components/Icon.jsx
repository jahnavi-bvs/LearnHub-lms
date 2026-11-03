// Small stroke icons drawn with SVG paths. They use currentColor, so they take the text colour
// of wherever they're placed (and switch with the theme automatically).

const PATHS = {
  home: 'M3 10.5 12 3l9 7.5V21h-6v-6H9v6H3z',
  book: 'M4 4h11a3 3 0 0 1 3 3v13H7a3 3 0 0 1-3-3z M4 17a3 3 0 0 1 3-3h11',
  layers: 'M12 3 3 8l9 5 9-5z M3 13l9 5 9-5',
  quiz: 'M9 11l3 3 8-8 M20 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11',
  chart: 'M4 20V10 M10 20V4 M16 20v-7 M2 20h20',
  users: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M22 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75',
  plus: 'M12 5v14 M5 12h14',
  user: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8',
  sun: 'M12 17a5 5 0 1 0 0-10 5 5 0 0 0 0 10 M12 1v2 M12 21v2 M4.2 4.2l1.4 1.4 M18.4 18.4l1.4 1.4 M1 12h2 M21 12h2 M4.2 19.8l1.4-1.4 M18.4 5.6l1.4-1.4',
  moon: 'M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z',
  logout: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4 M16 17l5-5-5-5 M21 12H9',
  search: 'M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16 M21 21l-4.35-4.35',
  chevron: 'M9 18l6-6-6-6',
  arrow: 'M5 12h14 M13 6l6 6-6 6',
  check: 'M20 6L9 17l-5-5',
  // Course cover icons
  cup: 'M17 8h1a4 4 0 0 1 0 8h-1 M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4z M6 1v3 M10 1v3 M14 1v3',
  terminal: 'M4 17l6-6-6-6 M12 19h8',
  globe: 'M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20 M2 12h20 M12 2a15 15 0 0 1 0 20 M12 2a15 15 0 0 0 0 20',
  database: 'M12 8c5 0 9-1.3 9-3s-4-3-9-3-9 1.3-9 3 4 3 9 3 M21 12c0 1.7-4 3-9 3s-9-1.3-9-3 M3 5v14c0 1.7 4 3 9 3s9-1.3 9-3V5',
  chip: 'M6 6h12v12H6z M10 10h4v4h-4z M9 2v4 M15 2v4 M9 18v4 M15 18v4 M2 9h4 M2 15h4 M18 9h4 M18 15h4',
  shield: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z M9 12l2 2 4-4',
  cloud: 'M18 10h-1.3A8 8 0 1 0 9 20h9a5 5 0 0 0 0-10z',
  briefcase: 'M4 7h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2z M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16',
  calculator: 'M6 2h12a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z M8 6h8v4H8z M8 14h.01 M12 14h.01 M16 14h.01 M8 18h.01 M12 18h.01 M16 18h.01',
  trend: 'M23 6l-9.5 9.5-5-5L1 18 M17 6h6v6',
  heart: 'M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8l1 1.1L12 21.3l7.8-7.8 1-1.1a5.5 5.5 0 0 0 0-7.8z',
  dna: 'M7 2c0 7 10 13 10 20 M17 2c0 7-10 13-10 20 M8 6h8 M8 18h8 M10 12h4',
  atom: 'M12 13a1 1 0 1 0 0-2 1 1 0 0 0 0 2 M2 12a10 4 0 1 0 20 0 10 4 0 1 0-20 0 M4.9 4.9a10 4 45 1 0 14.2 14.2 10 4 45 1 0-14.2-14.2 M19.1 4.9a10 4 -45 1 0-14.2 14.2 10 4 -45 1 0 14.2-14.2',
  sigma: 'M18 4H6l6 8-6 8h12',
  palette: 'M12 22a10 10 0 1 1 0-20c5.5 0 10 4 10 9a5 5 0 0 1-5 5h-2a2 2 0 0 0-1.5 3.3c.4.5.5 1 .1 1.6-.4.6-1 1.1-1.6 1.1z M7.5 11h.01 M10.5 7h.01 M15.5 7h.01 M18 11h.01',
};

export default function Icon({ name, size = 20 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor"
         strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d={PATHS[name]} />
    </svg>
  );
}
