import { useState } from 'react';

const SIZE = 180;
const THICKNESS = 26;
const RADIUS = (SIZE - THICKNESS) / 2;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;
const GAP = 2; // space between slices, in the card's own colour

const percent = (value, total) => `${Math.round((value / total) * 100)}%`;

/**
 * A donut with a legend listing every part's name, count and share, so nothing relies on colour alone.
 * @param parts      [{ key, label, value, color }] with value > 0
 * @param totalLabel what the number in the middle counts
 */
export default function DonutChart({ parts, totalLabel }) {
  const [hover, setHover] = useState(null); // { key, x, y }
  const total = parts.reduce((sum, p) => sum + p.value, 0);
  const active = parts.find((p) => p.key === hover?.key);

  function moveTooltip(event, key) {
    const box = event.currentTarget.closest('.donut-figure').getBoundingClientRect();
    setHover({ key, x: event.clientX - box.left, y: event.clientY - box.top });
  }

  // Where each slice starts: the total length of the slices before it
  const lengths = parts.map((p) => (p.value / total) * CIRCUMFERENCE);
  const starts = lengths.map((_, i) => lengths.slice(0, i).reduce((sum, length) => sum + length, 0));

  return (
    <div className="donut">
      <div className="donut-figure">
        <svg viewBox={`0 0 ${SIZE} ${SIZE}`} role="img"
             aria-label={`${total} ${totalLabel}: ${parts.map((p) => `${p.label} ${p.value}`).join(', ')}`}>
          {/* Starts at 12 o'clock and goes clockwise */}
          <g transform={`rotate(-90 ${SIZE / 2} ${SIZE / 2})`} fill="none" strokeWidth={THICKNESS}>
            {parts.map((part, i) => {
              const drawn = parts.length > 1 ? Math.max(lengths[i] - GAP, 1) : lengths[i];
              return (
                <circle key={part.key} cx={SIZE / 2} cy={SIZE / 2} r={RADIUS} stroke={part.color}
                        strokeDasharray={`${drawn} ${CIRCUMFERENCE - drawn}`} strokeDashoffset={-starts[i]}
                        className={hover && hover.key !== part.key ? 'donut-slice donut-slice-dim' : 'donut-slice'}
                        onMouseMove={(e) => moveTooltip(e, part.key)} onMouseLeave={() => setHover(null)} />
              );
            })}
          </g>
        </svg>
        <div className="donut-total">
          <strong>{total}</strong>
          <span>{totalLabel}</span>
        </div>
        {active && (
          <div className="donut-tooltip" style={{ left: hover.x, top: hover.y }}>
            <strong>{active.label}</strong>
            <span>{active.value} · {percent(active.value, total)}</span>
          </div>
        )}
      </div>

      <ul className="donut-legend">
        {parts.map((part) => (
          <li key={part.key} className={hover?.key === part.key ? 'active' : undefined}>
            <span className="donut-swatch" style={{ background: part.color }} />
            <span className="donut-name" title={part.label}>{part.label}</span>
            <span className="donut-value">{part.value}</span>
            <span className="donut-share">{percent(part.value, total)}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
