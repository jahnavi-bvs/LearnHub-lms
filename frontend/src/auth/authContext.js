import { createContext } from 'react';

// The context object itself, shared by AuthProvider (writes it) and useAuth (reads it).
// Kept in its own file so AuthProvider.jsx exports only a component (needed for fast refresh).
export const AuthContext = createContext(null);
