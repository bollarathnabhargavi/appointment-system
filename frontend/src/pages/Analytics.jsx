import { useState, useEffect } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend
} from 'recharts';
import api from '../api/axiosConfig';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export default function Analytics() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const response = await api.get('/analytics');
      setData(response.data);
    } catch (err) {
      setError('Failed to load analytics.');
    }
  };

  if (error) return <p className="p-8 text-red-600">{error}</p>;
  if (!data) return <p className="p-8 text-gray-500">Loading analytics...</p>;

  const specializationData = Object.entries(data.appointmentsBySpecialization || {}).map(
    ([name, value]) => ({ name, value })
  );

  const dateData = Object.entries(data.appointmentsByDate || {}).map(
    ([date, count]) => ({ date, count })
  );

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">Analytics Dashboard</h1>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <StatCard label="Total Appointments" value={data.totalAppointments} color="bg-blue-100 text-blue-700" />
        <StatCard label="Booked" value={data.bookedCount} color="bg-yellow-100 text-yellow-700" />
        <StatCard label="Completed" value={data.completedCount} color="bg-green-100 text-green-700" />
        <StatCard label="Cancellation Rate" value={`${data.cancellationRate}%`} color="bg-red-100 text-red-700" />
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow p-5">
          <h2 className="font-semibold text-gray-700 mb-4">Appointments by Specialization</h2>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={specializationData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={80}
                label
              >
                {specializationData.map((_, index) => (
                  <Cell key={index} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-xl shadow p-5">
          <h2 className="font-semibold text-gray-700 mb-4">Appointments by Date</h2>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={dateData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="count" fill="#3b82f6" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, value, color }) {
  return (
    <div className={`rounded-xl p-4 ${color}`}>
      <p className="text-sm font-medium">{label}</p>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}