'use client';

import { motion, AnimatePresence } from 'framer-motion';
import { createContext, useContext, useState } from 'react';

interface Toast {
  message: string;
  type: 'success' | 'error';
}

interface ToastContextType {
  showToast: (message: string, type: 'success' | 'error') => void;
}

const ToastContext = createContext<ToastContextType | null>(null);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

export const ToastProvider = ({ children }: { children: React.ReactNode }) => {
  const [toast, setToast] = useState<Toast | null>(null);

  const showToast = (message: string, type: 'success' | 'error') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000); // Hide after 3 seconds
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <AnimatePresence>
        {toast && (
          <motion.div
            initial={{ opacity: 0, y: -50, x: '0%' }}
            animate={{ opacity: 1, y: 20, x: '0%' }}
            exit={{ opacity: 0, y: -50, x: '0%' }}
            className={`fixed top-0 right-8 z-50 px-6 py-3 rounded-lg shadow-lg
                      ${toast.type === 'success' ? 'bg-green-500' : 'bg-red-500'} text-white`}
          >
            {toast.message}
          </motion.div>
        )}
      </AnimatePresence>
    </ToastContext.Provider>
  );
};
