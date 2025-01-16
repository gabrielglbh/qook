import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Home from './pages/home/Home';
import LoginForm from './pages/login/Login';
import { useEffect, useState } from 'react';
import { onAuthStateChanged } from 'firebase/auth';
import { auth } from './services/firebase.config';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false); 

  useEffect(() => {
      onAuthStateChanged(auth, (user) => {
          setIsAuthenticated(user != null)
      })
  }, [])

  return (
     <BrowserRouter>
      <Routes>
       <Route 
        path="/" 
        element={ 
          isAuthenticated ? <Home /> : <Navigate to="/login" /> 
        } 
      />
      <Route 
        path="/login" 
        element={ 
          !isAuthenticated ? <LoginForm /> : <Navigate to="/" /> 
        } 
      />
      </Routes>
    </BrowserRouter>
  );
}

export default App
