/**
 * Label + input + the backend's validation message for this field (if any).
 * `as` picks the element: "input" (default) or "textarea".
 * Extra props (type, value, onChange, autoComplete, rows...) go straight to that element.
 */
export default function FormField({ label, name, error, as: Element = 'input', ...inputProps }) {
  const errorId = `${name}-error`;
  return (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      <Element
        id={name}
        name={name}
        aria-invalid={error ? 'true' : undefined}
        aria-describedby={error ? errorId : undefined}
        {...inputProps}
      />
      {error && <p id={errorId} className="field-error">{error}</p>}
    </div>
  );
}
