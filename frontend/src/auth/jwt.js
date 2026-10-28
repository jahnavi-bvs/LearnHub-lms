// A JWT's payload is base64url-encoded JSON - readable by anyone (the signature only stops changes).
// The browser can't verify the signature (it doesn't have the secret); the backend does that.

export function decodeJwtPayload(token) {
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
  const bytes = Uint8Array.from(atob(base64), (char) => char.charCodeAt(0));
  return JSON.parse(new TextDecoder().decode(bytes)); // TextDecoder handles non-English names
}

/** Expiry as milliseconds since 1970 (JWT "exp" is in seconds), or 0 if unreadable. */
export function tokenExpiresAt(token) {
  try {
    return decodeJwtPayload(token).exp * 1000;
  } catch {
    return 0;
  }
}
