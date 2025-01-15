import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './pages/home/Home';
import LoginForm from './pages/login/Login';

function App() {
  return (
     <BrowserRouter>
      <Routes>
        <Route path="/home" element={<Home />} />
        <Route path="/" element={<LoginForm />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
