import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './ManagementPages.css';

const ActiveSessions = () => {
    const [sessions, setSessions] = useState({});
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const { user, token, loading: authLoading } = useAuth();

    useEffect(() => {
        const fetchSessions = async () => {
            if (authLoading) return;

            setError(null);

            if (!user || user.role !== 'SUPER_ADMIN') {
                setError('Acceso denegado. Se requieren permisos de Súper Administrador.');
                setLoading(false);
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/sessions', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Error al obtener sesiones');
                }

                const data = await response.json();
                setSessions(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchSessions();
    }, [user, token, authLoading]);

    if (authLoading || loading) return <div className="container">Cargando...</div>;
    if (error) return (
        <div className="container management-container">
            <div className="auth-card glass" style={{ margin: '0 auto' }}>
                <h2 style={{ color: 'var(--error)' }}>Acceso Denegado</h2>
                <p>{error}</p>
                <a href="/" className="auth-btn" style={{ display: 'block', textDecoration: 'none' }}>Volver al Inicio</a>
            </div>
        </div>
    );

    const sessionEntries = Object.entries(sessions);

    return (
        <div className="container management-container">
            <h2>Sesiones Activas (Redis)</h2>
            <div className="table-wrapper glass">
                <table className="management-table">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Token (JWT)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {sessionEntries.length > 0 ? (
                            sessionEntries.map(([username, jwt]) => (
                                <tr key={username}>
                                    <td>{username}</td>
                                    <td className="token-cell"><code>{jwt.substring(0, 50)}...</code></td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="2">No hay sesiones activas.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ActiveSessions;
