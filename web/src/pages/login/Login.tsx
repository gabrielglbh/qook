import React, { useState } from 'react';
import Title from '../../components/Title';
import { registerUser, signInUser } from '../../services/auth/AuthService';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

function LoginForm() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isSignIn, changeSignMode] = useState(true);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        var uid: string | number = "";
        if (isSignIn) {
            uid = await signInUser(email, password);
        } else {
            uid = await registerUser(email, password);
        }
        if (typeof uid === 'string') {
            navigate('/home');
        } else {
            toast("Ha ocurrido un error. Inténtalo de nuevo.");
        }
    };

    const handleChangeSignMode = () => {
        changeSignMode((value) => !value);
    }

    return (
        <div className="content-center h-screen bg-orange-300">
            <form onSubmit={handleSubmit} className="bg-white shadow-md rounded py-12 px-6 mx-4 lg:container lg:max-w-md">
                <Title title="Qook"/>
                <div className="mb-6">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                        Email
                    </label>
                    <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" id="email" type="email" placeholder="Ingrese su email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className="mb-6">
                    <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">
                        Contraseña
                    </label>
                    <input className="shadow appearance-none border border-red-500 rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline" id="password" type="password" placeholder="******************" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <div className="flex items-center justify-between">
                    <button className="bg-orange-500 hover:bg-orange-700 text-white font-bold py-2 px-4 w-full rounded focus:outline-none focus:shadow-outline" type="submit">
                        {isSignIn ? "Entrar" : "Registrarse"}
                    </button>
                </div>
                <div className="text-center justify-between hover:cursor-pointer">
                    <div className='underline pt-4' onClick={handleChangeSignMode}>
                        {isSignIn ? "Registrarse" : "Iniciar sesión"}
                    </div>
                </div>
            </form>
        </div>
    );
}

export default LoginForm;