import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './AuthPages.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER');
    const [companyCode, setCompanyCode] = useState('');
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [loading, setLoading] = useState(false);

    const { user, token, loading: authLoading } = useAuth();

    useEffect(() => {
        if (authLoading) return;
        if (!user || user.role !== 'SUPER_ADMIN') {
            setError('Acceso denegado. Se requieren permisos de Súper Administrador.');
        } else {
            setError(null);
        }
    }, [user, authLoading]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (authLoading || !user || user.role !== 'SUPER_ADMIN') return;

        setError(null);
        setSuccess(null);
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ username, email, password, role, companyCode }),
            });

            if (!response.ok) {
                const data = await response.json().catch(() => ({}));
                throw new Error(data.message || 'Error al crear usuario');
            }

            setSuccess('Usuario creado exitosamente');
            setUsername('');
            setEmail('');
            setPassword('');
            setCompanyCode('');
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (authLoading) return <div className="container">Cargando...</div>;

    if (error && (!user || user.role !== 'SUPER_ADMIN')) {
        return (
            <div className="auth-container">
                <div className="auth-card glass">
                    <h2 style={{ color: 'var(--error)' }}>Acceso Denegado</h2>
                    <p>{error}</p>
                    <a href="/" className="auth-btn" style={{ display: 'block', textDecoration: 'none' }}>Volver al Inicio</a>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-container">
            <div className="auth-card glass">
                <h2>Crear Usuario</h2>
                <p>Solo disponible para Súper Administradores</p>
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Nombre de Usuario</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            placeholder="usuario123"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Correo Electrónico</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="tu@ejemplo.com"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Contraseña</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="••••••••"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="companyCode">Código de Empresa</label>
                        <input
                            type="text"
                            id="companyCode"
                            value={companyCode}
                            onChange={(e) => setCompanyCode(e.target.value)}
                            required
                            placeholder="EMP001"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="role">Rol</label>
                        <select
                            id="role"
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            style={{
                                width: '100%',
                                padding: '0.8rem 1rem',
                                background: 'rgba(255, 255, 255, 0.05)',
                                border: '1px solid var(--border)',
                                borderRadius: '8px',
                                color: 'var(--text)',
                                cursor: 'pointer'
                            }}
                        >
                            <option value="USER">Usuario</option>
                            <option value="ADMIN">Administrador</option>
                            <option value="SUPER_ADMIN">Súper Administrador</option>
                        </select>
                    </div>
                    <button type="submit" className="auth-btn" disabled={loading}>
                        {loading ? 'Creando...' : 'Crear Usuario'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Register;
