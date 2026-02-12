import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './ManagementPages.css';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const { user, token, loading: authLoading } = useAuth();

    useEffect(() => {
        const fetchUsers = async () => {
            if (authLoading) return;

            setError(null);

            if (!user || user.role !== 'SUPER_ADMIN') {
                setError('Acceso denegado. Se requieren permisos de Súper Administrador.');
                setLoading(false);
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/users', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Error al obtener usuarios');
                }

                const data = await response.json();
                setUsers(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
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

    return (
        <div className="container management-container">
            <h2>Usuarios Registrados</h2>
            <div className="table-wrapper glass">
                <table className="management-table">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Email</th>
                            <th>Rol</th>
                            <th>Empresa</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((u) => (
                            <tr key={u.id}>
                                <td>{u.username}</td>
                                <td>{u.email}</td>
                                <td><span className={`role-badge ${u.role}`}>{u.role}</span></td>
                                <td>{u.companyCode}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default UserList;
