import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';

export default function Appointments() {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const endpoint = user.role === 'DOCTOR'
        ? '/appointments/my-appointments/doctor'
        : '/appointments/my-appointments/patient';
      const response = await api.get(endpoint);
      setAppointments(response.data);
    } catch (err) {
      setError('Failed to load appointments.');
    }
  };

  const handleCancel = async (id) => {
    try {
      await api.put(`/appointments/${id}/cancel`);
      fetchAppointments();
    } catch (err) {
      setError('Failed to cancel appointment.');
    }
  };

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">My Appointments</h1>

      {error && (
        <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4">
          {error}
        </div>
      )}

      {appointments.length === 0 && (
        <p className="text-gray-500">No appointments yet.</p>
      )}

      <div className="space-y-4">
        {appointments.map((appt) => (
          <div key={appt.id} className="bg-white rounded-xl shadow p-5 border border-gray-100 flex justify-between items-center">
            <div>
              <p className="font-semibold text-gray-800">
                {user.role === 'DOCTOR' ? appt.patientName : appt.doctorName}
              </p>
              {user.role !== 'DOCTOR' && (
                <p className="text-blue-600 text-sm">{appt.doctorSpecialization}</p>
              )}
              <p className="text-gray-500 text-sm">
                {new Date(appt.appointmentDateTime).toLocaleString()}
              </p>
              {appt.reasonForVisit && (
                <p className="text-gray-500 text-sm">Reason: {appt.reasonForVisit}</p>
              )}
            </div>

            <div className="flex flex-col items-end gap-2">
              <span className={statusBadgeClass(appt.status)}>
                {appt.status}
              </span>
              {appt.status === 'BOOKED' && (
                <button
                  onClick={() => handleCancel(appt.id)}
                  className="text-sm text-red-600 hover:underline"
                >
                  Cancel
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function statusBadgeClass(status) {
  if (status === 'BOOKED') return 'bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-xs font-medium';
  if (status === 'COMPLETED') return 'bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-medium';
  return 'bg-red-100 text-red-700 px-3 py-1 rounded-full text-xs font-medium';
}