import { useCallback, useEffect, useState } from 'react';

/**
 * Runs an async function (usually an API call) and tracks its result:
 *   const { data, loading, error, reload } = useAsync(() => getCourse(id), [id]);
 *
 * - Re-runs whenever a value in `deps` changes, or when reload() is called.
 * - Ignores responses that arrive after the inputs changed or the page was left.
 * - `error` is the thrown error (an ApiError for failed requests), so pages can check error.status.
 */
export function useAsync(asyncFunction, deps) {
  const [state, setState] = useState({ data: null, loading: true, error: null });
  const [reloadCount, setReloadCount] = useState(0);

  useEffect(() => {
    let ignore = false;
    setState((previous) => ({ ...previous, loading: true, error: null }));

    asyncFunction()
      .then((data) => { if (!ignore) setState({ data, loading: false, error: null }); })
      .catch((error) => { if (!ignore) setState({ data: null, loading: false, error }); });

    return () => { ignore = true; };
    // The caller decides what the function depends on, exactly like useEffect's own deps
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, reloadCount]);

  const reload = useCallback(() => setReloadCount((count) => count + 1), []);

  return { ...state, reload };
}
