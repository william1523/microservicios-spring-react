import React from 'react';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import UserList from './pages/UserList';
import ActiveSessions from './pages/ActiveSessions';
import './App.css';

function App() {
  // Simple routing logic based on pathname
  const path = window.location.pathname;

  return (
    <AuthProvider>
      <Navbar />
      <main>
        {path === '/login' && <Login />}
        {path === '/register' && <Register />}
        {path === '/users-list' && <UserList />}
        {path === '/active-sessions' && <ActiveSessions />}
        {(path === '/' || path === '' || path === '/index.html') && (
          <div className="container hero">
            <h1>Bienvenido a la Plataforma</h1>
            <p>Gestiona tu cuenta de forma segura con JWT y Microservicios.</p>
          </div>
        )}
      </main>
    </AuthProvider>
  );
}

export default App;
