import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

export default function Doctors() {
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [dateTime, setDateTime] = useState('');
  const [reason, setReason] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const response = await api.get('/doctors');
      setDoctors(response.data);
    } catch (err) {
      console.error('Failed to load doctors', err);
    }
  };

  const handleBook = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await api.post('/appointments/book', {
        doctorId: selectedDoctor.id,
        appointmentDateTime: dateTime,
        reasonForVisit: reason,
      });
      setMessage(`Appointment booked with ${selectedDoctor.name}!`);
      setSelectedDoctor(null);
      setDateTime('');
      setReason('');
    } catch (err) {
      setError(err.response?.data?.error || 'Booking failed. Please try again.');
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">Available Doctors</h1>

      {message && (
        <div className="bg-green-100 text-green-700 px-4 py-2 rounded mb-4">
          {message}
        </div>
      )}

      <div className="grid md:grid-cols-2 gap-4">
        {doctors.map((doc) => (
          <div key={doc.id} className="bg-white rounded-xl shadow p-5 border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-800">{doc.name}</h2>
            <p className="text-blue-600 font-medium">{doc.specialization}</p>
            <p className="text-gray-500 text-sm">{doc.qualification} • {doc.experienceYears} yrs experience</p>
            <p className="text-gray-500 text-sm mb-3">Fee: ₹{doc.consultationFee}</p>
            <button
              onClick={() => setSelectedDoctor(doc)}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition"
            >
              Book Appointment
            </button>
          </div>
        ))}
      </div>

      {selectedDoctor && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-lg font-bold mb-4">
              Book with {selectedDoctor.name}
            </h2>

            {error && (
              <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-sm">
                {error}
              </div>
            )}

            <form onSubmit={handleBook} className="space-y-4">
              <input
                type="datetime-local"
                value={dateTime}
                onChange={(e) => setDateTime(e.target.value)}
                required
                className="w-full border border-gray-300 rounded-lg px-4 py-2"
              />
              <input
                type="text"
                placeholder="Reason for visit"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2"
              />
              <div className="flex gap-3">
                <button
                  type="submit"
                  className="flex-1 bg-blue-600 text-white rounded-lg py-2 font-medium hover:bg-blue-700 transition"
                >
                  Confirm Booking
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedDoctor(null)}
                  className="flex-1 bg-gray-200 text-gray-700 rounded-lg py-2 font-medium hover:bg-gray-300 transition"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}