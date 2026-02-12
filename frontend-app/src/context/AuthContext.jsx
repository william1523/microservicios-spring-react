import React, { createContext, useContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
    };

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            try {
                const decoded = jwtDecode(token);
                setUser({
                    username: decoded.sub,
                    role: decoded.role,
                    companyCode: decoded.companyCode,
                    authenticated: true
                });
            } catch (error) {
                console.error('Error decoding token:', error);
                logout();
            }
        } else {
            localStorage.removeItem('token');
            setUser(null);
        }
        setLoading(false);
    }, [token]);

    const login = (newToken) => {
        setToken(newToken);
    };

    return (
        <AuthContext.Provider value={{ user, token, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
