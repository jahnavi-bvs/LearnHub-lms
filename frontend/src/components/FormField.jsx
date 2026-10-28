/**
 * Label + input + the backend's validation message for this field (if any).
 * Extra props (type, value, onChange, autoComplete...) go straight to the <input>.
 */
export default function FormField({ label, name, error, ...inputProps }) {
  const errorId = `${name}-error`;
  return (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      <input
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
