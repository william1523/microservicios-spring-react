import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import './AuthPages.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();

    const encryptData = async (data, publicKeyBase64) => {
        const binaryDerString = window.atob(publicKeyBase64);
        const binaryDer = new Uint8Array(binaryDerString.length);
        for (let i = 0; i < binaryDerString.length; i++) {
            binaryDer[i] = binaryDerString.charCodeAt(i);
        }

        const publicKey = await window.crypto.subtle.importKey(
            "spki",
            binaryDer.buffer,
            {
                name: "RSA-OAEP",
                hash: "SHA-256",
            },
            true,
            ["encrypt"]
        );

        const encodedData = new TextEncoder().encode(data);
        const encryptedBuffer = await window.crypto.subtle.encrypt(
            {
                name: "RSA-OAEP"
            },
            publicKey,
            encodedData
        );

        return window.btoa(String.fromCharCode.apply(null, new Uint8Array(encryptedBuffer)));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            console.log("Iniciando login con cifrado...");
            // 1. Fetch public key
            const keyResponse = await fetch('http://localhost:8080/auth/public-key');
            if (!keyResponse.ok) throw new Error('Error al obtener la clave pública');
            const publicKeyBase64 = await keyResponse.text();
            console.log("Clave pública recibida:", publicKeyBase64);

            // 2. Encrypt credentials
            const encryptedUsername = await encryptData(username, publicKeyBase64);
            const encryptedPassword = await encryptData(password, publicKeyBase64);
            console.log("Datos cifrados listos");

            // 3. Send encrypted credentials
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: encryptedUsername,
                    password: encryptedPassword
                }),
            });

            if (!response.ok) {
                throw new Error('Credenciales inválidas');
            }

            const data = await response.json();
            login(data.token);
            window.location.href = '/';
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card glass">
                <h2>Bienvenido</h2>
                <p>Inicia sesión para continuar</p>
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Nombre de Usuario</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            placeholder="Usuario"
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
                    <button type="submit" className="auth-btn" disabled={loading}>
                        {loading ? 'Ingresando...' : 'Ingresar'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;
