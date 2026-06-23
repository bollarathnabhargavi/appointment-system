import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-blue-600 text-white px-6 py-4 flex justify-between items-center shadow-md">
      <Link to="/" className="font-bold text-lg">
        Smart Clinic
      </Link>

      <div className="flex gap-6 items-center text-sm font-medium">
        {user && (
          <>
            <Link to="/doctors" className="hover:underline">Doctors</Link>
            <Link to="/appointments" className="hover:underline">My Appointments</Link>
            <Link to="/analytics" className="hover:underline">Analytics</Link>
            <span className="text-blue-100">Hi, {user.name}</span>
            <button
              onClick={handleLogout}
              className="bg-white text-blue-600 px-3 py-1 rounded-lg font-semibold hover:bg-blue-100 transition"
            >
              Logout
            </button>
          </>
        )}
        {!user && (
          <>
            <Link to="/login" className="hover:underline">Login</Link>
            <Link to="/register" className="hover:underline">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}