const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:5000';

interface FormData {
  name: string;
  phone: string;
  service: string;
  comment: string;
}

export const api = {
  async get(endpoint: string) {
    const response = await fetch(`${API_URL}${endpoint}`);
    return response.json();
  },

  async post(endpoint: string, data: FormData) {
    const response = await fetch(`${API_URL}${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return response.json();
  },
};

export const submitForm = async (formData: FormData): Promise<void> => {
  try {
    const response = await fetch('/api/submit', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    });

    if (!response.ok) {
      throw new Error('Failed to submit form');
    }
  } catch (error) {
    console.error('Error submitting form:', error);
    throw error;
  }
};
