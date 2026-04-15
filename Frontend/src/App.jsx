import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './Assets/Context/AuthContext';
import AppRoutes from './Assets/Routes/AppRoutes';
import './index.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
