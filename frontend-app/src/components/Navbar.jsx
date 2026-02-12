import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
    const { user, logout } = useAuth();

    const toggleMenu = () => {
        setIsOpen(!isOpen);
        if (isUserMenuOpen) setIsUserMenuOpen(false);
    };

    const toggleUserMenu = () => {
        setIsUserMenuOpen(!isUserMenuOpen);
    };

    const handleLogout = () => {
        logout();
        setIsOpen(false);
        setIsUserMenuOpen(false);
    };

    return (
        <nav className="navbar glass">
            <div className="container nav-content">
                <div className="logo">
                    <a href="/">Spring<span>React</span></a>
                </div>

                <div className="nav-actions">
                    <button className="hamburger" onClick={toggleMenu} aria-label="Toggle menu">
                        <span className={`bar ${isOpen ? 'active' : ''}`}></span>
                        <span className={`bar ${isOpen ? 'active' : ''}`}></span>
                        <span className={`bar ${isOpen ? 'active' : ''}`}></span>
                    </button>

                    <ul className={`nav-links ${isOpen ? 'open' : ''}`}>
                        <li><a href="/" onClick={() => setIsOpen(false)}>Inicio</a></li>
                        {user ? (
                            <>
                                {user.role === 'SUPER_ADMIN' && (
                                    <>
                                        <li><a href="/users-list" onClick={() => setIsOpen(false)}>Usuarios</a></li>
                                        <li><a href="/active-sessions" onClick={() => setIsOpen(false)}>Sesiones</a></li>
                                        <li><a href="/register" onClick={() => setIsOpen(false)} className="register-btn">Crear Usuario</a></li>
                                    </>
                                )}
                                <li className="user-profile">
                                    <div className="user-info" onClick={toggleUserMenu}>
                                        <div className="avatar">{user.username.charAt(0).toUpperCase()}</div>
                                        <span className="username">{user.username}</span>
                                        <span className={`arrow ${isUserMenuOpen ? 'up' : 'down'}`}>▾</span>
                                    </div>
                                    {isUserMenuOpen && (
                                        <ul className="user-dropdown glass">
                                            <li><button onClick={handleLogout}>Cerrar Sesión</button></li>
                                        </ul>
                                    )}
                                </li>
                            </>
                        ) : (
                            <li><a href="/login" onClick={() => setIsOpen(false)} className="login-link">Iniciar Sesión</a></li>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
